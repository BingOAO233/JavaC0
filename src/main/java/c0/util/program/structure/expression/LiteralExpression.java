package c0.util.program.structure.expression;

import c0.util.program.Span;
import c0.util.program.structure.LiteralType;

public class LiteralExpression extends Expression
{
    public LiteralType type;

    public Object value;

    public LiteralExpression(Span span, LiteralType type, Object value)
    {
        this.span = span;
        this.type = type;
    }

    public Long getInt64Value()
    {
        return (long) value;
    }

    public String getStringValue()
    {
        return (String) value;
    }

    public Double getDoubleValue()
    {
        return (double) value;
    }

}
