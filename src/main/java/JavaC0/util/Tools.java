package JavaC0.util;

import JavaC0.vm.dataType.Uint32;
import com.google.common.primitives.Longs;

public class Tools
{
    public static byte[] toU32(byte[] src)
    {
        byte[] res = new byte[4];
        for (int i = src.length - 4, j = 0; i < src.length; i++, j++)
        {
            if (i < 0)
                res[j] = 0;
            else
                res[j] = src[i];
        }
        return res;
    }

    public static byte[] toU8(byte[] src)
    {
        byte[] res = new byte[1];
        for (int i = src.length - 1, j = 0; i < src.length; i++, j++)
        {
            if (i < 0)
                res[j] = 0;
            else
                res[j] = src[i];
        }
        return res;
    }
}