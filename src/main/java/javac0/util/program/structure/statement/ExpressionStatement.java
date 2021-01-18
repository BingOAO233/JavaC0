package javac0.util.program.structure.statement;

import javac0.util.program.Span;
import javac0.util.program.structure.expression.Expression;

public class ExpressionStatement extends Statement
{
    // public Span span;
    public Expression expression;

    public ExpressionStatement(Span span, Expression expression)
    {
        super(span);
        this.expression = expression;
    }
}
