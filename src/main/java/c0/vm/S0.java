package c0.vm;

import java.util.ArrayList;

public class S0
{
    public ArrayList<GlobalValue> globals;
    public ArrayList<FunctionDefine> functions;

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
}
