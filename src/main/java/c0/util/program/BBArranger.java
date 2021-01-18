package c0.util.program;

import c0.error.CompileError;
import c0.error.ErrorCode;
import c0.util.program.structure.BasicBlock;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;

public class BBArranger
{
    BasicBlock[] bb;
    HashSet<BigInteger> path;
    HashSet<BigInteger> vis;
    ArrayList<BigInteger> inDegree;
    ArrayList<BigInteger> arr;

    public BBArranger(BasicBlock[] bb)
    {
        this.bb = bb;
        path = new HashSet<>();
        vis = new HashSet<>();
        inDegree = new ArrayList<>();
        for (int i = 0; i < bb.length; i++)
        {
            inDegree.add(BigInteger.valueOf(0));
        }
        arr = new ArrayList<>();
    }

    public void constructArr(BigInteger srt) throws CompileError
    {
        vis(srt);
        arr(srt);
    }

    private void vis(BigInteger id)
    {
        if (path.contains(id))
        {
            return;
        }

        var temp = inDegree.get(id.intValue());
        inDegree.set(id.intValue(), temp.add(BigInteger.ONE));

        if (vis.contains(id))
        {
            return;
        }
        vis.add(id);
        path.add(id);
        var ins = bb[id.intValue()].jump;
        switch (ins.inst)
        {
            case Jump:
                vis(ins.value.get().first);
                break;
            case JumpIf:
                vis(ins.value.get().first);
                vis(ins.value.get().second.get());
                break;
        }
        path.remove(id);
    }

    private void arr(BigInteger id) throws CompileError
    {
        if (path.contains(id))
        {
            return;
        }
        var temp = inDegree.get(id.intValue());
        temp = temp.subtract(BigInteger.ONE);
        if (temp.compareTo(BigInteger.ZERO) == -1)
        {
            throw new CompileError(ErrorCode.OtherError, new Span());
        }
        if (temp.intValue() != 0)
        {
            return;
        }

        arr.add(id);
        path.add(id);

        var ins = bb[id.intValue()].jump;
        switch (ins.inst)
        {
            case Jump:
                arr(ins.value.get().first);
                break;
            case JumpIf:
                arr(ins.value.get().first);
                arr(ins.value.get().second.get());
                break;
            case Return:
                break;
            case Unreachable:
                throw new CompileError(ErrorCode.Unreachable, new Span());
            case Undefined:
                throw new CompileError(ErrorCode.NoDeclaration, new Span());
        }
        path.remove(id);
    }

    public ArrayList<BigInteger> arrange()
    {
        return arr;
    }
}
