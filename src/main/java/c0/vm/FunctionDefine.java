package c0.vm;

import c0.vm.dataType.Uint32;

import java.util.ArrayList;

public class FunctionDefine
{
    public Uint32 name;
    public Uint32 retSlots;
    public Uint32 paramSlots;
    public Uint32 locSlots;
    public ArrayList<Op> ins;

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(String.format("fn [%s] %s %s -> %s {", name, locSlots, paramSlots, retSlots));
        for (int i = 0; i < ins.size(); i++)
        {
            Op instruction = ins.get(i);
            result.append(String.format("%05d: %s", i, instruction));
        }
        result.append("}");
        return result.toString();
    }
}
