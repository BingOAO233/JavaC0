package c0.analyser;

import c0.error.AnalyseError;
import c0.error.*;
import c0.instruction.Instruction;
import c0.tokenizer.Token;
import c0.tokenizer.TokenType;
import c0.tokenizer.Tokenizer;
import c0.util.Position;
import c0.util.program.Span;
import c0.util.program.structure.Ident;
import c0.util.program.structure.Program;
import c0.util.program.structure.TypeDefine;
import c0.util.program.structure.statement.*;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Analyser
{
    Tokenizer tokenizer;
    ArrayList<Instruction> instructions;

    Token peekedToken = null;

    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();

    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer)
    {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
    }

    public Program analyse() throws CompileError
    {
        return analyseProgram();

    }

    private Token peek() throws TokenizeError
    {
        if (peekedToken == null)
        {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    private Token next() throws TokenizeError
    {
        if (peekedToken != null)
        {
            var token = peekedToken;
            peekedToken = null;
            return token;
        }
        else
        {
            return tokenizer.nextToken();
        }
    }

    private boolean check(TokenType tt) throws TokenizeError
    {
        var token = peek();
        return token.getTokenType() == tt;
    }

    private boolean check(TokenType[] tts) throws TokenizeError
    {
        var token = peek();
        for (var tt : tts)
        {
            if (check(tt))
                return true;
        }
        return false;
    }

    private Token nextIf(TokenType tt) throws TokenizeError
    {
        var token = peek();
        if (token.getTokenType() == tt)
        {
            return next();
        }
        return null;

    }

    private Token expect(TokenType tt) throws CompileError
    {
        var token = peek();
        if (token.getTokenType() == tt)
        {
            return next();
        }
        throw new UnexpectedTokenError(tt, token);

    }

    private int getNextVariableOffset()
    {
        return this.nextOffset++;
    }

    private void addSymbol(Token token, boolean isInitialized, boolean isConstant, Position curPos) throws AnalyseError
    {
        String tokenName = (String) token.getValue();
        if (this.symbolTable.get(tokenName) != null)
        {
            throw new AnalyseError(ErrorCode.DuplicatedDeclaration, curPos);
        }
        else
        {
            this.symbolTable.put(tokenName, new SymbolEntry(token.getTokenType(), isConstant, isInitialized, getNextVariableOffset()));
        }
    }

    private void initializeSymbol(String name, Position curPos) throws AnalyseError
    {
        SymbolEntry entry = this.symbolTable.get(name);
        if (entry == null)
        {
            throw new AnalyseError(ErrorCode.NoDeclaration, curPos);
        }
        else
        {
            entry.setInitialized(true);
        }
    }

    private int getOffset(String name, Position curPos) throws AnalyseError
    {
        SymbolEntry entry = this.symbolTable.get(name);
        if (entry == null)
        {
            throw new AnalyseError(ErrorCode.NoDeclaration, curPos);
        }
        else
        {
            return entry.getStackOffset();
        }
    }

    private boolean isConstant(String name, Position curPos) throws AnalyseError
    {
        SymbolEntry entry = this.symbolTable.get(name);
        if (entry == null)
        {
            throw new AnalyseError(ErrorCode.NoDeclaration, curPos);
        }
        else
        {
            return entry.isConstant();
        }
    }

    private TypeDefine analyseType() throws CompileError
    {
        Token typeToken = expect(TokenType.IDENT);
        return new TypeDefine(typeToken.getSpan(), (String) typeToken.getValue(), null);
    }

    private Ident analyseIdent() throws CompileError
    {
        var token = expect(TokenType.IDENT);
        return new Ident(token.getSpan(), (String) token.getValue());
    }

    private Program analyseProgram() throws CompileError
    {
        ArrayList<FunctionStatement> functions = new ArrayList<>();
        ArrayList<DeclareStatement> declares = new ArrayList<>();
        // program -> item*
        // Item -> function | decl_stmt
        // decl_stmt -> let_decl_stmt | const_decl_stmt
        while (check(TokenType.FN_KW)
                || check(TokenType.CONST_KW)
                || check(TokenType.LET_KW))
        {
            if (check(TokenType.FN_KW))
            {
                var func = analyseFunction();
                functions.add(func);
            }
            else if (check(TokenType.CONST_KW))
            {
                var decl = analyseConstDeclaration();
                declares.add(decl);
            }
            else if (check(TokenType.LET_KW))
            {
                var decl = analyseLetDeclaration();
                declares.add(decl);
            }
            else
            {
                throw new UnexpectedTokenError(new TokenType[]{TokenType.FN_KW, TokenType.CONST_KW, TokenType.LET_KW}, peek());
            }
        }
        return new Program(declares, functions);
    }

    private FunctionStatement analyseFunction() throws CompileError
    {
        // function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
        // 'fn'
        var srtSpan = expect(TokenType.FN_KW).getSpan();
        // IDENT
        var funcName = analyseIdent();
        // '('
        expect(TokenType.L_PAREN);
        // function_param_list
        ArrayList<FunctionParam> params = null;
        if (!check(TokenType.R_PAREN))
        {
            params = analyseFunctionParamList();
        }
        // ')'
        expect(TokenType.R_PAREN);
        // '->'
        expect(TokenType.ARROW);
        // ty
        var allowType = new TokenType[]{TokenType.INT64, TokenType.DOUBLE, TokenType.VOID};
        if (!check(allowType))
        {
            throw new UnexpectedTokenError(allowType, peek());
        }
        TypeDefine returnType = analyseType();
        // block_stmt
        BlockStatement body = analyseBlockStatement();

        return new FunctionStatement(Span.add(srtSpan, body.getSpan()), funcName, params, returnType, body);
    }

    private ArrayList<FunctionParam> analyseFunctionParamList() throws CompileError
    {
        // function_param_list -> function_param (',' function_param)*
        ArrayList<FunctionParam> params = new ArrayList<>();

        while (true)
        {
            var param = analyseFunctionParam();
            params.add(param);
            if (!check(TokenType.COMMA))
                break;
            next();
        }

        return params;
    }

    private FunctionParam analyseFunctionParam() throws CompileError
    {
        // function_param -> 'const'? IDENT ':' ty
        boolean isConst = false;
        // 'const'?
        if (check(TokenType.CONST_KW))
        {
            isConst = true;
        }
        // IDENT
        Ident paramName = analyseIdent();
        // :
        expect(TokenType.COMMA);
        // ty
        if (!(check(TokenType.INT64) || check(TokenType.DOUBLE)))
        {
            throw new UnexpectedTokenError(new TokenType[]{TokenType.INT64, TokenType.DOUBLE}, peek());
        }
        TypeDefine paramType = analyseType();
        // TODO: function param table
        return new FunctionParam(isConst, paramName, paramType);
    }

    private BlockStatement analyseBlockStatement() throws CompileError
    {
        // block_stmt -> '{' stmt* '}'
        var srtSpan = expect(TokenType.L_BRACE).getSpan();
        ArrayList<Statement> stmts = new ArrayList<>();
        while (!check(TokenType.R_BRACE))
        {
            var stmt = analyseStatement();
            stmts.add(stmt);
        }
        var endSpan = expect(TokenType.R_BRACE).getSpan();
        return new BlockStatement(Span.add(srtSpan, endSpan), stmts);
    }

    private Statement analyseStatement() throws TokenizeError
    {
        /*
            stmt ->
                  expr_stmt
                | decl_stmt
                | if_stmt
                | while_stmt
                | break_stmt
                | continue_stmt
                | return_stmt
                | block_stmt
                | empty_stmt
         */
        if (check(TokenType.CONST_KW))
        {
            return analyseConstDeclaration();
        }
    }

    private void analyseExpressionStatement() throws CompileError
    {
        // expr_stmt -> expr ';'
        analyseExpression();
        if (!check(TokenType.SEMICOLON))
        {
            throw new AnalyseError(ErrorCode.NoSemicolonAfterStatement, peek().getStartPos());
        }
        next();
    }

    private void analyseExpression() throws TokenizeError
    {
        /*
            expr ->
                  operator_expr
                | negate_expr   -
                | assign_expr   IDENT
                | as_expr
                | call_expr     IDENT
                | literal_expr  " ' digit
                | ident_expr    IDENT
                | group_expr    (
         */
        if (check(TokenType.MINUS))
        {
            analyseNegativeExpression();
        }
        else if (check(TokenType.IDENT))
        {
            analyseAssignCallIdentExpression();
        }

    }

    private void analyseNegativeExpression()
    {

    }

    private void analyseAssignCallIdentExpression()
    {

    }

    private DeclareStatement analyseConstDeclaration()
    {

    }

    private DeclareStatement analyseLetDeclaration()
    {

    }

}
