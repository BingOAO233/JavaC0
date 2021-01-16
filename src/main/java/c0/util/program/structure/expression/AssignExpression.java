package c0.util.program.structure.expression;

import c0.util.program.Span;

class AssignExpression
{
    public Span span;
    public boolean allowAssignConst;
    public Expression left;
    public Expression right;

    public AssignExpression(Span span, boolean allowAssignConst, Expression left, Expression right)
    {
        this.span = span;
        this.allowAssignConst = allowAssignConst;
        this.left = left;
        this.right = right;
    }

    public AssignExpression()
    {

    }
}
