package c0.vm.dataType;

import java.math.BigInteger;

public class Uint32
{
    long value;

    public Uint32(long value)
    {
        this.value = value;
    }

    public long getValue()
    {
        return value & 0x00000000ffffffff;
    }

    public void setValue(short value)
    {
        this.value = value;
    }

    public BigInteger getBig()
    {
        return BigInteger.valueOf(getValue());
    }
}
