package javac0.compiler;

import javac0.vm.dataType.Uint32;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GlobalEntries
{
    public IndexSet<String> functions;
    public IndexMap<BigInteger, byte[]> values;

    public GlobalEntries(IndexSet<String> functions,
                         HashMap<BigInteger, byte[]> values)
    {
        this.functions = functions;
        this.values = new IndexMap<>(values);
    }

    public Optional<Uint32> functionId(String funcName)
    {
        var id = functions.getIndex(funcName);
        if (id != -1)
        {
            return Optional.of(new Uint32(id));
        }
        return Optional.empty();
    }

    public Uint32 insertStringLiteral(String s, BigInteger valId)
    {
        values.put(valId, s.getBytes(StandardCharsets.UTF_8));
        return valueId(valId).get();
    }

    public Optional<Uint32> valueId(BigInteger symbol)
    {
        var id = values.getIndex(symbol);
        if (id != -1)
        {
            return Optional.of(new Uint32(id));
        }
        return Optional.empty();
    }
}
