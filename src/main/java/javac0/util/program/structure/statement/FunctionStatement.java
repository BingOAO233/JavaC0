package javac0.util.program.structure.statement;

import javac0.util.program.Span;
import javac0.util.program.structure.Ident;
import javac0.util.program.structure.TypeDefine;

import java.util.ArrayList;

public class FunctionStatement extends Statement
{
    //    public Span span;
    public Ident name;
    public ArrayList<FunctionParam> params;
    public TypeDefine returnType;
    public BlockStatement body;

    public FunctionStatement(Span span, Ident name, ArrayList<FunctionParam> params, TypeDefine returnType, BlockStatement body)
    {
        super(span);
        this.name = name;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }
}
