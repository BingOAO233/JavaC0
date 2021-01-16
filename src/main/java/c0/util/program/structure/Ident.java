package c0.util.program.structure;

import c0.util.program.Span;

public class Ident
{
    public Span span;
    public String name;

    public Ident()
    {

    }

    public Ident(Span span, String name)
    {
        this.span = span;
        this.name = name;
    }
}
