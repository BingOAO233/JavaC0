package javac0.util.program.structure.statement;

import javac0.util.program.Span;
import javac0.util.program.structure.expression.Expression;

public class IfStatement extends Statement implements IElseIfBlock
{
    //    public Span span;
    public Expression condition;
    public BlockStatement ifBlock;
    public IElseIfBlock elseBlock;

    public IfStatement(Span span, Expression condition, BlockStatement ifBlock, IElseIfBlock elseBlock)
    {
        super(span);
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

}
