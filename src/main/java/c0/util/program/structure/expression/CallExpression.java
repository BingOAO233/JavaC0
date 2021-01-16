package c0.util.program.structure.expression;

import c0.util.program.Span;
import c0.util.program.structure.Ident;

import java.util.ArrayList;

class CallExpression
{
    public Span span;
    public Ident function;
    public ArrayList<Expression> params;

    public CallExpression(Span span, Ident function, ArrayList<Expression> params)
    {
        this.span = span;
        this.function = function;
        this.params = params;
    }

    public CallExpression()
    {

    }
}
