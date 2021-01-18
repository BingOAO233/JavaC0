package JavaC0.util.program.structure;

import JavaC0.compiler.Tuple;

import java.math.BigInteger;
import java.util.Optional;

public class JumpInstruction
{
    public JumpInstE inst;
    public Optional<Tuple<BigInteger, Optional<BigInteger>>> value;

    public JumpInstruction(JumpInstE inst)
    {
        this.inst = inst;
    }

    public JumpInstruction(BigInteger val)
    {
        inst = JumpInstE.Jump;
        this.value = Optional.of(new Tuple<>(val, Optional.empty()));
    }

    public JumpInstruction(BigInteger val1, BigInteger val2)
    {
        inst = JumpInstE.JumpIf;
        this.value = Optional.of(new Tuple<>(val1, Optional.of(val2)));
    }
}

