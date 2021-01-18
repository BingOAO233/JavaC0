package javac0.tokenizer;

import javac0.error.ErrorCode;
import javac0.error.TokenizeError;
import javac0.tokenizer.token.Token;
import javac0.tokenizer.token.TokenType;
import javac0.util.Position;
import javac0.util.tokenizer.StringIter;

import java.math.BigInteger;
import java.util.HashMap;

public class Tokenizer
{
    private StringIter iter;

    public Tokenizer(StringIter it)
    {
        iter = it;
    }

    private static final HashMap<String, TokenType> KEYWORDS = new HashMap<>();

    static
    {
        KEYWORDS.put("fn", TokenType.FN_KW);
        KEYWORDS.put("let", TokenType.LET_KW);
        KEYWORDS.put("const", TokenType.CONST_KW);
        KEYWORDS.put("as", TokenType.AS_KW);
        KEYWORDS.put("while", TokenType.WHILE_KW);
        KEYWORDS.put("if", TokenType.IF_KW);
        KEYWORDS.put("else", TokenType.ELSE_KW);
        KEYWORDS.put("return", TokenType.RETURN_KW);
        KEYWORDS.put("break", TokenType.BREAK_KW);
        KEYWORDS.put("continue", TokenType.CONTINUE_KW);
    }

    public Token nextToken() throws TokenizeError
    {
        iter.readAll();

        skipSpaceCharacters();

        if (iter.isEOF())
        {
            return new Token(TokenType.EOF, "", iter.currentPos(), iter.currentPos());
        }

        char peek = iter.peekChar();

        if (Character.isDigit(peek))
        {
            return lexNumber();
        }
        else if (peek == '\"')
        {
            return lexString();
        }
        else if (Character.isAlphabetic(peek) || peek == '_')
        {
            return lexIdentOrKeyword();
        }
        else
        {
            return lexOperatorOrUnknown();
        }

    }

    private Token lexString()
    {
        StringBuilder s = new StringBuilder();
        iter.nextChar();
        var c = iter.peekChar();
        var srt = iter.currentPos();
        while (c != '\"')
        {
            if (c == '\\')
            {
                char a = iter.nextChar();
                switch (a)
                {
                    case 'n':
                        s.append('\n');
                        break;
                    case 't':
                        s.append('\t');
                        break;
                    case 'r':
                        s.append('\r');
                        break;
                    default:
                        s.append(a);
                }
            }
            else
            {
                s.append(iter.nextChar());
            }
            c = iter.peekChar();
        }
        iter.nextChar();
        return new Token(TokenType.STR, s.toString(), srt, iter.currentPos());
    }

    private Token lexNumber() throws TokenizeError
    {
        // TODO: double data type support
        return lexInt64();
    }

    private Token lexInt64() throws TokenizeError
    {
        char peek = iter.peekChar();
        Position srtPos = iter.currentPos();
        StringBuilder int64Raw = new StringBuilder();
        while (Character.isDigit(peek))
        {
            int64Raw.append(iter.nextChar());
            peek = iter.peekChar();
        }

        BigInteger res = new BigInteger("-1");
        try
        {
            res = new BigInteger(int64Raw.toString());
        } catch (Exception e)
        {
            throw new TokenizeError(ErrorCode.InvalidInt64, iter.currentPos());
        }

        if (res.compareTo(new BigInteger("9223372036854775807")) > 0)
            throw new TokenizeError(ErrorCode.Int64Overflow, iter.currentPos());
        return new Token(TokenType.INT64, res.longValue(), srtPos, iter.currentPos());
    }

    private Token lexIdentOrKeyword() throws TokenizeError
    {
        char peek = iter.peekChar();
        Position srtPos = iter.currentPos();
        StringBuilder keyIdentRaw = new StringBuilder();
        while (Character.isDigit(peek) || Character.isAlphabetic(peek) || peek == '_')
        {
            keyIdentRaw.append(iter.nextChar());
            peek = iter.peekChar();
        }
        if (KEYWORDS.containsKey(keyIdentRaw.toString()))
            return new Token(KEYWORDS.get(keyIdentRaw.toString()), keyIdentRaw.toString(), srtPos, iter.currentPos());
        return new Token(TokenType.IDENT, keyIdentRaw.toString(), srtPos, iter.currentPos());
    }


