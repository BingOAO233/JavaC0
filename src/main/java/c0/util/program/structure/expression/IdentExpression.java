package c0.util.program.structure.expression;

import c0.util.program.Span;
import c0.util.program.structure.Ident;

public class IdentExpression extends Expression
{
    public Ident ident;

    public IdentExpression(Ident ident)
    {
        super(new Span());
        this.ident = ident;
    }

    @Override
    public IdentExpression clone() throws CloneNotSupportedException
    {
        IdentExpression e = (IdentExpression) super.clone();
        e.ident = this.ident.clone();
        return e;
    }
}
