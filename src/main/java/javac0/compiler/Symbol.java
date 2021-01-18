package javac0.compiler;

import java.math.BigInteger;

public class Symbol
{
    public BigInteger id;
    public Type ty;
    public boolean isConst;

    public Symbol(Type ty, boolean isConst)
    {
        this.id = BigInteger.valueOf(0);
        this.ty = ty;
        this.isConst = isConst;
    }
}
