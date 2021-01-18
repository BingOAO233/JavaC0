package c0.error;

import c0.util.Position;
import c0.util.program.Span;

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
