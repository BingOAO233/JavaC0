package JavaC0.vm;

import JavaC0.error.CompileError;
import JavaC0.util.Tools;
import JavaC0.vm.dataType.Uint8;
import com.google.common.primitives.Longs;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Optional;

public class Op
{
    VmOp insType;
    BigInteger value;

    public Op(VmOp insType)
    {
        this.insType = insType;
        this.value = null;
    }

    public Op(VmOp insType, BigInteger value)
    {
        this.insType = insType;
        this.value = value;
    }

    public static BigInteger param_size(Uint8 code)
    {
        switch (code.getValue())
        {
            case 0x01:
            case 0x40:
                return BigInteger.valueOf(8);
            case 0x03:
            case 0x0a:
            case 0x0b:
            case 0x0c:
            case 0x1a:
            case 0x41:
            case 0x42:
            case 0x43:
            case 0x48:
            case 0x4a:
                return BigInteger.valueOf(4);
            default:
                return BigInteger.ZERO;
        }
    }

    public static Optional<Op> from_code(Uint8 code, BigInteger param)
    {
        switch (code.getValue())
        {
            case 0x00:
                return Optional.of(new Op(VmOp.Nop));
            case 0x01:
                return Optional.of(new Op(VmOp.Push, param));
            case 0x02:
                return Optional.of(new Op(VmOp.Pop));
            case 0x03:
                return Optional.of(new Op(VmOp.PopN, param.and(new BigInteger("0xffffffff", 16))));
            case 0x04:
                return Optional.of(new Op(VmOp.Dup));
            case 0x0a:
                return Optional.of(new Op(VmOp.LocA, param.and(new BigInteger("0xffffffff", 16))));
            case 0x0b:
                return Optional.of(new Op(VmOp.ArgA, param.and(new BigInteger("0xffffffff", 16))));
            case 0x0c:
                return Optional.of(new Op(VmOp.GlobA, param.and(new BigInteger("0xffffffff", 16))));
            case 0x10:
                return Optional.of(new Op(VmOp.Load8));
            case 0x11:
                return Optional.of(new Op(VmOp.Load16));
            case 0x12:
                return Optional.of(new Op(VmOp.Load32));
            case 0x13:
                return Optional.of(new Op(VmOp.Load64));
            case 0x14:
                return Optional.of(new Op(VmOp.Store8));
            case 0x15:
                return Optional.of(new Op(VmOp.Store16));
            case 0x16:
                return Optional.of(new Op(VmOp.Store32));
            case 0x17:
                return Optional.of(new Op(VmOp.Store64));
            case 0x18:
                return Optional.of(new Op(VmOp.Alloc));
            case 0x19:
                return Optional.of(new Op(VmOp.Free));
            case 0x1a:
                return Optional.of(new Op(VmOp.StackAlloc, param.and(new BigInteger("0xffffffff", 16))));
            case 0x20:
                return Optional.of(new Op(VmOp.AddI));
            case 0x21:
                return Optional.of(new Op(VmOp.SubI));
            case 0x22:
                return Optional.of(new Op(VmOp.MulI));
            case 0x23:
                return Optional.of(new Op(VmOp.DivI));
            case 0x24:
                return Optional.of(new Op(VmOp.AddF));
            case 0x25:
                return Optional.of(new Op(VmOp.SubF));
            case 0x26:
                return Optional.of(new Op(VmOp.MulF));
            case 0x27:
                return Optional.of(new Op(VmOp.DivF));
            case 0x28:
                return Optional.of(new Op(VmOp.DivU));
            case 0x29:
                return Optional.of(new Op(VmOp.Shl));
            case 0x2a:
                return Optional.of(new Op(VmOp.Shr));
            case 0x2b:
                return Optional.of(new Op(VmOp.And));
            case 0x2c:
                return Optional.of(new Op(VmOp.Or));
            case 0x2d:
                return Optional.of(new Op(VmOp.Xor));
            case 0x2e:
                return Optional.of(new Op(VmOp.Not));
            case 0x30:
                return Optional.of(new Op(VmOp.CmpI));
            case 0x31:
                return Optional.of(new Op(VmOp.CmpU));
            case 0x32:
                return Optional.of(new Op(VmOp.CmpF));
            case 0x34:
                return Optional.of(new Op(VmOp.NegI));
            case 0x35:
                return Optional.of(new Op(VmOp.NegF));
            case 0x36:
                return Optional.of(new Op(VmOp.IToF));
            case 0x37:
                return Optional.of(new Op(VmOp.FToI));
            case 0x38:
                return Optional.of(new Op(VmOp.ShrL));
            case 0x39:
                return Optional.of(new Op(VmOp.SetLt));
            case 0x3a:
                return Optional.of(new Op(VmOp.SetGt));
            case 0x40:
                return Optional.of(new Op(VmOp.Bra, param));
            case 0x41:
                return Optional.of(new Op(VmOp.Br, BigInteger.valueOf((int) param.longValue())));
            case 0x42:
                return Optional.of(new Op(VmOp.BrFalse, BigInteger.valueOf((int) param.longValue())));
            case 0x43:
                return Optional.of(new Op(VmOp.BrTrue, BigInteger.valueOf((int) param.longValue())));
            case 0x48:
                return Optional.of(new Op(VmOp.Call, param.and(new BigInteger("0xffffffff", 16))));
            case 0x49:
                return Optional.of(new Op(VmOp.Ret));
            case 0x4a:
                return Optional.of(new Op(VmOp.CallName, param.and(new BigInteger("0xffffffff", 16))));
            case 0x50:
                return Optional.of(new Op(VmOp.ScanI));
            case 0x51:
                return Optional.of(new Op(VmOp.ScanC));
            case 0x52:
                return Optional.of(new Op(VmOp.ScanF));
            case 0x54:
                return Optional.of(new Op(VmOp.PrintI));
            case 0x55:
                return Optional.of(new Op(VmOp.PrintC));
            case 0x56:
                return Optional.of(new Op(VmOp.PrintF));
            case 0x57:
                return Optional.of(new Op(VmOp.PrintS));
            case 0x58:
                return Optional.of(new Op(VmOp.PrintLn));
            case 0xfe:
                return Optional.of(new Op(VmOp.Panic));
        }
        return null;
    }

    public BigInteger codeParam()
    {
        switch (this.insType)
        {
            case Push:
            case Bra:
                return value;
            case PopN:
            case LocA:
            case ArgA:
            case GlobA:
            case StackAlloc:
            case Call:
            case CallName:
                return value.and(new BigInteger("ffffffffffffffff", 16));
            case Br:
            case BrFalse:
            case BrTrue:
                return BigInteger.valueOf(value.longValue());
            default:
                return null;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(insType);
        if (value != null)
        {
            result.append(String.format("(%s)", value));
        }
        return result.toString();
    }

    public void writeBinary(PrintStream output) throws IOException, CompileError
    {
        var code = insType.code();
        var param = codeParam();
        var len = param_size(code);

        output.write(Tools.toU8(Longs.toByteArray(code.getValue())));
        switch (
                len.intValue())
        {
            case 0:
                break;
            case 4:
                output.write(Tools.toU32(Longs.toByteArray(param.longValue())));
                break;
            case 8:
                output.write(Longs.toByteArray(param.longValue()));
                break;
        }
    }
}

