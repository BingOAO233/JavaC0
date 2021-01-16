package c0.util.program.structure.statement;

import c0.util.program.Span;
import c0.util.program.structure.Ident;
import c0.util.program.structure.TypeDefine;
import c0.util.program.structure.expression.Expression;

import java.util.Optional;

public class DeclareStatement extends Statement
{
    //    public Span span;
    public boolean isConst;
    public Ident name;
    public TypeDefine type;
    public Optional<Expression> value;

    public DeclareStatement(Span span, boolean isConst, Ident name, TypeDefine type, Optional<Expression> value)
    {
        this.span = span;
        this.isConst = isConst;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    DeclareStatement()
    {

    }
}
