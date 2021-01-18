package c0.util.program;

import c0.util.Position;

public class Span implements Cloneable
{
    public Position startPos;
    public Position endPos;

    public Span()
    {
        startPos = new Position();
        endPos = new Position();
    }

    public Span(Position pos)
    {
        startPos = endPos = pos;
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

    @Override
    public Span clone() throws CloneNotSupportedException
    {
        Span s = (Span) super.clone();
        s.startPos = this.startPos.clone();
        s.endPos = this.endPos.clone();
        return s;
    }

    @Override
    public String toString()
    {
        return "{" + startPos + "," + endPos + "}";
    }
}
