package c0.compiler.codegen;

import c0.compiler.*;
import c0.compiler.Compiler;
import c0.error.CompileError;
import c0.error.ErrorCode;
import c0.util.program.BBArranger;
import c0.util.program.Span;
import c0.util.program.structure.*;
import c0.util.program.structure.expression.*;
import c0.util.program.structure.operator.BinaryOperator;
import c0.util.program.structure.operator.UnaryOperator;
import c0.util.program.structure.statement.*;
import c0.vm.FunctionDefine;
import c0.vm.Op;
import c0.vm.VmOp;
import c0.vm.dataType.Uint32;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

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

        var endBB = compileBlockWithoutScope(func.body, srtBB, s);

        if (s.find(RET_VAL_KEY).get().ty.type == Ty.VOID)
        {
            setJump(endBB, new JumpInstruction(JumpInstE.Return));
        }

        var arr = bbArrange(srtBB);

        HashMap<BigInteger, BigInteger> map = new HashMap<>();
        Tuple<BigInteger, HashMap<BigInteger, BigInteger>> tuple = null;
        var acc = 0;
        for (var a : arr)
        {
            var pack = new Tuple<>(a, basicBlocks.get(a.intValue()));
            map.put(pack.first, BigInteger.valueOf(acc));
            acc += pack.second.code.size();
            switch (pack.second.jump.inst)
            {
                case JumpIf:
                    acc += 1;
                case Jump:
                case Return:
                    acc += 1;
                case Unreachable:
                case Undefined:
            }
            tuple = new Tuple<>(BigInteger.valueOf(acc), map);
        }

        assert tuple != null;
        var srtOffset = tuple.second;

        var resCode = new ArrayList<Op>();
        for (var a : arr)
        {
            var bb = basicBlocks.get(a.intValue());
            resCode.addAll(bb.code);
            switch (bb.jump.inst)
            {
                case Return:
                    resCode.add(new Op(VmOp.Ret));
                    break;
                case Jump:
                    resCode.add(new Op(
                            VmOp.Br,
                            srtOffset
                                    .get(BigInteger.valueOf(bb.jump.value.get().first.intValue()))
                                    .subtract(BigInteger.valueOf(resCode.size()))
                                    .subtract(BigInteger.ONE)

                    ));
                    break;
                case JumpIf:
                    resCode.add(new Op(
                            VmOp.BrTrue,
                            srtOffset
                                    .get(BigInteger.valueOf(bb.jump.value.get().first.intValue()))
                                    .subtract(BigInteger.valueOf(resCode.size()))
                                    .subtract(BigInteger.ONE)

                    ));
                    resCode.add(new Op(
                            VmOp.Br,
                            srtOffset
                                    .get(BigInteger.valueOf(bb.jump.value.get().second.get().intValue()))
                                    .subtract(BigInteger.valueOf(resCode.size()))
                                    .subtract(BigInteger.ONE)

                    ));
                    break;
            }
        }


        var nameValId = scope.getNewId();
        var nameGlobalId = entries.insertStringLiteral(func.name.name, nameValId);

        return new FunctionDefine(
                nameGlobalId,
                new Uint32(slot.first.longValue()),
                new Uint32(slot.second.longValue()),
                new Uint32(loc_top),
                resCode
        );
    }

    private ArrayList<BigInteger> bbArrange(BigInteger srt) throws CompileError
    {
        var arrState = new BBArranger(basicBlocks.toArray(new BasicBlock[]{}));

        arrState.constructArr(srt);

        return arrState.arrange();
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
            return compileExprStmt((ExpressionStatement) stmt, id, s);
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

    private BigInteger compileExprStmt(ExpressionStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var ty = compileExpr(stmt.expression, id, s);
        if (ty.sizeSlot().longValue() > 0)
        {
            appendCode(id, new Op(VmOp.PopN, ty.sizeSlot()));
        }
        return id;
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
        compileExpr(stmt.condition, condB, s);
        setJump(id, new JumpInstruction(condB));
        setJump(condB, new JumpInstruction(bodyB, nextB));

        var bodyEndB = compileBlock(stmt.body, bodyB, s);
        setJump(bodyEndB, new JumpInstruction(condB));

        breakContinue.remove(breakContinue.size() - 1);

        return nextB;
    }

    private void setJump(BigInteger id, JumpInstruction jump) throws CompileError
    {
        var bb = basicBlocks.get(id.intValue());
        if (bb.jump.inst == JumpInstE.Undefined)
        {
            bb.jump = jump;
        }
        else
        {
            throw new CompileError(ErrorCode.DuplicatedJump, null);
        }
    }

    private Optional<Place> getPlace(BigInteger id)
    {
        return Optional.ofNullable(placeMapping.get(id));
    }

    private BigInteger compileIf(IfStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var endB = newBB();
        compileExpr(stmt.condition, id, s);

        var trueB = newBB();
        var trueEndB = compileBlock(stmt.ifBlock, trueB, s);

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

        if (stmt.value.isPresent())
        {
            var val = stmt.value.get();
            var ass = new AssignExpression(new Span(), true, new IdentExpression(stmt.name), val);
            compileAssignExpr(ass, id, s);
        }
        return id;
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
                var offset = getPlace(retId).get();
                appendCode(id, opLoadAddress(offset));
                var ty = compileExpr(stmt.retValue.get(), id, s);
                if (ty.type != retTy.type)
                {
                    throw new CompileError(ErrorCode.TypeMismatch, stmt.getSpan());
                }
                appendCode(id, storeTy(retTy));
            }
        }
        setJump(id, new JumpInstruction(JumpInstE.Return));
        return newBB();
    }

    //TODO: probably wrong
    private BigInteger compileBreak(BreakStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var next = breakContinue.get(breakContinue.size() - 1).second;
        setJump(id, new JumpInstruction(next));
        return newBB();
    }

    private BigInteger compileContinue(ContinueStatement stmt, BigInteger id, Scope s) throws CompileError
    {
        var next = breakContinue.get(breakContinue.size() - 1).first;
        setJump(id, new JumpInstruction(next));
        return newBB();
    }

    private Type compileExpr(Expression stmt, BigInteger id, Scope s) throws CompileError
    {
        if (IdentExpression.class.equals(stmt.getClass()))
        {
            return compileIdentExpr((IdentExpression) stmt, id, s);
        }
        else if (AssignExpression.class.equals(stmt.getClass()))
        {
            return compileAssignExpr((AssignExpression) stmt, id, s);
        }
        else if (AsExpression.class.equals(stmt.getClass()))
        {
            return compileAsExpr((AsExpression) stmt, id, s);
        }
        else if (LiteralExpression.class.equals(stmt.getClass()))
        {
            return compileLiteralExpr((LiteralExpression) stmt, id, s);
        }
        else if (UnaryExpression.class.equals(stmt.getClass()))
        {
            return compileUnaryExpr((UnaryExpression) stmt, id, s);
        }
        else if (BinaryExpression.class.equals(stmt.getClass()))
        {
            return compileBinaryExpr((BinaryExpression) stmt, id, s);
        }
        else if (CallExpression.class.equals(stmt.getClass()))
        {
            return compileCallExpr((CallExpression) stmt, id, s);
        }
        throw new CompileError(ErrorCode.Unreachable, new Span());
    }

    private Type compileIdentExpr(IdentExpression expr, BigInteger id, Scope s) throws CompileError
    {
        var ty = genIdentAddr(expr, id, s).first;
        appendCode(id, loadTy(ty));
        return ty;
    }

    private Type compileAssignExpr(AssignExpression expr, BigInteger id, Scope s) throws CompileError
    {
        var l = getLValueAddr(expr.left, id, s);
        var rT = compileExpr(expr.right, id, s);

        if (l.first.type != rT.type)
        {
            throw new CompileError(ErrorCode.AssignToConst, expr.left.getSpan());
        }

        if (!expr.allowAssignConst && l.second)
        {
            throw new CompileError(ErrorCode.AssignToConst, expr.left.getSpan());
        }

        appendCode(id, storeTy(l.first));

        return new Type(Ty.VOID);
    }

    private Type compileBinaryExpr(BinaryExpression expr, BigInteger id, Scope s) throws CompileError
    {
        var lhsTy = compileExpr(expr.left, id, s);
        var rhsTy = compileExpr(expr.right, id, s);

        if (lhsTy.type != rhsTy.type)
        {
            throw new CompileError(ErrorCode.TypeMismatch, expr.right.getSpan());
        }

        var code = binaryOp(expr.op, lhsTy).get();

        for (var c : code)
        {
            appendCode(id, c);
        }

        var resTy = binaryOpResTy(expr.op, lhsTy).get();
        return resTy;
    }

    private Type compileUnaryExpr(UnaryExpression expr, BigInteger id, Scope s) throws CompileError
    {
        var lhsTy = compileExpr(expr.expression, id, s);

        var code = unaryOp(expr.op, lhsTy).get();

        for (var c : code)
        {
            appendCode(id, c);
        }

        var resTy = unaryOpResTy(expr.op, lhsTy).get();
        return resTy;
    }

    private Type compileAsExpr(AsExpression expr, BigInteger id, Scope s) throws CompileError
    {
        var lhsTy = compileExpr(expr.value, id, s);
        var rhsTy = getTyNoVoid(expr.type);

        var code = asOp(lhsTy, rhsTy).get();

        for (var c : code)
        {
            appendCode(id, c);
        }

        return rhsTy;
    }

    private Type compileLiteralExpr(LiteralExpression expr, BigInteger id, Scope s) throws CompileError
    {
        switch (expr.type)
        {
            case INT64:
            case CHAR:
                appendCode(id, new Op(VmOp.Push, BigInteger.valueOf(expr.getInt64Value())));
                return new Type(Ty.INT);
            case DOUBLE:
                appendCode(id, new Op(VmOp.Push, BigInteger.valueOf(expr.getInt64Value())));
                return new Type(Ty.DOUBLE);
            case STRING:
                var valId = s.getNewId();
                var globalId = entries.insertStringLiteral((String) expr.value, valId);
                appendCode(id, new Op(VmOp.Push, globalId.getBig()));
                return new Type(Ty.INT);
        }
        throw new CompileError(ErrorCode.Unreachable, expr.getSpan());
    }

    private Type compileCallExpr(CallExpression expr, BigInteger id, Scope s) throws CompileError
    {
        ArrayList<Type> exprTy = new ArrayList<>();
        var funcName = expr.function.name;
        var funcSig = scope.find(funcName).get();

        var funcTy = funcSig.ty.getFunc().get();

        appendCode(id, new Op(VmOp.StackAlloc, funcTy.ret.sizeSlot()));

        for (var sub : expr.params)
        {
            var ty = compileExpr(sub, id, s);
            exprTy.add(ty);
        }

        if (exprTy.size() != funcTy.params.size())
        {
            throw new CompileError(ErrorCode.TypeMismatch, expr.getSpan());
        }


        for (int i = 0; i < exprTy.size(); i++)
        {
            if (exprTy.get(i).type != funcTy.params.get(i).type)
            {
                throw new CompileError(ErrorCode.TypeMismatch, expr.params.get(i).getSpan());
            }
        }

        var retTy = funcTy.ret;
        var funcId = entries.functionId(funcName);
        if (funcId.isPresent())
        {
            appendCode(id, new Op(VmOp.Call, funcId.get().getBig()));
        }
        else
        {
            var valId = s.getNewId();
            var globalId = entries.insertStringLiteral(funcName, valId);
            appendCode(id, new Op(VmOp.CallName, globalId.getBig()));
        }

        return retTy;
    }

    private void appendCode(BigInteger id, Op code)
    {
        var b = basicBlocks.get(id.intValue());
        b.code.add(code);
    }

    private BigInteger newBB()
    {
        var id = basicBlocks.size();
        basicBlocks.add(new BasicBlock());
        return BigInteger.valueOf(id);
    }

    private Tuple<BigInteger, BigInteger> addParams(Scope s) throws CompileError
    {
        var retTy = getTy(func.returnType);
        var retSize = retTy.sizeSlot();
        var retId = s.insert(RET_VAL_KEY, new Symbol(retTy, false)).get();

        placeMapping.put(retId, new Place(p.Arg, arg_top));
        arg_top += retSize.longValue();

        if (func.params != null)
        {
            for (var param : func.params)
            {
                var paramTy = getTyNoVoid(param.type);
                var paramSize = paramTy.sizeSlot();
                var paramId = s.insert(
                        param.name.name,
                        new Symbol(paramTy, param.isConst)
                ).get();
                placeMapping.put(paramId, new Place(p.Arg, arg_top));
                arg_top += paramSize.longValue();
            }
        }

        return new Tuple<>(retSize, BigInteger.valueOf(arg_top).subtract(retSize));
    }

    private Optional<Op[]> asOp(Type from, Type to)
    {
        switch (from.type)
        {
            case INT:
            case ADDR:
                switch (to.type)
                {
                    case INT:
                    case ADDR:
                    case BOOL:
                        return Optional.of(new Op[]{});
                    case DOUBLE:
                        return Optional.of(new Op[]{new Op(VmOp.FToI)});
                    default:
                        return Optional.empty();
                }
            case DOUBLE:
                switch (to.type)
                {
                    case INT:
                        return Optional.of(new Op[]{new Op(VmOp.IToF)});
                    case DOUBLE:
                    case BOOL:
                        return Optional.of(new Op[]{});
                    default:
                        return Optional.empty();
                }
            default:
                return Optional.empty();
        }
    }

    private Optional<Op[]> binaryOp(BinaryOperator op, Type ty)
    {
        switch (ty.type)
        {
            case INT:
            case ADDR:
                switch (op)
                {
                    case Add:
                        return Optional.of(new Op[]{new Op(VmOp.AddI)});
                    case Sub:
                        return Optional.of(new Op[]{new Op(VmOp.SubI)});
                    case Mul:
                        return Optional.of(new Op[]{new Op(VmOp.MulI)});
                    case Div:
                        return Optional.of(new Op[]{new Op(VmOp.DivI)});
                    case Gt:
                        return Optional.of(new Op[]{new Op(VmOp.CmpI), new Op(VmOp.SetGt)});
                    case Lt:
                        return Optional.of(new Op[]{new Op(VmOp.CmpI), new Op(VmOp.SetLt)});
                    case Ge:
                        return Optional.of(new Op[]{new Op(VmOp.CmpI), new Op(VmOp.SetLt), new Op(VmOp.Not)});
                    case Le:
                        return Optional.of(new Op[]{new Op(VmOp.CmpI), new Op(VmOp.SetGt), new Op(VmOp.Not)});
                    case Eq:
                        return Optional.of(new Op[]{new Op(VmOp.CmpI), new Op(VmOp.Not)});
                    case Neq:
                        return Optional.of(new Op[]{new Op(VmOp.CmpI)});
                }
            case DOUBLE:
                switch (op)
                {
                    case Add:
                        return Optional.of(new Op[]{new Op(VmOp.AddF)});
                    case Sub:
                        return Optional.of(new Op[]{new Op(VmOp.SubF)});
                    case Mul:
                        return Optional.of(new Op[]{new Op(VmOp.MulF)});
                    case Div:
                        return Optional.of(new Op[]{new Op(VmOp.DivF)});
                    case Gt:
                        return Optional.of(new Op[]{new Op(VmOp.CmpF), new Op(VmOp.SetGt)});
                    case Lt:
                        return Optional.of(new Op[]{new Op(VmOp.CmpF), new Op(VmOp.SetLt)});
                    case Ge:
                        return Optional.of(new Op[]{new Op(VmOp.CmpF), new Op(VmOp.SetLt), new Op(VmOp.Not)});
                    case Le:
                        return Optional.of(new Op[]{new Op(VmOp.CmpF), new Op(VmOp.SetGt), new Op(VmOp.Not)});
                    case Eq:
                        return Optional.of(new Op[]{new Op(VmOp.CmpF), new Op(VmOp.Not)});
                    case Neq:
                        return Optional.of(new Op[]{new Op(VmOp.CmpF)});
                }
            default:
                return Optional.empty();
        }
    }

    private Optional<Type> binaryOpResTy(BinaryOperator op, Type ty)
    {
        switch (ty.type)
        {
            case INT:
            case DOUBLE:
            case ADDR:
                switch (op)
                {
                    case Add:
                    case Sub:
                    case Mul:
                    case Div:
                        return Optional.of(ty);
                    case Gt:
                    case Lt:
                    case Ge:
                    case Le:
                    case Eq:
                    case Neq:
                        return Optional.of(new Type(Ty.BOOL));
                }
            default:
                return Optional.empty();
        }
    }

    private Optional<Op[]> unaryOp(UnaryOperator op, Type ty)
    {
        switch (ty.type)
        {
            case INT:
                switch (op)
                {
                    case Neg:
                        return Optional.of(new Op[]{new Op(VmOp.NegI)});
                    case Pos:
                        return Optional.of(new Op[]{});
                }
            case DOUBLE:
                switch (op)
                {
                    case Neg:
                        return Optional.of(new Op[]{new Op(VmOp.NegF)});
                    case Pos:
                        return Optional.of(new Op[]{});
                }
            default:
                return Optional.empty();
        }
    }

    private Optional<Type> unaryOpResTy(UnaryOperator op, Type ty)
    {
        switch (ty.type)
        {
            case INT:
            case DOUBLE:
                return Optional.of(ty);
            default:
                return Optional.empty();
        }
    }

    private Op storeTy(Type ty) throws CompileError
    {
        switch (ty.type)
        {
            case INT:
            case DOUBLE:
            case BOOL:
            case ADDR:
                return new Op(VmOp.Store64);
            case VOID:
                return new Op(VmOp.Pop);
            default:
                throw new CompileError(ErrorCode.TypeMismatch, new Span());
        }
    }

    private Op loadTy(Type ty) throws CompileError
    {
        switch (ty.type)
        {
            case INT:
            case DOUBLE:
            case BOOL:
            case ADDR:
                return new Op(VmOp.Load64);
            case VOID:
                return new Op(VmOp.Pop);
            default:
                throw new CompileError(ErrorCode.TypeMismatch, new Span());
        }
    }

    private Op opLoadAddress(Place place) throws CompileError
    {
        switch (place.place)
        {
            case Arg:
                return new Op(VmOp.ArgA, BigInteger.valueOf(place.value));
            case Loc:
                return new Op(VmOp.LocA, BigInteger.valueOf(place.value));
        }
        throw new CompileError(ErrorCode.Unreachable, new Span());
    }

    private Tuple<Type, Boolean> genIdentAddr(IdentExpression i, BigInteger id, Scope s) throws CompileError
    {
        var v = s.findGlobal(i.ident.name).get();

        if (v.second)
        {
            var globalId = entries.valueId(v.first.id).get();
            appendCode(id, new Op(VmOp.GlobA, globalId.getBig()));
        }
        else
        {
            var varId = v.first.id;
            appendCode(id, opLoadAddress(getPlace(varId).get()));
        }
        return new Tuple<>(v.first.ty, v.first.isConst);
    }

    private Tuple<Type, Boolean> getLValueAddr(Expression expr, BigInteger id, Scope s) throws CompileError
    {
        if (IdentExpression.class.equals(expr.getClass()))
        {
            var i = (IdentExpression) expr;
            return genIdentAddr(i, id, s);
        }
        else
        {
            throw new CompileError(ErrorCode.TypeMismatch, expr.getSpan());
        }
    }

    private Type getTy(TypeDefine ty) throws CompileError
    {
        return Compiler.getTy(ty);
    }

    private Type getTyNoVoid(TypeDefine ty) throws CompileError
    {
        return Compiler.getTyNoVoid(ty);
    }
}
