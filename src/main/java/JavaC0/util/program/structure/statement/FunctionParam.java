package JavaC0.util.program.structure.statement;

import JavaC0.util.program.structure.Ident;
import JavaC0.util.program.structure.TypeDefine;

public class FunctionParam
{
    public boolean isConst;
    public Ident name;
    public TypeDefine type;

    public FunctionParam(boolean isConst, Ident name, TypeDefine type)
    {
        this.isConst = isConst;
        this.name = name;
        this.type = type;
    }
}

