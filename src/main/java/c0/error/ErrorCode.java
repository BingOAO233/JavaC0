package c0.error;

public enum ErrorCode
{
    NoError, // Should be only used internally.
    StreamError,
    EOF,
    InvalidInt64,
    Int64Overflow,
    UnknownToken,
    UnexpectedToken,
    DuplicatedDeclaration,
    NoDeclaration
}

