package javac0.compiler;

import javac0.error.CompileError;
import javac0.error.ErrorCode;
import javac0.util.program.Span;

import java.math.BigInteger;
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

    public int size() throws CompileError
    {
        switch (type)
        {
            case INT:
            case DOUBLE:
            case ADDR:
                return 8;
            case BOOL:
                return 1;
            case FUNC:
            case VOID:
                return 0;
        }
        throw new CompileError(ErrorCode.UnexpectedType, new Span());
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

