package c0.util.program.structure.statement;

import c0.util.program.Span;

import java.util.ArrayList;

public class BlockStatement extends Statement
{
    //    public Span span;
    public ArrayList<Statement> statements;

    BlockStatement()
    {

    }

    public BlockStatement(Span span, ArrayList<Statement> statements)
    {
        this.span = span;
        this.statements = statements;
    }

}
