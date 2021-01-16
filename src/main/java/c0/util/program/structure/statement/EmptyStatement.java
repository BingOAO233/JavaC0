package c0.util.program.structure.statement;

import c0.util.program.Span;

public class EmptyStatement extends Statement implements IElseIfBlock
{
    public EmptyStatement(Span span)
    {
        super(span);
    }
}
