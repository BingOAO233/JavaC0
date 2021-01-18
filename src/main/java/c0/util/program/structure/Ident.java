package c0.util.program.structure;

import c0.util.program.Span;

public class Ident implements Cloneable
{
    public Span span;
    public String name;

    public Ident(Span span, String name)
    {
        this.span = span;
        this.name = name;
    }

    @Override
    public Ident clone() throws CloneNotSupportedException
    {
        Ident i = (Ident) super.clone();
        i.span = this.span.clone();
        i.name = this.name;
        return i;
    }
}
