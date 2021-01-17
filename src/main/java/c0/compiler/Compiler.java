package c0.compiler;

import c0.error.CompileError;
import c0.error.ErrorCode;
import c0.util.program.structure.Program;
import c0.util.program.structure.TypeDefine;
import c0.util.program.structure.statement.DeclareStatement;
import c0.vm.S0;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class Compiler
{
    Program program;

    public Compiler(Program program)
    {
        this.program = program;
    }

    public S0 compile()
    {
        SymbolIdGenerator globalGen = new SymbolIdGenerator();
        Scope globalScope = new Scope(globalGen);
        GlobalEntries globalEntries = new GlobalEntries(new IndexSet<>("_start"), new HashMap<>());

        ArrayList<> funcs = new ArrayList();

        createLibFunction(globalScope);

        for (var decl : program.declarations)
        {
            var
        }
    }

    private void createLibFunction(Scope scope)
    {
        scope.insert("putint", new Symbol(
                new Type(Ty.FUNC, Optional.of(
                        new FunctionTy(new ArrayList<>(Arrays.asList(new Type[]{new Type(Ty.INT)})),
                                       new Type(Ty.VOID))))
                , true)
        );
        scope.insert("putstr", new Symbol(
                new Type(Ty.FUNC, Optional.of(
                        new FunctionTy(new ArrayList<>(Arrays.asList(new Type[]{new Type(Ty.INT)})),
                                       new Type(Ty.VOID))))
                , true)
        );
        scope.insert("putdouble", new Symbol(
                new Type(Ty.FUNC, Optional.of(
                        new FunctionTy(new ArrayList<>(Arrays.asList(new Type[]{new Type(Ty.DOUBLE)})),
                                       new Type(Ty.VOID))))
                , true)
        );
        scope.insert("putchar", new Symbol(
                new Type(Ty.FUNC, Optional.of(
                        new FunctionTy(new ArrayList<>(Arrays.asList(new Type[]{new Type(Ty.INT)})),
                                       new Type(Ty.VOID))))
                , true)
        );
        scope.insert("putln", new Symbol(
                new Type(Ty.FUNC, Optional.of(
                        new FunctionTy(new ArrayList<>(),
                                       new Type(Ty.VOID))))
                , true)
        );
        scope.insert("getchar", new Symbol(
                new Type(Ty.FUNC, Optional.of(
                        new FunctionTy(new ArrayList<>(),
                                       new Type(Ty.INT))))
                , true)
        );
        scope.insert("getint", new Symbol(
                new Type(Ty.FUNC, Optional.of(
                        new FunctionTy(new ArrayList<>(),
                                       new Type(Ty.INT))))
                , true)
        );
        scope.insert("getdouble", new Symbol(
                new Type(Ty.FUNC, Optional.of(
                        new FunctionTy(new ArrayList<>(),
                                       new Type(Ty.DOUBLE))))
                , true)
        );
    }

    private Tuple<BigInteger, Type> addDeclarationScope(DeclareStatement stmt, Scope scope) throws CompileError
    {
        var ty = getTy(stmt.type);
        if (ty.type == Ty.VOID)
        {
            throw new CompileError(ErrorCode.UnexpectedType, stmt.getSpan());
        }
        var name = stmt.name.name;

        var symbol = new Symbol(ty, stmt.isConst);

        var id = scope.insert(name, symbol);

        if (id.isPresent())
        {
            return new Tuple<BigInteger, Type>(id.get(), ty);
        }
        throw new CompileError(ErrorCode.UnexpectedType, stmt.name.span);
    }

    private Type getTy(TypeDefine ty) throws CompileError
    {
        switch (ty.name)
        {
            case "int":
                return new Type(Ty.INT);
            case "double":
                return new Type(Ty.DOUBLE);
            case "void":
                return new Type(Ty.VOID);
            default:
                throw new CompileError(ErrorCode.UnexpectedType, ty.span);
        }

    }
}
