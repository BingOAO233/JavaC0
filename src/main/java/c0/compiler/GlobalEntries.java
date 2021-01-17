package c0.compiler;

import c0.vm.dataType.Uint32;
import c0.vm.dataType.Uint8;

import java.math.BigInteger;
import java.util.*;

public class GlobalEntries
{
    IndexSet<String> functions;
    HashMap<BigInteger, ArrayList<Uint8>> values;

    public GlobalEntries(IndexSet<String> functions,
                         HashMap<BigInteger, ArrayList<Uint8>> values)
    {
        this.functions = functions;
        this.values = values;
    }

    public Optional<Uint32> functionId(String funcName)
    {
        return Optional.of(new Uint32(functions.getIndex(funcName)));
    }

    public Optional<Uint32> valueId(BigInteger symbol)
    {
        return Optional.empty();
    }
}
