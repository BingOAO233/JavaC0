package c0.util.program.structure.expression;

import c0.util.program.structure.Ident;

public class IdentExpression extends Expression
{
    public Ident ident;

    public IdentExpression(Ident ident)
    {
        this.ident = ident;
    }
}
