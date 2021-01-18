package JavaC0.tokenizer.token;

import JavaC0.error.AnalyseError;
import JavaC0.error.ErrorCode;
import JavaC0.util.Position;
import JavaC0.util.program.Span;
import JavaC0.util.program.structure.operator.BinaryOperator;

import java.util.Objects;
import java.util.Optional;

public class Token
{
    private TokenType tokenType;
    private Object value;
    private Position startPos;
    private Position endPos;

    public Token(TokenType type, Object val, Position srt, Position end)
    {
        tokenType = type;
        value = val;
        startPos = srt;
        endPos = end;
    }

    public Token(Token token)
    {
        this.tokenType = token.tokenType;
        this.value = token.value;
        this.startPos = token.startPos;
        this.endPos = token.endPos;
    }

    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Token token = (Token) o;
        return tokenType == token.tokenType
                && Objects.equals(value, token.value)
                && Objects.equals(startPos, token.startPos)
                && Objects.equals(endPos, token.endPos);
    }

    public int hashCode()
    {
        return Objects.hash(tokenType, value, startPos, endPos);
    }

    public String getValueString()
    {
        if (value instanceof Integer
                || value instanceof Double
                || value instanceof Character
                || value instanceof String)
            return value.toString();
        throw new Error("No suitable cast for token value.");
    }

    /**
     * getters & setters
     *
     * @return
     */
    public TokenType getTokenType()
    {
        return tokenType;
    }

    public Object getValue()
    {
        return value;
    }

    public Position getStartPos()
    {
        return startPos;
    }

    public Position getEndPos()
    {
        return endPos;
    }

    public Span getSpan()
    {
        return new Span(getStartPos(), getEndPos());
    }

    public void setTokenType(TokenType type)
    {
        tokenType = type;
    }

    public void setValue(Object val)
    {
        value = val;
    }

    public void setStartPos(Position srt)
    {
        startPos = srt;
    }

    public void setEndPos(Position end)
    {
        endPos = end;
    }

    /**
     * toString
     */
    @Override
    public String toString()
    {
        return String.format(
                "Token:\n[\n\tType: %s\n\tValue: %s\n\tPosition: (%s,%s)\n]\n",
                tokenType, value, startPos, endPos
        );
    }

    public String info()
    {
        return String.format("Token [%s] with value of [%s] at [%s]", tokenType, value, startPos);
    }

    /**
     * binary_operator -> '+' | '-' | '*' | '/' | '==' | '!=' | '<' | '>' | '<=' | '>='
     *
     * @return
     */
    public boolean isBinaryOp()
    {
        return tokenType == TokenType.PLUS ||
                tokenType == TokenType.MINUS ||
                tokenType == TokenType.MUL ||
                tokenType == TokenType.DIV ||
                tokenType == TokenType.ASSIGN ||
                tokenType == TokenType.EQ ||
                tokenType == TokenType.NEQ ||
                tokenType == TokenType.LT ||
                tokenType == TokenType.GT ||
                tokenType == TokenType.LE ||
                tokenType == TokenType.GE;
    }

    public int precedence() throws AnalyseError
    {
        switch (tokenType)
        {
            case PLUS:
            case MINUS:
                return 10;
            case MUL:
            case DIV:
                return 20;
            case ASSIGN:
                return 1;
            case EQ:
            case NEQ:
            case LT:
            case GT:
            case LE:
            case GE:
                return 2;
            default:
                throw new AnalyseError(ErrorCode.NotABinaryOperator, startPos);
        }
    }

    public boolean isLeftAssoc() throws AnalyseError
    {
        switch (tokenType)
        {
            case PLUS:
            case MINUS:
            case MUL:
            case DIV:
            case EQ:
            case NEQ:
            case LT:
            case GT:
            case LE:
            case GE:
                return true;
            case ASSIGN:
                return false;
            default:
                throw new AnalyseError(ErrorCode.NotABinaryOperator, startPos);
        }
    }

    public Optional<BinaryOperator> toBinaryOp()
    {
        switch (tokenType)
        {
            case PLUS:
                return Optional.of(BinaryOperator.Add);
            case MINUS:
                return Optional.of(BinaryOperator.Sub);
            case MUL:
                return Optional.of(BinaryOperator.Mul);
            case DIV:
                return Optional.of(BinaryOperator.Div);
            case EQ:
                return Optional.of(BinaryOperator.Eq);
            case NEQ:
                return Optional.of(BinaryOperator.Neq);
            case LT:
                return Optional.of(BinaryOperator.Lt);
            case LE:
                return Optional.of(BinaryOperator.Le);
            case GT:
                return Optional.of(BinaryOperator.Gt);
            case GE:
                return Optional.of(BinaryOperator.Ge);
            default:
                return Optional.empty();
        }
    }
}
