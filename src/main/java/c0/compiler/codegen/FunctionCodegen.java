package c0.compiler.codegen;

import c0.compiler.*;
import c0.compiler.Compiler;
import c0.error.CompileError;
import c0.error.ErrorCode;
import c0.util.program.Span;
import c0.util.program.structure.BasicBlock;
import c0.util.program.structure.JumpInstruction;
import c0.util.program.structure.Place;
import c0.util.program.structure.expression.AssignExpression;
import c0.util.program.structure.expression.Expression;
import c0.util.program.structure.expression.IdentExpression;
import c0.util.program.structure.p;
import c0.util.program.structure.statement.*;
import c0.vm.FunctionDefine;
import org.checkerframework.checker.units.qual.C;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class FunctionCodegen
{
    public static String RET_VAL_KEY = "$ret";
    public static String FUNC_VAL_KEY = "$func";
    FunctionStatement func;
    Scope scope;
    GlobalEntries entries;
    ArrayList<BasicBlock> basicBlocks;
    ArrayList<Tuple<BigInteger, BigInteger>> breakContinue;
    HashMap<BigInteger, Place> placeMapping;
    long arg_top;
    long loc_top;

    public FunctionCodegen(FunctionStatement func, Scope scope, GlobalEntries entries)
    {
        this.func = func;
        this.scope = scope;
        this.entries = entries;
        basicBlocks = new ArrayList<>();
        breakContinue = new ArrayList<>();
        placeMapping = new HashMap<>();
        arg_top = 0;
        arg_top = 0;
    }

    public FunctionDefine compile() throws CompileError
    {
        return compileFunction();
    }

    private FunctionDefine compileFunction() throws CompileError
    {
        Scope s = new Scope(scope);

        var slot = addParams(s);

        var srtBB = newBB();

        var endBB = compileBlockWithoutScope(func.body, srtBB, scope);

    }

    private BigInteger compileBlockWithoutScope(BlockStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var curBBId = id;
        for (var st : stmt.statements)
        {
            curBBId = compileStmt(st, curBBId, s);
        }
        return curBBId;
    }

    private BigInteger compileStmt(Statement stmt, BigInteger id, Scope s) throws CompileError
    {
        var c = stmt.getClass().toString().split("[.]");
        if (BlockStatement.class.equals(stmt.getClass()))
        {
            return compileBlock((BlockStatement) stmt, id, s);
        }
        else if (WhileStatement.class.equals(stmt.getClass()))
        {
            return compileWhile((WhileStatement) stmt, id, s);
        }
        else if (IfStatement.class.equals(stmt.getClass()))
        {
            return compileIf((IfStatement) stmt, id, s);
        }
        else if (ExpressionStatement.class.equals(stmt.getClass()))
        {
            return compileExpr(((ExpressionStatement) stmt).expression, id, s);
        }
        else if (DeclareStatement.class.equals(stmt.getClass()))
        {
            return compileDeclare((DeclareStatement) stmt, id, s);
        }
        else if (ReturnStatement.class.equals(stmt.getClass()))
        {
            return compileReturn((ReturnStatement) stmt, id, s);
        }
        else if (BreakStatement.class.equals(stmt.getClass()))
        {
            return compileBreak((BreakStatement) stmt, id, s);
        }
        else if (ContinueStatement.class.equals(stmt.getClass()))
        {
            return compileContinue((ContinueStatement) stmt, id, s);
        }
        else if (EmptyStatement.class.equals(stmt.getClass()))
        {
            return id;
        }
        else return null;
    }

    private BigInteger compileBlock(BlockStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var sc = new Scope(s);
        return compileBlockWithoutScope(stmt, id, sc);
    }

    private BigInteger compileWhile(WhileStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var condB = newBB();
        var bodyB = newBB();
        var nextB = newBB();

        breakContinue.add(new Tuple<>(condB, nextB));
        setJump(id, new JumpInstruction(condB));
        setJump(id, new JumpInstruction(bodyB, nextB));

        var bodyEndB = compileBlock(stmt.body, bodyB, s);
        setJump(bodyEndB, new JumpInstruction(condB));

        breakContinue.remove(breakContinue.size() - 1);

        return nextB;
    }

    private void setJump(BigInteger id, JumpInstruction jump) throws CompileError
    {
        var bb = basicBlocks.get(id.intValue());
        if (bb.jump.inst == jump.inst)
        {
            bb.jump = jump;
        }
        else
        {
            throw new CompileError(ErrorCode.DuplicatedJump, new Span());
        }
    }

    private BigInteger compileIf(IfStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var endB = newBB();
        compileExpr(stmt.condition, id, s);

        var trueB = newBB();
        var trueEndB = newBB();

        setJump(trueEndB, new JumpInstruction(endB));
        BigInteger falseB;
        if (EmptyStatement.class.equals(stmt.elseBlock.getClass()))
        {
            falseB = endB;
        }
        else if (IfStatement.class.equals(stmt.elseBlock.getClass()))
        {
            var elseB = newBB();
            var elseEndB = compileIf((IfStatement) stmt.elseBlock, elseB, s);
            setJump(elseEndB, new JumpInstruction(endB));
            falseB = elseB;
        }
        else
        {
            var elseB = newBB();
            var elseEndB = compileBlock((BlockStatement) stmt.elseBlock, elseB, s);
            setJump(elseEndB, new JumpInstruction(endB));
            falseB = elseB;
        }

        setJump(id, new JumpInstruction(trueB, falseB));

        return endB;
    }

    private BigInteger compileDeclare(DeclareStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var v = Compiler.addDeclarationScope(stmt, s);

        placeMapping.put(v.first, new Place(p.Loc, loc_top));
        var size = v.second.sizeSlot();
        loc_top += size.longValue();

        var val = stmt.value.get();
        var ass = new AssignExpression(new Span(), true, new IdentExpression(stmt.name), val);
        compileAssignExpr(ass, id, s);
        return id;
    }

    private BigInteger compileExpr(Expression stmt, BigInteger id, Scope s)
    {

    }

    private BigInteger compileAssignExpr(AssignExpression expr, BigInteger id, Scope s)
    {

    }

    private BigInteger compileReturn(ReturnStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var funcTy = scope.find(func.name.name).get().ty.getFunc().get();
        var retTy = funcTy.ret;

        if (retTy.type == Ty.VOID)
        {
            if (stmt.retValue.isPresent())
            {
                throw new CompileError(ErrorCode.TypeMismatch, stmt.getSpan());
            }
        }
        else
        {
            if (stmt.retValue.isEmpty())
            {
                throw new CompileError(ErrorCode.TypeMismatch, stmt.getSpan());
            }
            else
            {
                var retId = s.find(RET_VAL_KEY).get().id;
                var offset = setJump();
            }
        }
    }

    private BigInteger compileBreak(BreakStatement stmt, BigInteger id, Scope s)
    {

    }

    private BigInteger compileContinue(ContinueStatement stmt, BigInteger id, Scope s)
    {

    }

    private BigInteger newBB()
    {
        var id = basicBlocks.size();
        basicBlocks.add(new BasicBlock());
        return BigInteger.valueOf(id);
    }

    private Tuple<BigInteger, BigInteger> addParams(Scope s)
    {

    }
}
