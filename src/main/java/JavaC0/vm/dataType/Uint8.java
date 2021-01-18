package JavaC0.vm.dataType;

public class Uint8
{
    short value;

    public Uint8(short value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value & 0x00ff;
    }

    public void setValue(short value)
    {
        this.value = value;
    }


}
