package c0.compiler;

import c0.vm.dataType.Uint32;
import c0.vm.dataType.Uint8;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GlobalEntries
{
    public IndexSet<String> functions;
    public HashMap<BigInteger, byte[]> values;

    public GlobalEntries(IndexSet<String> functions,
                         HashMap<BigInteger, byte[]> values)
    {
        this.functions = functions;
        this.values = values;
    }

    public Optional<Uint32> functionId(String funcName)
    {
        return Optional.of(new Uint32(functions.getIndex(funcName)));
    }

    public Uint32 insertStringLiteral(String s, BigInteger valId)
    {
        values.put(valId, s.getBytes(StandardCharsets.UTF_8));
        return valueId(valId).get();
    }

    public Optional<Uint32> valueId(BigInteger symbol)
    {
        return Optional.empty();
    }
}
