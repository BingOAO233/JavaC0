package c0.error;

import c0.util.Position;

public class TokenizeError extends CompileError
{
    private ErrorCode errorCode;
    private Position position;

    public TokenizeError(ErrorCode err, Position pos)
    {
        super();
        errorCode = err;
        position = pos;
    }

    public TokenizeError(ErrorCode err, Integer row, Integer col)
    {
        super();
        errorCode = err;
        position = new Position(row, col);
    }

    @Override
    public ErrorCode getErr()
    {
        return errorCode;
    }

    @Override
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
