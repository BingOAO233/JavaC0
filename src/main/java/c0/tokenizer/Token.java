package c0.tokenizer;

import c0.util.Position;

import java.util.Objects;

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

    public void setTokenType(TokenType type)
    {
        tokenType = type;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object val)
    {
        value = val;
    }

    public Position getStartPos()
    {
        return startPos;
    }

    public void setStartPos(Position srt)
    {
        startPos = srt;
    }

    public Position getEndPos()
    {
        return endPos;
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
}
