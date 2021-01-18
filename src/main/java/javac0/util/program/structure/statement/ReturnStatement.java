package javac0.util.program.structure.statement;

import javac0.util.program.Span;
import javac0.util.program.structure.expression.Expression;

import java.util.Optional;

public class ReturnStatement extends Statement
{
    //    public Span span;
    public Optional<Expression> retValue;

    public ReturnStatement(Span span, Optional<Expression> retValue)
    {
        super(span);
        this.retValue = retValue;
    }

}
