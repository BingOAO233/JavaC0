package javac0.compiler;

import java.math.BigInteger;

public class SymbolIdGenerator
{
    public BigInteger nextId;

    public SymbolIdGenerator()
    {
        nextId = BigInteger.valueOf(0);
    }

    public BigInteger next()
    {
        var id = nextId;
        nextId = nextId.add(BigInteger.valueOf(1));
        return BigInteger.valueOf(id.longValue());
    }
}
