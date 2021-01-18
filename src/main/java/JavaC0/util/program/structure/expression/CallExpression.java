package JavaC0.util.program.structure.expression;

import JavaC0.util.program.Span;
import JavaC0.util.program.structure.Ident;

import java.util.ArrayList;

public class CallExpression extends Expression
{
    public Ident function;
    public ArrayList<Expression> params;

    public CallExpression(Span span, Ident function, ArrayList<Expression> params)
    {
        super(span);
        this.function = function;
        this.params = params;
    }


}
