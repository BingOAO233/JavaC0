package JavaC0.vm;

import JavaC0.error.CompileError;
import com.google.common.primitives.Longs;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


public class S0
{
    public ArrayList<GlobalValue> globals;
    public ArrayList<FunctionDefine> functions;
    public static final long MAGIC_NUMBER = 0x72303b3e;
    public static final long VERSION = 1;

    public S0(ArrayList<GlobalValue> globals, ArrayList<FunctionDefine> functions)
    {
        this.globals = globals;
        this.functions = functions;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (var global : globals)
        {
            result.append(global);
            result.append('\n');
        }
        result.append('\n');
        for (var func : functions)
        {
            result.append(func);
            result.append('\n');
        }
        return result.toString();
    }

    public void writeBinary(PrintStream output) throws IOException, CompileError
    {
        output.write(Longs.toByteArray(MAGIC_NUMBER));
        output.write(Longs.toByteArray(VERSION));

        output.write(Longs.toByteArray(globals.size()));
        for (var item : globals)
        {
            item.writeBinary(output);
        }

        output.write(Longs.toByteArray(functions.size()));
        for (var item : functions)
        {
            item.writeBinary(output);
        }
    }
}