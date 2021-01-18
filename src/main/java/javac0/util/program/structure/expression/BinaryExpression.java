package javac0.util.program.structure.expression;

import javac0.util.program.Span;
import javac0.util.program.structure.operator.BinaryOperator;

public class BinaryExpression extends Expression
{
    public BinaryOperator op;
    public Expression left;
    public Expression right;

    public BinaryExpression(Span span, BinaryOperator op, Expression left, Expression right)
    {
        super(span);
        this.op = op;
        this.left = left;
        this.right = right;
    }

}