    private Token lexOperatorOrUnknown() throws TokenizeError
    {
        switch (iter.nextChar())
        {
            case '+':
                // +
                return new Token(TokenType.PLUS, "+", iter.previousPos(), iter.currentPos());

            case '-':
                if (iter.peekChar() == '>')
                {
                    // ->
                    Position srtPos = iter.previousPos();
                    iter.nextChar();
                    return new Token(TokenType.ARROW, "->", srtPos, iter.currentPos());
                }
                // -
                return new Token(TokenType.MINUS, "-", iter.previousPos(), iter.currentPos());

            case '*':
                // *
                return new Token(TokenType.MUL, "*", iter.previousPos(), iter.currentPos());

            case '/':
                // /
                if (iter.peekChar() == '/')
                {
                    // //
                    iter.nextChar();
                    Position srt = iter.previousPos();
                    StringBuilder value = new StringBuilder("//");
                    for (char c = iter.nextChar(); c != '\n'; c = iter.nextChar())
                    {
                        value.append(c);
                    }
                    return new Token(TokenType.COMMENT, value.toString(), srt, iter.currentPos());
                }
                return new Token(TokenType.DIV, "/", iter.previousPos(), iter.currentPos());

            case '=':
                if (iter.peekChar() == '=')
                {
                    // ==
                    Position srtPos = iter.previousPos();
                    iter.nextChar();
                    return new Token(TokenType.EQ, "==", srtPos, iter.currentPos());
                }
                // =
                return new Token(TokenType.ASSIGN, "=", iter.previousPos(), iter.currentPos());

            case '<':
                if (iter.peekChar() == '=')
                {
                    // <=
                    Position srtPos = iter.previousPos();
                    iter.nextChar();
                    return new Token(TokenType.LE, "<=", srtPos, iter.currentPos());
                }
                // <
                return new Token(TokenType.LT, "<", iter.previousPos(), iter.currentPos());

            case '>':
                if (iter.peekChar() == '=')
                {
                    // >=
                    Position srtPos = iter.previousPos();
                    iter.nextChar();
                    return new Token(TokenType.GE, ">=", srtPos, iter.currentPos());
                }
                // >
                return new Token(TokenType.GT, ">", iter.previousPos(), iter.currentPos());

            case '(':
                // (
                return new Token(TokenType.L_PAREN, "(", iter.previousPos(), iter.currentPos());

            case ')':
                // )
                return new Token(TokenType.R_PAREN, ")", iter.previousPos(), iter.currentPos());

            case '{':
                // {
                return new Token(TokenType.L_BRACE, "{", iter.previousPos(), iter.currentPos());

            case '}':
                // }
                return new Token(TokenType.R_BRACE, "}", iter.previousPos(), iter.currentPos());

            case ',':
                // ,
                return new Token(TokenType.COMMA, ",", iter.previousPos(), iter.currentPos());

            case ':':
                // :
                return new Token(TokenType.COLON, ":", iter.previousPos(), iter.currentPos());

            case ';':
                // ;
                return new Token(TokenType.SEMICOLON, ";", iter.previousPos(), iter.currentPos());

            case '!':
                if (iter.peekChar() == '=')
                {
                    // !=
                    Position srtPos = iter.previousPos();
                    iter.nextChar();
                    return new Token(TokenType.NEQ, "!=", srtPos, iter.currentPos());
                }
                throw new TokenizeError(ErrorCode.UnknownToken, iter.previousPos());

            default:
                throw new TokenizeError(ErrorCode.UnknownToken, iter.previousPos());
        }
    }

    private void skipSpaceCharacters()
    {
        while (!iter.isEOF() && Character.isWhitespace(iter.peekChar()))
        {
            iter.nextChar();
        }
    }
}
