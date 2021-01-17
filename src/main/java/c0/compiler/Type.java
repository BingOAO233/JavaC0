package c0.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;

public class Type
{
    public Ty type;
    public Optional<FunctionTy> func;

    public Type(Ty type, Optional<FunctionTy> func)
    {
        this.type = type;
        this.func = func;
    }

    public Type(Ty type)
    {
        this.type = type;
        this.func = Optional.empty();
    }

    public BigInteger size()
    {
        switch (type)
        {
            case INT:
            case DOUBLE:
            case ADDR:
                return BigInteger.valueOf(8);
            case BOOL:
                return BigInteger.valueOf(1);
            case FUNC:
            case VOID:
                return BigInteger.valueOf(0);
        }
        return null;
    }

    public BigInteger sizeSlot()
    {
        switch (type)
        {
            case INT:
            case DOUBLE:
            case BOOL:
            case ADDR:
                return BigInteger.valueOf(1);
            case FUNC:
            case VOID:
                return BigInteger.valueOf(0);
        }
        return null;
    }

    public Optional<FunctionTy> getFunc()
    {
        if (type == Ty.FUNC)
        {
            return func;
        }
        return Optional.empty();
    }

    @Override
    public String toString()
    {
        switch (type)
        {
            case INT:
                return "int";
            case DOUBLE:
                return "double";
            case BOOL:
                return "bool";
            case ADDR:
                return "addr";
            case FUNC:
                StringBuilder result = new StringBuilder();
                result.append("Fn(");
                var params = func.get().params;
                if (params.size() > 0)
                    result.append(params.get(0));
                for (var param : params)
                {
                    result.append(String.format(", %s", param));
                }
                result.append(String.format(") -> %s", func.get().ret));
                return result.toString();
            case VOID:
                return "void";
        }
        return null;
    }
}

enum Ty
{
    INT,
    DOUBLE,
    BOOL,
    ADDR,
    FUNC,
    VOID,
}

class FunctionTy
{
    public ArrayList<Type> params;
    public Type ret;

    public FunctionTy(ArrayList<Type> params, Type ret)
    {
        this.params = params;
        this.ret = ret;
    }
}
