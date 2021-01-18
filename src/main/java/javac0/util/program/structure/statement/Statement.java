package javac0.util.program.structure.statement;


import javac0.util.program.Span;

public class Statement
{
    protected Span span;

    public Statement(Span span)
    {
        this.span = span;
    }

    public Span getSpan()
    {
        return span;
    }
}

//
//public enum Statement
//{
//    Block(new BlockStatement()),
//    While(new WhileStatement()),
//    If(new IfStatement()),
//    Expr(Expression.AS),
//    Declare(new DeclareStatement()),
//    Return(new ReturnStatement()),
//    Break(new Span()),
//    Continue(new Span()),
//    Empty(new Span());
//
//    private Object stmt;
//
//    Statement(Object stmt)
//    {
//        this.stmt = stmt;
//    }
//
//    public Span getSpan()
//    {
//        switch (this)
//        {
//            case Block:
//                return ((BlockStatement) stmt).span;
//            case While:
//                return ((WhileStatement) stmt).span;
//            case If:
//                return ((IfStatement) stmt).span;
//            case Expr:
//                return ((Expression) stmt).getSpan();
//            case Declare:
//                return ((DeclareStatement) stmt).span;
//            case Return:
//                return ((ReturnStatement) stmt).span;
//            case Empty:
//            case Break:
//            case Continue:
//                return (Span) stmt;
//        }
//        return null;
//    }
//}

