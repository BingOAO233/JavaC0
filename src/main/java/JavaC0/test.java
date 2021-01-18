package JavaC0;

import JavaC0.util.Tools;
import com.google.common.primitives.Longs;

import java.io.IOException;

public class test
{
    public static void main(String[] args) throws IOException
    {
        long a = 16;
        for (var b :Tools.toU32(Longs.toByteArray(a)))
        {
            System.out.println(b);
        }
    }
}

