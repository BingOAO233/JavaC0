package JavaC0.util.program.structure.expression;

import JavaC0.util.program.Span;
import JavaC0.util.program.structure.LiteralType;

public class LiteralExpression extends Expression
{
    public LiteralType type;

    public Object value;

    public LiteralExpression(Span span, LiteralType type, Object value)
    {
        super(span);
        this.type = type;
        this.value = value;
    }

    public Long getInt64Value()
    {
        return (long) value;
    }

    @Override
    public LiteralExpression clone() throws CloneNotSupportedException
    {
        LiteralExpression e = (LiteralExpression) super.clone();
        e.type = LiteralType.valueOf(type.toString());
        switch (type)
        {
            case INT64:
                e.value = Long.valueOf((long) value);
                break;
            case DOUBLE:
                e.value = Double.valueOf((double) value);
                break;
            case STRING:
                e.value = String.valueOf(value);
                break;
            case CHAR:
                e.value = Long.valueOf((long) value);
                break;
        }
        return e;
    }
}
