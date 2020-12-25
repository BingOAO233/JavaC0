package c0.util;

import c0.util.LineBuffer;

import java.util.Optional;
import java.util.Scanner;

public class StringIter
{
    private LineBuffer lineBuffer;

    boolean initialized = false;

    private Optional<Character> peeked = Optional.empty();

    public StringIter(LineBuffer buffer)
    {
        lineBuffer = buffer;
    }

    /**
     * move buffer ptr to next
     *
     * @return current char
     */
    public char nextChar()
    {
        char ch;
        if (this.peeked.isPresent())
        {
            ch = this.peeked.get();
            this.peeked = Optional.empty();
        }
        else
        {
            ch = lineBuffer.getNextChar();
        }
        lineBuffer.moveNext();
        return ch;
    }

    /**
     * peek next char, without buffer ptr operation
     *
     * @return peeked char
     */
    public char peekChar()
    {
        if (peeked.isPresent())
        {
            return peeked.get();
        }
        else
        {
            char ch = lineBuffer.getNextChar();
            this.peeked = Optional.of(ch);
            return ch;
        }
    }
}
