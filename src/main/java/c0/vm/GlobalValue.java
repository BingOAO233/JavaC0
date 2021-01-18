package c0.vm;

import c0.vm.dataType.Uint8;
import com.google.common.primitives.Longs;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class GlobalValue
{
    public boolean isConst;
    public byte[] bytes;

    public GlobalValue(boolean isConst, byte[] bytes)
    {
        this.isConst = isConst;
        this.bytes = bytes;
    }

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
            result.append(String.format(" %s", Integer.toHexString(bt).toUpperCase()));
        }

        String s = null;
        try
        {
            s = new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        result.append(String.format(" (`%s`)", s));
        result.append('\n');
        return result.toString();
    }

    public void writeBinary(PrintStream output) throws IOException
    {
        output.write(isConst ? 0x01 : 0x00);
        output.write(bytes);
    }
}
