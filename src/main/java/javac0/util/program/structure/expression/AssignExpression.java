package javac0.util.program.structure.expression;

import javac0.util.program.Span;

public class AssignExpression extends Expression
{
    public boolean allowAssignConst;
    public Expression left;
    public Expression right;

    public AssignExpression(Span span, boolean allowAssignConst, Expression left, Expression right)
    {
        super(span);
        this.allowAssignConst = allowAssignConst;
        this.left = left;
        this.right = right;
    }

}
