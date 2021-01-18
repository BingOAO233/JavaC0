package JavaC0.analyser;

import JavaC0.tokenizer.Tokenizer;
import JavaC0.error.AnalyseError;
import JavaC0.error.*;
import JavaC0.tokenizer.token.Token;
import JavaC0.tokenizer.token.TokenType;
import JavaC0.util.Position;
import JavaC0.util.program.Span;
import JavaC0.util.program.structure.Ident;
import JavaC0.util.program.structure.LiteralType;
import JavaC0.util.program.structure.Program;
import JavaC0.util.program.structure.TypeDefine;
import JavaC0.util.program.structure.expression.*;
import JavaC0.util.program.structure.operator.UnaryOperator;
import JavaC0.util.program.structure.statement.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

public class Analyser
{
    Tokenizer tokenizer;

    Token peekedToken = null;

    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();

    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer)
    {
        this.tokenizer = tokenizer;
    }

    public Program analyse() throws C0Error, CloneNotSupportedException
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

    private boolean check(TokenType tt, Token t)
    {
        return t.getTokenType() == tt;
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

    private Token expect(TokenType tt) throws C0Error
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
            this.symbolTable.put(tokenName, new SymbolEntry(token.getTokenType(), isConstant, isInitialized,
                                                            getNextVariableOffset()));
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
     * @throws C0Error
     */
    private TypeDefine analyseType() throws C0Error
    {
        Token typeToken = expect(TokenType.IDENT);
        return new TypeDefine(typeToken.getSpan(), (String) typeToken.getValue(), null);
    }

    /**
     * token(IDENT) -> Ident
     *
     * @return Ident structure
     * @throws C0Error error
     */
    private Ident analyseIdent() throws C0Error
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
     * @throws C0Error error
     */
    private Program analyseProgram() throws C0Error, CloneNotSupportedException
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
     * @throws C0Error error
     */
    private FunctionStatement analyseFunction() throws C0Error, CloneNotSupportedException
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
        if (!check(TokenType.IDENT))
        {
            throw new UnexpectedTokenError(TokenType.IDENT, peek());
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
     * @throws C0Error error
     */
    private ArrayList<FunctionParam> analyseFunctionParamList() throws C0Error
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
     * @throws C0Error error
     */
    private FunctionParam analyseFunctionParam() throws C0Error
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
        expect(TokenType.COLON);
        // ty
        if (!check(TokenType.IDENT))
        {
            throw new UnexpectedTokenError(TokenType.IDENT, peek());
        }
        TypeDefine paramType = analyseType();

        return new FunctionParam(isConst, paramName, paramType);
    }

    /**
     * block_stmt -> '{' stmt* '}'
     *
     * @return BlockStatement structure
     * @throws C0Error error
     */
    private BlockStatement analyseBlockStatement() throws C0Error, CloneNotSupportedException
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
     * @return Statement structure
     * @throws C0Error error
     */
    private Statement analyseStatement() throws C0Error, CloneNotSupportedException
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

    /**
     * empty_stmt -> ';'
     *
     * @return EmptyStatement structure
     * @throws C0Error error
     */
    private EmptyStatement analyseEmptyStatement() throws C0Error
    {
        // ';'
        var span = expect(TokenType.SEMICOLON).getSpan();

        return new EmptyStatement(span);
    }

    /**
     * if_stmt -> 'if' expr block_stmt ('else' 'if' expr block_stmt)* ('else' block_stmt)?
     *
     * @return IfStatement structure
     * @throws C0Error error
     */
    private IfStatement analyseIfStatement() throws C0Error, CloneNotSupportedException
    {
        // 'if'
        var span = expect(TokenType.IF_KW).getSpan();
        // expr
        var cond = analyseExpression();
        // block_stmt
        var ifBlock = analyseBlockStatement();

        span.addAssign(ifBlock.getSpan());
        IElseIfBlock elseBlock;
        if (check(TokenType.ELSE_KW))
        {
            // 'else'
            expect(TokenType.ELSE_KW);
            if (check(TokenType.IF_KW))
            {
                // 'if'
                var elseIf = analyseIfStatement();
                span.addAssign(elseIf.getSpan());
                elseBlock = elseIf;
            }
            else
            {
                // block_stmt
                var block = analyseBlockStatement();
                span.addAssign(block.getSpan());
                elseBlock = block;
            }
        }
        else
        {
            elseBlock = new EmptyStatement(new Span(span.endPos));
        }

        return new IfStatement(span, cond, ifBlock, elseBlock);
    }

    /**
     * while_stmt -> 'while' expr block_stmt
     *
     * @return WhileStatement structure
     * @throws C0Error error
     */
    private WhileStatement analyseWhileStatement() throws C0Error, CloneNotSupportedException
    {
        // 'while'
        expect(TokenType.WHILE_KW);
        // expr
        var cond = analyseExpression();
        // block_stmt
        var body = analyseBlockStatement();

        return new WhileStatement(Span.add(cond.getSpan(), body.getSpan()), cond, body);
    }

    /**
     * break_stmt -> 'break' ';'
     *
     * @return BreakStatement structure
     * @throws C0Error error
     */
    private BreakStatement analyseBreakStatement() throws C0Error
    {
        // 'break'
        var span = expect(TokenType.BREAK_KW).getSpan();
        // ';'
        expect(TokenType.SEMICOLON);
        return new BreakStatement(span);
    }

    /**
     * return_stmt -> 'return' expr? ';'
     *
     * @return ReturnStatement structure
     * @throws C0Error error
     */
    private ReturnStatement analyseReturnStatement() throws C0Error, CloneNotSupportedException
    {
        // 'return'
        var srtSpan = expect(TokenType.RETURN_KW).getSpan();
        // expr
        Expression retVal = null;
        if (!check(TokenType.SEMICOLON))
        {
            retVal = analyseExpression();
        }
        // ';'
        var endSpan = expect(TokenType.SEMICOLON).getSpan();

        return new ReturnStatement(Span.add(srtSpan, endSpan), Optional.ofNullable(retVal));
    }

    /**
     * continue_stmt -> 'continue' ';'
     *
     * @return ContinueStatement structure
     * @throws C0Error error
     */
    private ContinueStatement analyseContinueStatement() throws C0Error
    {
        // 'continue'
        var span = expect(TokenType.CONTINUE_KW).getSpan();
        // ';'
        expect(TokenType.SEMICOLON);
        return new ContinueStatement(span);
    }

    /**
     * const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
     *
     * @return DeclareStatement structure
     * @throws C0Error error
     */
    private DeclareStatement analyseConstDeclaration() throws C0Error, CloneNotSupportedException
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
     * @throws C0Error error
     */
    private DeclareStatement analyseLetDeclaration() throws C0Error, CloneNotSupportedException
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

    /**
     * expr_stmt -> expr ';'
     *
     * @return ExpressionStatement structure
     * @throws C0Error error
     */
    private ExpressionStatement analyseExpressionStatement() throws C0Error, CloneNotSupportedException
    {
        // expr
        var expr = analyseExpression();
        // ';'
        var semiSpan = expect(TokenType.SEMICOLON).getSpan();

        return new ExpressionStatement(Span.add(expr.getSpan(), semiSpan), expr);
    }

    /**
     * Expr -> unary_expr opg
     *
     * @return Expression structure
     * @throws TokenizeError error
     */
    private Expression analyseExpression() throws C0Error, CloneNotSupportedException
    {
        // unary_expr
        var lhs = analyseUnaryExpression();
        // opg
        return analyseOPG(lhs, 0);
    }

    /**
     * Item -> Ident | FunctionCall | Literal | '(' Expr ')'
     *
     * @return Expression structure
     * @throws C0Error error
     */
    private Expression analyseItem() throws C0Error, CloneNotSupportedException
    {
        if (check(TokenType.IDENT))
        {
            Token identToken = next();
            Ident ident = new Ident(identToken.getSpan(), (String) identToken.getValue());
            if (check(TokenType.L_PAREN))
            {
                return analyseFunctionCall(ident);
            }
            else
            {
                return new IdentExpression(ident);
            }
        }
        else if (check(TokenType.INT64) || check(TokenType.CHAR))
        {
            Token literal = next();
            return new LiteralExpression(literal.getSpan(), LiteralType.INT64,
                                         literal.getValue());
        }
        else if (check(TokenType.STR))
        {
            Token literal = next();
            return new LiteralExpression(literal.getSpan(), LiteralType.STRING,
                                         literal.getValue());
        }
        else if (check(TokenType.L_PAREN))
        {
            expect(TokenType.L_PAREN);
            var expr = analyseExpression();
            expect(TokenType.R_PAREN);
            return expr;
        }
        else
        {
            throw new AnalyseError(ErrorCode.UnexpectedPattern, peek().getStartPos());
        }

    }

    /**
     * UExpr -> PreUOp* Item ProUOp*
     * PreUOp -> '+' | '-'
     * ProUOp -> 'as' TypeDef
     *
     * @return Expression structure
     * @throws C0Error error
     */
    private Expression analyseUnaryExpression() throws C0Error, CloneNotSupportedException
    {
        ArrayList<Token> precOps = new ArrayList<>();
        while (check(TokenType.MINUS))
        {
            precOps.add(next());
        }
        var item = analyseItem();
        Collections.reverse(precOps);
        for (var op : precOps)
        {
            UnaryOperator uop;
            if (check(TokenType.PLUS, op))
            {
                uop = UnaryOperator.Pos;
            }
            else if (check(TokenType.MINUS, op))
            {
                uop = UnaryOperator.Neg;
            }
            else
            {
                throw new AnalyseError(ErrorCode.UnexpectedOperator, op.getStartPos());
            }
            item = new UnaryExpression(Span.add(item.getSpan(), op.getSpan()), uop, item.clone());
        }

        while (check(TokenType.AS_KW))
        {
            next();
            var type = analyseType();
            item = new AsExpression(Span.add(type.span, item.getSpan()), item.clone(), type);
        }

        return item;
    }

    // TODO: clone?

    /**
     * FunctionCall -> Ident '(' (Expr (,Expr)* )? ')'
     *
     * @param func Ident func
     * @return CallExpression structure
     * @throws C0Error error
     */
    private CallExpression analyseFunctionCall(Ident func) throws C0Error, CloneNotSupportedException
    {
        expect(TokenType.L_PAREN);
        ArrayList<Expression> params = new ArrayList<>();
        if (!check(TokenType.R_PAREN))
        {
            while (true)
            {
                var expr = analyseExpression();
                params.add(expr);
                if (!check(TokenType.COMMA))
                    break;
                next();
            }
        }
        var rSpan = expect(TokenType.R_PAREN).getSpan();

        return new CallExpression(Span.add(func.span, rSpan), func, params);
    }

    private Expression analyseOPG(Expression lhs, int precedence) throws C0Error, CloneNotSupportedException
    {
        while (peek().isBinaryOp() && peek().precedence() >= precedence)
        {
            // OPG
            var op = next();
            var rhs = analyseUnaryExpression();

            while (peek().isBinaryOp()
                    && ((peek().precedence() > op.precedence() && peek().isLeftAssoc())
                    || (peek().precedence() == op.precedence() && !peek().isLeftAssoc())))
            {
                var operator = peek();
                int opPrecedence = operator.precedence();
                rhs = analyseOPG(rhs, opPrecedence);
            }

            lhs = combineExpression(lhs, rhs, op);
        }
        return lhs;
    }

    private Expression combineExpression(Expression lhs, Expression rhs, Token op) throws CloneNotSupportedException
    {
        switch (op.getTokenType())
        {
            case ASSIGN:
                var span = Span.add(lhs.getSpan(), rhs.getSpan());
                return new AssignExpression(span, false, lhs.clone(), rhs.clone());
            default:
                var operator = op.toBinaryOp().get();
                var s = Span.add(lhs.getSpan(), rhs.getSpan());
                return new BinaryExpression(s, operator, lhs.clone(), rhs.clone());
        }
    }

}
