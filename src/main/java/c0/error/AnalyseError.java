package c0.error;

import c0.util.Position;

public class AnalyseError extends CompileError
{
    private static final long serialVersionUID = 1L;

    ErrorCode errorCode;
    Position position;

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

    public AnalyseError(ErrorCode code, Position pos)
    {
        errorCode = code;
        position = pos;
    }

    @Override
    public String toString()
    {
        return String.format("Analyse Error: %s, at: %s", errorCode, position);
    }
}
