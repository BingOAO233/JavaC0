package JavaC0.error;

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
