package c0.vm;

import c0.vm.dataType.Uint8;

public enum VmOp
{
    NOP,
    PUSH,
    POP,
    POP_N,
    DUP,
    LOC_A,
    ARG_A,
    GLOBAL_A,
    LOAD8,
    LOAD16,
    LOAD32,
    LOAD64,
    STORE8,
    STORE16,
    STORE32,
    STORE64,
    ALLOC,
    FREE,
    STACK_ALLOC,
    ADD_I,
    SUB_I,
    MUL_I,
    DIV_I,
    ADD_F,
    SUB_F,
    MUL_F,
    DIV_F,
    DIV_U,
    SHL,
    SHR,
    AND,
    OR,
    XOR,
    NOT,
    CMP_I,
    CMP_U,
    CMP_F,
    NEG_I,
    NEG_F,
    I_TO_F,
    F_TO_I,
    SHR_L,
    SET_LT,
    SET_GT,
    BRA,
    BR,
    BR_FALSE,
    BR_TRUE,
    CALL,
    CALL_NAME,
    RET,
    SCAN_I,
    SCAN_C,
    SCAN_F,
    PRINT_I,
    PRINT_C,
    PRINT_F,
    PRINT_S,
    PRINT_LN,
    PANIC;

    public Uint8 code()
    {
        switch (this)
        {
            case NOP:
                return new Uint8((short) 0x00);
            case PUSH:
                return new Uint8((short) 0x01);
            case POP:
                return new Uint8((short) 0x02);
            case POP_N:
                return new Uint8((short) 0x03);
            case DUP:
                return new Uint8((short) 0x04);
            case LOC_A:
                return new Uint8((short) 0x0a);
            case ARG_A:
                return new Uint8((short) 0x0b);
            case GLOBAL_A:
                return new Uint8((short) 0x0c);
            case LOAD8:
                return new Uint8((short) 0x10);
            case LOAD16:
                return new Uint8((short) 0x11);
            case LOAD32:
                return new Uint8((short) 0x12);
            case LOAD64:
                return new Uint8((short) 0x13);
            case STORE8:
                return new Uint8((short) 0x14);
            case STORE16:
                return new Uint8((short) 0x15);
            case STORE32:
                return new Uint8((short) 0x16);
            case STORE64:
                return new Uint8((short) 0x17);
            case ALLOC:
                return new Uint8((short) 0x18);
            case FREE:
                return new Uint8((short) 0x19);
            case STACK_ALLOC:
                return new Uint8((short) 0x1a);
            case ADD_I:
                return new Uint8((short) 0x20);
            case SUB_I:
                return new Uint8((short) 0x21);
            case MUL_I:
                return new Uint8((short) 0x22);
            case DIV_I:
                return new Uint8((short) 0x23);
            case ADD_F:
                return new Uint8((short) 0x24);
            case SUB_F:
                return new Uint8((short) 0x25);
            case MUL_F:
                return new Uint8((short) 0x26);
            case DIV_F:
                return new Uint8((short) 0x27);
            case DIV_U:
                return new Uint8((short) 0x28);
            case SHL:
                return new Uint8((short) 0x29);
            case SHR:
                return new Uint8((short) 0x2a);
            case AND:
                return new Uint8((short) 0x2b);
            case OR:
                return new Uint8((short) 0x2c);
            case XOR:
                return new Uint8((short) 0x2d);
            case NOT:
                return new Uint8((short) 0x2e);
            case CMP_I:
                return new Uint8((short) 0x30);
            case CMP_U:
                return new Uint8((short) 0x31);
            case CMP_F:
                return new Uint8((short) 0x32);
            case NEG_I:
                return new Uint8((short) 0x34);
            case NEG_F:
                return new Uint8((short) 0x35);
            case I_TO_F:
                return new Uint8((short) 0x36);
            case F_TO_I:
                return new Uint8((short) 0x37);
            case SHR_L:
                return new Uint8((short) 0x38);
            case SET_LT:
                return new Uint8((short) 0x39);
            case SET_GT:
                return new Uint8((short) 0x3a);
            case BRA:
                return new Uint8((short) 0x40);
            case BR:
                return new Uint8((short) 0x41);
            case BR_FALSE:
                return new Uint8((short) 0x42);
            case BR_TRUE:
                return new Uint8((short) 0x43);
            case CALL:
                return new Uint8((short) 0x48);
            case RET:
                return new Uint8((short) 0x49);
            case CALL_NAME:
                return new Uint8((short) 0x4a);
            case SCAN_I:
                return new Uint8((short) 0x50);
            case SCAN_C:
                return new Uint8((short) 0x51);
            case SCAN_F:
                return new Uint8((short) 0x52);
            case PRINT_I:
                return new Uint8((short) 0x54);
            case PRINT_C:
                return new Uint8((short) 0x55);
            case PRINT_F:
                return new Uint8((short) 0x56);
            case PRINT_S:
                return new Uint8((short) 0x57);
            case PRINT_LN:
                return new Uint8((short) 0x58);
            case PANIC:
                return new Uint8((short) 0xfe);
        }
        return null;
    }

}
