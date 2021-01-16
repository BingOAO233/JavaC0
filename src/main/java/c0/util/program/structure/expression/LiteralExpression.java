package c0.util.program.structure.expression;

import c0.util.program.Span;
import c0.util.program.structure.LiteralType;

class LiteralExpression
{
    public Span span;
    public LiteralType type;

    public LiteralExpression(Span span, LiteralType type)
    {
        this.span = span;
        this.type = type;
    }

    public LiteralExpression()
    {

    }
}
