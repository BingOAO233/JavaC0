package JavaC0.compiler;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Optional;

public class Scope
{
    private SymbolIdGenerator generator;
    public Optional<Scope> parent;
    public HashMap<String, Symbol> vars;

    public Scope(SymbolIdGenerator gen)
    {
        this.generator = gen;
        this.parent = Optional.empty();
        this.vars = new HashMap<>();
    }

    public Scope(Scope parent)
    {
        this.generator = parent.generator;
        this.parent = Optional.of(parent);
        this.vars = new HashMap<>();
    }

    public Optional<Symbol> findSelf(String ident)
    {
        return Optional.ofNullable(vars.get(ident));
    }

    public Optional<Symbol> find(String ident)
    {
        var res = findSelf(ident);
        if (res.isEmpty())
        {
            if (parent.isPresent())
            {
                return parent.get().find(ident);
            }
        }
        return res;
    }

    public boolean isRootScope()
    {
        return parent.isEmpty();
    }

    public Optional<Tuple<Symbol, Boolean>> findGlobal(String ident)
    {
        var res = findSelf(ident);

        if (res.isEmpty())
        {
            if (parent.isPresent())
            {
                return parent.get().findGlobal(ident);
            }
        }

        return Optional.of(new Tuple<>(res.get(), isRootScope()));
    }

    public Optional<BigInteger> insert(String ident, Symbol symbol)
    {
        if (vars.containsKey(ident))
            return Optional.empty();
        else
        {
            var id = generator.next();
            symbol.id = id;
            vars.put(ident, symbol);
            return Optional.ofNullable(id);
        }
    }

    public BigInteger getNewId()
    {
        return generator.next();
    }
}
