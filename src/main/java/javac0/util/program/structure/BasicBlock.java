package javac0.util.program.structure;

import javac0.vm.Op;

import java.util.ArrayList;

public class BasicBlock
{
    public ArrayList<Op> code;
    public JumpInstruction jump;

    public BasicBlock()
    {
        this.code = new ArrayList<>();
        this.jump = new JumpInstruction(JumpInstE.Undefined);
    }
}

