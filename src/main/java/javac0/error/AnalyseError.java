package javac0.error;

import javac0.util.Position;

public class AnalyseError extends C0Error
{
    private static final long serialVersionUID = 1L;
    protected Position position;

    public AnalyseError(ErrorCode errorCode, Position position)
    {
        super(errorCode);
        this.position = position;
    }

    public Position getPos()
    {
        return position;
    }

    @Override
    public String toString()
    {
        return String.format("Analyse Error: %s, at: %s", errorCode, position);
    }
}
