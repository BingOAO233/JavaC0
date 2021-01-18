package c0.util.program.structure.expression;

import c0.util.program.Span;
import c0.util.program.structure.TypeDefine;

public class AsExpression extends Expression
{
    public Expression value;
    public TypeDefine type;

    public AsExpression(Span span, Expression value, TypeDefine type)
    {
        super(span);
        this.value = value;
        this.type = type;
    }

}
