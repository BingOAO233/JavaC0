package c0.vm;

import c0.vm.dataType.Uint8;

import java.util.ArrayList;

public class GlobalValue
{
    public boolean isConst;
    public ArrayList<Uint8> bytes;

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        if (isConst)
        {
            result.append("const:");
        }
        else
        {
            result.append("static:");
        }

        for (var bt : bytes)
        {
            result.append(Integer.toHexString(bt.getValue()));
        }

        String s = String.valueOf(bytes);
        result.append(s);
        result.append('\n');
        return result.toString();
    }
}
