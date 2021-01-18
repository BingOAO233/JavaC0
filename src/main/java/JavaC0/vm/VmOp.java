package JavaC0.vm;

import JavaC0.error.CompileError;
import JavaC0.error.ErrorCode;
import JavaC0.vm.dataType.Uint8;

public enum VmOp
{
    Nop,
    Push,
    Pop,
    PopN,
    Dup,
    LocA,
    ArgA,
    GlobA,
    Load8,
    Load16,
    Load32,
    Load64,
    Store8,
    Store16,
    Store32,
    Store64,
    Alloc,
    Free,
    StackAlloc,
    AddI,
    SubI,
    MulI,
    DivI,
    AddF,
    SubF,
    MulF,
    DivF,
    DivU,
    Shl,
    Shr,
    And,
    Or,
    Xor,
    Not,
    CmpI,
    CmpU,
    CmpF,
    NegI,
    NegF,
    IToF,
    FToI,
    ShrL,
    SetLt,
    SetGt,
    Bra,
    Br,
    BrFalse,
    BrTrue,
    Call,
    CallName,
    Ret,
    ScanI,
    ScanC,
    ScanF,
    PrintI,
    PrintC,
    PrintF,
    PrintS,
    PrintLn,
    Panic;

    public Uint8 code() throws CompileError
    {
        switch (this)
        {
            case Nop:
                return new Uint8((short) 0x00);
            case Push:
                return new Uint8((short) 0x01);
            case Pop:
                return new Uint8((short) 0x02);
            case PopN:
                return new Uint8((short) 0x03);
            case Dup:
                return new Uint8((short) 0x04);
            case LocA:
                return new Uint8((short) 0x0a);
            case ArgA:
                return new Uint8((short) 0x0b);
            case GlobA:
                return new Uint8((short) 0x0c);
            case Load8:
                return new Uint8((short) 0x10);
            case Load16:
                return new Uint8((short) 0x11);
            case Load32:
                return new Uint8((short) 0x12);
            case Load64:
                return new Uint8((short) 0x13);
            case Store8:
                return new Uint8((short) 0x14);
            case Store16:
                return new Uint8((short) 0x15);
            case Store32:
                return new Uint8((short) 0x16);
            case Store64:
                return new Uint8((short) 0x17);
            case Alloc:
                return new Uint8((short) 0x18);
            case Free:
                return new Uint8((short) 0x19);
            case StackAlloc:
                return new Uint8((short) 0x1a);
            case AddI:
                return new Uint8((short) 0x20);
            case SubI:
                return new Uint8((short) 0x21);
            case MulI:
                return new Uint8((short) 0x22);
            case DivI:
                return new Uint8((short) 0x23);
            case AddF:
                return new Uint8((short) 0x24);
            case SubF:
                return new Uint8((short) 0x25);
            case MulF:
                return new Uint8((short) 0x26);
            case DivF:
                return new Uint8((short) 0x27);
            case DivU:
                return new Uint8((short) 0x28);
            case Shl:
                return new Uint8((short) 0x29);
            case Shr:
                return new Uint8((short) 0x2a);
            case And:
                return new Uint8((short) 0x2b);
            case Or:
                return new Uint8((short) 0x2c);
            case Xor:
                return new Uint8((short) 0x2d);
            case Not:
                return new Uint8((short) 0x2e);
            case CmpI:
                return new Uint8((short) 0x30);
            case CmpU:
                return new Uint8((short) 0x31);
            case CmpF:
                return new Uint8((short) 0x32);
            case NegI:
                return new Uint8((short) 0x34);
            case NegF:
                return new Uint8((short) 0x35);
            case IToF:
                return new Uint8((short) 0x36);
            case FToI:
                return new Uint8((short) 0x37);
            case ShrL:
                return new Uint8((short) 0x38);
            case SetLt:
                return new Uint8((short) 0x39);
            case SetGt:
                return new Uint8((short) 0x3a);
            case Bra:
                return new Uint8((short) 0x40);
            case Br:
                return new Uint8((short) 0x41);
            case BrFalse:
                return new Uint8((short) 0x42);
            case BrTrue:
                return new Uint8((short) 0x43);
            case Call:
                return new Uint8((short) 0x48);
            case Ret:
                return new Uint8((short) 0x49);
            case CallName:
                return new Uint8((short) 0x4a);
            case ScanI:
                return new Uint8((short) 0x50);
            case ScanC:
                return new Uint8((short) 0x51);
            case ScanF:
                return new Uint8((short) 0x52);
            case PrintI:
                return new Uint8((short) 0x54);
            case PrintC:
                return new Uint8((short) 0x55);
            case PrintF:
                return new Uint8((short) 0x56);
            case PrintS:
                return new Uint8((short) 0x57);
            case PrintLn:
                return new Uint8((short) 0x58);
            case Panic:
                return new Uint8((short) 0xfe);
        }
        throw new CompileError(ErrorCode.Unreachable, null);
    }

}
