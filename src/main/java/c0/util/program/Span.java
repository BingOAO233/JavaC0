package c0.util.program;

import JavaC0.util.Pos;
import c0.util.Position;

public class Span
{
    public Position startPos;
    public Position endPos;

    public Span()
    {
        startPos = new Position();
        endPos = new Position();
    }

    public Span(Position startPos, Position endPos)
    {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public Position getStartPos()
    {
        return startPos;
    }

    public Position getEndPos()
    {
        return endPos;
    }

//    public static Span EOF()
//    {
//        return new Span()
//    }

    public static Span add(Span a, Span b)
    {
        Position srt = Position.min(a.startPos, b.startPos);
        Position end = Position.max(a.endPos, b.endPos);
        return new Span(srt, end);
    }

    public void addAssign(Span s)
    {
        startPos = Position.min(startPos, s.startPos);
        endPos = Position.max(endPos, s.endPos);
    }
}
