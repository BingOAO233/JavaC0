package c0.error;

import c0.util.Position;

public class C0Error extends Exception
{
    protected ErrorCode errorCode;
    private static final long serialVersionUID = 1L;

    public C0Error(ErrorCode errorCode)
    {
        this.errorCode = errorCode;
    }

    public ErrorCode getErr()
    {
        return errorCode;
    }

}
