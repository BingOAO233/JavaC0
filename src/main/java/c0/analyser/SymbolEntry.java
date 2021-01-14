package c0.analyser;

public class SymbolEntry
{
    boolean isConstant;
    boolean isInitialized;
    int stackOffset;

    public SymbolEntry(boolean isConstant, boolean isInitialized, int stackOffset)
    {
        this.isConstant = isConstant;
        this.isInitialized = isInitialized;
        this.stackOffset = stackOffset;
    }

    public int getStackOffset()
    {
        return stackOffset;
    }

    public boolean isConstant()
    {
        return isConstant;
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void setConstant(boolean constant)
    {
        isConstant = constant;
    }

    public void setInitialized(boolean initialized)
    {
        isInitialized = initialized;
    }

    public void setStackOffset(int stackOffset)
    {
        this.stackOffset = stackOffset;
    }
}
