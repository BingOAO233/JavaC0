package JavaC0.util.program.structure;

import JavaC0.util.program.Span;

import java.util.ArrayList;
import java.util.Optional;

public class TypeDefine
{
    public Span span;
    public String name;
    public Optional<ArrayList<TypeDefine>> params;

    public TypeDefine(Span span, String name, Optional<ArrayList<TypeDefine>> params)
    {
        this.span = span;
        this.name = name;
        this.params = params;
    }
}
