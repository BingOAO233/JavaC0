package c0.util.program.structure.statement;

import c0.util.program.Span;
import c0.util.program.structure.expression.Expression;

public class WhileStatement extends Statement
{
    //    public Span span;
    public Expression condition;
    public BlockStatement blockStatement;

    public WhileStatement(Span span, Expression condition, BlockStatement blockStatement)
    {
        super(span);
        this.condition = condition;
        this.blockStatement = blockStatement;
    }


}
