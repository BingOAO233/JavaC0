package c0.util.program.structure.expression;

import c0.util.program.Span;
import c0.util.program.structure.Ident;

public class Expression
{

}

//public enum Expression
//{
//    IDENT(new Ident()),
//    ASSIGN(new AssignExpression()),
//    AS(new AsExpression()),
//    LITERAL(new LiteralExpression()),
//    UNARY(new UnaryExpression()),
//    BINARY(new BinaryExpression()),
//    CALL(new CallExpression());
//
//    private Object expr;
//
//    Expression(Object expr)
//    {
//        this.expr = expr;
//    }
//
//    public Span getSpan()
//    {
//        switch (this)
//        {
//            case IDENT:
//                return ((Ident) expr).span;
//            case ASSIGN:
//                return ((AssignExpression) expr).span;
//            case AS:
//                return ((AsExpression) expr).span;
//            case LITERAL:
//                return ((LiteralExpression) expr).span;
//            case UNARY:
//                return ((UnaryExpression) expr).span;
//            case BINARY:
//                return ((BinaryExpression) expr).span;
//            case CALL:
//                return ((CallExpression) expr).span;
//        }
//        return null;
//    }
//}