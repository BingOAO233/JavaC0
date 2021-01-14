package c0.error;

import c0.tokenizer.Token;
import c0.tokenizer.TokenType;
import c0.util.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnexpectedTokenError extends CompileError
{
    private static final long serialVersionUID = 1L;
    List<TokenType> expectTokenType;
    Token token;

    public UnexpectedTokenError(TokenType[] tt, Token tok)
    {
        expectTokenType = new ArrayList<>();
        expectTokenType.addAll(Arrays.asList(tt));
        token = tok;
    }

    public UnexpectedTokenError(TokenType tt, Token tok)
    {
        this(new TokenType[]{tt}, tok);
    }


    @Override
    public String toString()
    {
        return String.format("Analyse Error: expect token type [%s] on %s, get: [%s]", expectTokenType, token.getStartPos(), token.getTokenType());
    }

    @Override
    public ErrorCode getErr()
    {
        return ErrorCode.UnexpectedToken;
    }

    @Override
    public Position getPos()
    {
        return token.getStartPos();
    }
}
