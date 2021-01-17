package c0.error;

import c0.util.Position;

public class TokenizeError extends C0Error
{
    protected Position position;

    public TokenizeError(ErrorCode errorCode, Position position)
    {
        super(errorCode);
        this.position = position;
    }

    public TokenizeError(ErrorCode err, Integer row, Integer col)
    {
        super(err);
        this.position = new Position(row, col);
    }

    @Override
    public ErrorCode getErr()
    {
        return errorCode;
    }

    public Position getPos()
    {
        return position;
    }

    @Override
    public String toString()
    {
        return String.format("Tokenize Error: %s, at: %s", errorCode, position);
    }
}
