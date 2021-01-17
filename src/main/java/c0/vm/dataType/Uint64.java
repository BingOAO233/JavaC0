package c0.vm.dataType;

import java.math.BigInteger;

public class Uint64
{
    BigInteger value;

    public BigInteger getValue()
    {
        return value.and(new BigInteger("0xffffffff_ffffffff", 16));
    }

    public void setValue(BigInteger value)
    {
        this.value = value;
    }
}
