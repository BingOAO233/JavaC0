package c0.vm;

import c0.error.CompileError;
import c0.vm.dataType.Uint8;
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
                return null;
        }
    }

    public static Optional<Op> from_code(Uint8 code, BigInteger param)
    {
        switch (code.getValue())
        {
            case 0x00:
                return Optional.of(new Op(VmOp.NOP));
            case 0x01:
                return Optional.of(new Op(VmOp.PUSH, param));
            case 0x02:
                return Optional.of(new Op(VmOp.POP));
            case 0x03:
                return Optional.of(new Op(VmOp.POP_N, param.and(new BigInteger("0xffffffff", 16))));
            case 0x04:
                return Optional.of(new Op(VmOp.DUP));
            case 0x0a:
                return Optional.of(new Op(VmOp.LOC_A, param.and(new BigInteger("0xffffffff", 16))));
            case 0x0b:
                return Optional.of(new Op(VmOp.ARG_A, param.and(new BigInteger("0xffffffff", 16))));
            case 0x0c:
                return Optional.of(new Op(VmOp.GLOBAL_A, param.and(new BigInteger("0xffffffff", 16))));
            case 0x10:
                return Optional.of(new Op(VmOp.LOAD8));
            case 0x11:
                return Optional.of(new Op(VmOp.LOAD16));
            case 0x12:
                return Optional.of(new Op(VmOp.LOAD32));
            case 0x13:
                return Optional.of(new Op(VmOp.LOAD64));
            case 0x14:
                return Optional.of(new Op(VmOp.STORE8));
            case 0x15:
                return Optional.of(new Op(VmOp.STORE16));
            case 0x16:
                return Optional.of(new Op(VmOp.STORE32));
            case 0x17:
                return Optional.of(new Op(VmOp.STORE64));
            case 0x18:
                return Optional.of(new Op(VmOp.ALLOC));
            case 0x19:
                return Optional.of(new Op(VmOp.FREE));
            case 0x1a:
                return Optional.of(new Op(VmOp.STACK_ALLOC, param.and(new BigInteger("0xffffffff", 16))));
            case 0x20:
                return Optional.of(new Op(VmOp.ADD_I));
            case 0x21:
                return Optional.of(new Op(VmOp.SUB_I));
            case 0x22:
                return Optional.of(new Op(VmOp.MUL_I));
            case 0x23:
                return Optional.of(new Op(VmOp.DIV_I));
            case 0x24:
                return Optional.of(new Op(VmOp.ADD_F));
            case 0x25:
                return Optional.of(new Op(VmOp.SUB_F));
            case 0x26:
                return Optional.of(new Op(VmOp.MUL_F));
            case 0x27:
                return Optional.of(new Op(VmOp.DIV_F));
            case 0x28:
                return Optional.of(new Op(VmOp.DIV_U));
            case 0x29:
                return Optional.of(new Op(VmOp.SHL));
            case 0x2a:
                return Optional.of(new Op(VmOp.SHR));
            case 0x2b:
                return Optional.of(new Op(VmOp.AND));
            case 0x2c:
                return Optional.of(new Op(VmOp.OR));
            case 0x2d:
                return Optional.of(new Op(VmOp.XOR));
            case 0x2e:
                return Optional.of(new Op(VmOp.NOT));
            case 0x30:
                return Optional.of(new Op(VmOp.CMP_I));
            case 0x31:
                return Optional.of(new Op(VmOp.CMP_U));
            case 0x32:
                return Optional.of(new Op(VmOp.CMP_F));
            case 0x34:
                return Optional.of(new Op(VmOp.NEG_I));
            case 0x35:
                return Optional.of(new Op(VmOp.NEG_F));
            case 0x36:
                return Optional.of(new Op(VmOp.I_TO_F));
            case 0x37:
                return Optional.of(new Op(VmOp.F_TO_I));
            case 0x38:
                return Optional.of(new Op(VmOp.SHR_L));
            case 0x39:
                return Optional.of(new Op(VmOp.SET_LT));
            case 0x3a:
                return Optional.of(new Op(VmOp.SET_GT));
            case 0x40:
                return Optional.of(new Op(VmOp.BRA, param));
            case 0x41:
                return Optional.of(new Op(VmOp.BR, BigInteger.valueOf((int) param.longValue())));
            case 0x42:
                return Optional.of(new Op(VmOp.BR_FALSE, BigInteger.valueOf((int) param.longValue())));
            case 0x43:
                return Optional.of(new Op(VmOp.BR_TRUE, BigInteger.valueOf((int) param.longValue())));
            case 0x48:
                return Optional.of(new Op(VmOp.CALL, param.and(new BigInteger("0xffffffff", 16))));
            case 0x49:
                return Optional.of(new Op(VmOp.RET));
            case 0x4a:
                return Optional.of(new Op(VmOp.CALL_NAME, param.and(new BigInteger("0xffffffff", 16))));
            case 0x50:
                return Optional.of(new Op(VmOp.SCAN_I));
            case 0x51:
                return Optional.of(new Op(VmOp.SCAN_C));
            case 0x52:
                return Optional.of(new Op(VmOp.SCAN_F));
            case 0x54:
                return Optional.of(new Op(VmOp.PRINT_I));
            case 0x55:
                return Optional.of(new Op(VmOp.PRINT_C));
            case 0x56:
                return Optional.of(new Op(VmOp.PRINT_F));
            case 0x57:
                return Optional.of(new Op(VmOp.PRINT_S));
            case 0x58:
                return Optional.of(new Op(VmOp.PRINT_LN));
            case 0xfe:
                return Optional.of(new Op(VmOp.PANIC));
        }
        return null;
    }

    public BigInteger codeParam()
    {
        switch (this.insType)
        {
            case PUSH:
            case BRA:
                return value;
            case POP_N:
            case LOC_A:
            case ARG_A:
            case GLOBAL_A:
            case STACK_ALLOC:
            case CALL:
            case CALL_NAME:
                return value.and(new BigInteger("0xffffffff_ffffffff", 16));
            case BR:
            case BR_FALSE:
            case BR_TRUE:
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

        output.write(Longs.toByteArray(code.getValue()));
        switch (len.intValue())
        {
            case 0:
                break;
            case 4:
            case 8:
                var x = param.longValue();
                output.write(Longs.toByteArray(x));
                break;
        }
    }
}

