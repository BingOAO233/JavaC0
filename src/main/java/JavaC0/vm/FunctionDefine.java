package JavaC0.vm;

import JavaC0.error.CompileError;
import JavaC0.util.Tools;
import JavaC0.vm.dataType.Uint32;
import com.google.common.primitives.Longs;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class FunctionDefine
{
    public Uint32 name;
    public Uint32 retSlots;
    public Uint32 paramSlots;
    public Uint32 locSlots;
    public ArrayList<Op> ins;

    public FunctionDefine(Uint32 name, Uint32 retSlots, Uint32 paramSlots, Uint32 locSlots,
                          ArrayList<Op> ins)
    {
        this.name = name;
        this.retSlots = retSlots;
        this.paramSlots = paramSlots;
        this.locSlots = locSlots;
        this.ins = ins;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(String.format("fn [%s] %s %s -> %s {\n", name, locSlots, paramSlots, retSlots));
        for (int i = 0; i < ins.size(); i++)
        {
            Op instruction = ins.get(i);
            result.append(String.format("% 5d: %s\n", i, instruction));
        }
        result.append("}");
        return result.toString();
    }

    public void writeBinary(PrintStream output) throws IOException, CompileError
    {
        output.write(Tools.toU32(Longs.toByteArray(name.getValue())));
        output.write(Tools.toU32(Longs.toByteArray(retSlots.getValue())));
        output.write(Tools.toU32(Longs.toByteArray(paramSlots.getValue())));
        output.write(Tools.toU32(Longs.toByteArray(locSlots.getValue())));
        output.write(Tools.toU32(Longs.toByteArray(ins.size())));
        for (var item : ins)
        {
            item.writeBinary(output);
        }
    }
}
