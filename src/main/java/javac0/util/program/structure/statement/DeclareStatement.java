package javac0.util.program.structure.statement;

import javac0.util.program.Span;
import javac0.util.program.structure.Ident;
import javac0.util.program.structure.TypeDefine;
import javac0.util.program.structure.expression.Expression;

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
        super(span);
        this.isConst = isConst;
        this.name = name;
        this.type = type;
        this.value = value;
    }

}
