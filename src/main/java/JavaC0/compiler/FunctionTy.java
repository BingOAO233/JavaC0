package JavaC0.compiler;

import java.util.ArrayList;

public class FunctionTy
{
    public ArrayList<Type> params;
    public Type ret;

    public FunctionTy(ArrayList<Type> params, Type ret)
    {
        this.params = params;
        this.ret = ret;
    }
}
