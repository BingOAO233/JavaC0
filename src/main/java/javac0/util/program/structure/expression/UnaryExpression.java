package javac0.util.program.structure.expression;

import javac0.util.program.Span;
import javac0.util.program.structure.operator.UnaryOperator;

public class UnaryExpression extends Expression
{
    public UnaryOperator op;
    public Expression expression;

    public UnaryExpression(Span span, UnaryOperator op, Expression expression)
    {
        super(span);
        this.op = op;
        this.expression = expression;
    }

    @Override
    public UnaryExpression clone() throws CloneNotSupportedException
    {
        UnaryExpression ue = (UnaryExpression) super.clone();
        ue.expression = this.expression.clone();
        return ue;
    }
}
