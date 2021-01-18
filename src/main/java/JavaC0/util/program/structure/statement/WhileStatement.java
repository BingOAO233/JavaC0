package JavaC0.util.program.structure.statement;

import JavaC0.util.program.Span;
import JavaC0.util.program.structure.expression.Expression;

public class WhileStatement extends Statement
{
    //    public Span span;
    public Expression condition;
    public BlockStatement body;

    public WhileStatement(Span span, Expression condition, BlockStatement blockStatement)
    {
        super(span);
        this.condition = condition;
        this.body = blockStatement;
    }


}
