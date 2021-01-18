package JavaC0.error;

import JavaC0.util.program.Span;

public class CompileError extends C0Error
{
    private static final long serialVersionUID = 1L;

    private Span span;

    public CompileError(ErrorCode errorCode, Span span)
    {
        super(errorCode);
        this.span = span;
    }

    @Override
    public String toString()
    {
        return String.format("Compile Error: %s, at: %s", errorCode, span);
    }
}
