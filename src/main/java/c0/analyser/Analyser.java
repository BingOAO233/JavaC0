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
import c0.util.program.structure.expression.Expression;
import c0.util.program.structure.statement.*;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

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

    /**
     * token(IDENT) -> TypeDefine
     *
     * @return TypeDefine structure
     * @throws CompileError
     */
    private TypeDefine analyseType() throws CompileError
    {
        Token typeToken = expect(TokenType.IDENT);
        return new TypeDefine(typeToken.getSpan(), (String) typeToken.getValue(), null);
    }

    /**
     * token(IDENT) -> Ident
     *
     * @return Ident structure
     * @throws CompileError error
     */
    private Ident analyseIdent() throws CompileError
    {
        // IDENT
        var token = expect(TokenType.IDENT);
        return new Ident(token.getSpan(), (String) token.getValue());
    }

    /**
     * program -> item*
     * Item -> function | decl_stmt
     * decl_stmt -> let_decl_stmt | const_decl_stmt
     *
     * @return Program structure
     * @throws CompileError error
     */
    private Program analyseProgram() throws CompileError
    {
        ArrayList<FunctionStatement> functions = new ArrayList<>();
        ArrayList<DeclareStatement> declares = new ArrayList<>();

        // Item*
        var allowKey = new TokenType[]{TokenType.FN_KW, TokenType.CONST_KW, TokenType.LET_KW};
        while (check(allowKey))
        {
            if (check(TokenType.FN_KW))
            {
                // function
                var func = analyseFunction();
                functions.add(func);
            }
            else if (check(TokenType.CONST_KW))
            {
                // const_decl_stmt
                var decl = analyseConstDeclaration();
                declares.add(decl);
            }
            else
            {
                // let_decl_stmt
                var decl = analyseLetDeclaration();
                declares.add(decl);
            }
        }
        return new Program(declares, functions);
    }

    /**
     * function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
     *
     * @return FunctionStatement structure
     * @throws CompileError error
     */
    private FunctionStatement analyseFunction() throws CompileError
    {
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

    /**
     * function_param_list -> function_param (',' function_param)*
     *
     * @return List of FunctionParam
     * @throws CompileError error
     */
    private ArrayList<FunctionParam> analyseFunctionParamList() throws CompileError
    {
        ArrayList<FunctionParam> params = new ArrayList<>();
        while (true)
        {
            // function_param
            var param = analyseFunctionParam();
            params.add(param);
            // ','
            if (!check(TokenType.COMMA))
                break;
            next();
        }

        return params;
    }

    /**
     * function_param -> 'const'? IDENT ':' ty
     *
     * @return FunctionParam structure
     * @throws CompileError error
     */
    private FunctionParam analyseFunctionParam() throws CompileError
    {
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
        var allowType = new TokenType[]{TokenType.INT64, TokenType.DOUBLE};
        if (!check(allowType))
        {
            throw new UnexpectedTokenError(allowType, peek());
        }
        TypeDefine paramType = analyseType();

        return new FunctionParam(isConst, paramName, paramType);
    }

    /**
     * block_stmt -> '{' stmt* '}'
     *
     * @return BlockStatement structure
     * @throws CompileError error
     */
    private BlockStatement analyseBlockStatement() throws CompileError
    {
        // '{'
        var srtSpan = expect(TokenType.L_BRACE).getSpan();

        // stmt*
        ArrayList<Statement> stmts = new ArrayList<>();
        while (!check(TokenType.R_BRACE))
        {
            var stmt = analyseStatement();
            stmts.add(stmt);
        }
        // '}'
        var endSpan = expect(TokenType.R_BRACE).getSpan();

        return new BlockStatement(Span.add(srtSpan, endSpan), stmts);
    }

    /**
     * stmt ->
     * expr_stmt | decl_stmt | if_stmt
     * | while_stmt | break_stmt | continue_stmt
     * | return_stmt | block_stmt | empty_stmt
     *
     * @return Statement
     * @throws CompileError error
     */
    private Statement analyseStatement() throws CompileError
    {

        if (check(TokenType.CONST_KW))
        {
            // const_decl_stmt
            return analyseConstDeclaration();
        }
        else if (check(TokenType.LET_KW))
        {
            // let_decl_stmt
            return analyseLetDeclaration();
        }
        else if (check(TokenType.L_BRACE))
        {
            // block_stmt
            return analyseBlockStatement();
        }
        else if (check(TokenType.IF_KW))
        {
            // if_stmt
            return analyseIfStatement();
        }
        else if (check(TokenType.WHILE_KW))
        {
            // while_stmt
            return analyseWhileStatement();
        }
        else if (check(TokenType.BREAK_KW))
        {
            // break_stmt
            return analyseBreakStatement();
        }
        else if (check(TokenType.CONTINUE_KW))
        {
            // continue_stmt
            return analyseContinueStatement();
        }
        else if (check(TokenType.RETURN_KW))
        {
            // return_stmt
            return analyseReturnStatement();
        }
        else if (check(TokenType.SEMICOLON))
        {
            // empty_stmt
            return analyseEmptyStatement();
        }
        else
        {
            // expression stmt
            return analyseExpressionStatement();
        }
    }

    private IfStatement analyseIfStatement()
    {

    }

    private WhileStatement analyseWhileStatement()
    {

    }

    private BreakStatement analyseBreakStatement()
    {

    }

    private ReturnStatement analyseReturnStatement()
    {

    }

    private ContinueStatement analyseContinueStatement()
    {

    }

    /**
     * empty_stmt -> ';'
     *
     * @return EmptyStatement structure
     * @throws CompileError error
     */
    private EmptyStatement analyseEmptyStatement() throws CompileError
    {
        // ';'
        var span = expect(TokenType.SEMICOLON).getSpan();

        return new EmptyStatement(span);
    }

    private ExpressionStatement analyseExpressionStatement() throws CompileError
    {
        // expr_stmt -> expr ';'
        var expr = analyseExpression();
        if (!check(TokenType.SEMICOLON))
        {
            throw new AnalyseError(ErrorCode.NoSemicolonAfterStatement, peek().getStartPos());
        }
        var semiSpan = expect(TokenType.SEMICOLON).getSpan();
        return new ExpressionStatement(Span.add(expr.getSpan(), semiSpan), expr);
    }

    private Expression analyseExpression() throws TokenizeError
    {
        /*
            expr ->
                  operator_expr
                | negate_expr   -
                | as_expr
                | literal_expr  " ' digit
                | ident_expr    IDENT
                | assign_expr   IDENT
                | call_expr     IDENT
                | group_expr    (
         */

    }

    private void analyseNegativeExpression()
    {

    }

    private void analyseAssignCallIdentExpression()
    {

    }

    /**
     * const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
     *
     * @return DeclareStatement structure
     * @throws CompileError error
     */
    private DeclareStatement analyseConstDeclaration() throws CompileError
    {
        // 'let'
        var srtSpan = expect(TokenType.LET_KW).getSpan();
        // IDENT
        var ident = analyseIdent();
        // ':'
        expect(TokenType.COLON);
        // ty
        var type = analyseType();
        // '='
        expect(TokenType.ASSIGN);
        // expr
        var value = analyseExpression();
        // ';'
        var endSpan = expect(TokenType.SEMICOLON).getSpan();

        return new DeclareStatement(Span.add(srtSpan, endSpan), true, ident, type, Optional.of(value));
    }

    /**
     * let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
     *
     * @return DeclareStatement structure
     * @throws CompileError error
     */
    private DeclareStatement analyseLetDeclaration() throws CompileError
    {
        // 'let'
        var srtSpan = expect(TokenType.LET_KW).getSpan();
        // IDENT
        var ident = analyseIdent();
        // ':'
        expect(TokenType.COLON);
        // ty
        var type = analyseType();
        // ('=' expr)?
        Expression value = null;
        if (check(TokenType.ASSIGN))
        {
            // '='
            expect(TokenType.ASSIGN);
            // expr
            value = analyseExpression();
        }
        // ';'
        var endSpan = expect(TokenType.SEMICOLON).getSpan();

        return new DeclareStatement(Span.add(srtSpan, endSpan), false, ident, type, Optional.ofNullable(value));
    }

}
