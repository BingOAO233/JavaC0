package javac0.util.program.structure;

import javac0.util.program.structure.statement.DeclareStatement;
import javac0.util.program.structure.statement.FunctionStatement;

import java.util.ArrayList;

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


