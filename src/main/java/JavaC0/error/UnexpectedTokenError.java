package JavaC0.error;

import JavaC0.tokenizer.token.Token;
import JavaC0.tokenizer.token.TokenType;
import JavaC0.util.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnexpectedTokenError extends C0Error
{
    private static final long serialVersionUID = 1L;
    List<TokenType> expectTokenType;
    Token token;

    public UnexpectedTokenError(TokenType[] tt, Token tok)
    {
        super(ErrorCode.UnexpectedToken);
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
        return String
                .format("Analyse Error: expect token type %s on %s, get: [%s]", expectTokenType, token.getStartPos(),
                        token.getTokenType());
    }

    @Override
    public ErrorCode getErr()
    {
        return ErrorCode.UnexpectedToken;
    }

    public Position getPos()
    {
        return token.getStartPos();
    }
}
