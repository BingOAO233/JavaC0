package c0.util.program.structure;

import c0.util.program.Span;
import c0.util.program.structure.statement.DeclareStatement;
import c0.util.program.structure.statement.FunctionStatement;

import java.util.ArrayList;
import java.util.Optional;

public class Program
{
    public ArrayList<DeclareStatement> declarations;
    public ArrayList<FunctionStatement> functions;

    public Program(ArrayList<DeclareStatement> decls, ArrayList<FunctionStatement> funcs)
    {
        declarations = decls;
        functions = funcs;
    }
}


