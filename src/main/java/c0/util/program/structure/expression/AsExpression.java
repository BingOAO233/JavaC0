package c0.util.program.structure.expression;

import c0.util.program.Span;
import c0.util.program.structure.TypeDefine;

public class AsExpression
{
    public Span span;
    public Expression value;
    public TypeDefine type;

    public AsExpression(Span span, Expression value, TypeDefine type)
    {
        this.span = span;
        this.value = value;
        this.type = type;
    }

    public AsExpression()
    {

    }
}
