package JavaC0.util.tokenizer;

import JavaC0.util.Position;
import JavaC0.util.console.BetterLogger;

import java.util.Optional;
import java.util.Scanner;

public class StringIter
{
    private Scanner scanner;

    private LineBuffer lineBuffer;

    boolean initialized = false;

    private Optional<Character> peeked = Optional.empty();

    public StringIter(Scanner s)
    {
        scanner = s;
        lineBuffer = new LineBuffer();
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

    /**
     * read in function
     */
    public void readAll()
    {
        if (initialized)
            return;
        while (scanner.hasNextLine())
        {
            lineBuffer.append(scanner.nextLine() + '\n');
        }
        BetterLogger.notify("Source", '\n' + lineBuffer.toString());
        initialized = true;
    }

    public boolean isEOF()
    {
        return lineBuffer.isEOF();
    }

    public Position currentPos()
    {
        return lineBuffer.currentPos();
    }

    public Position previousPos()
    {
        return lineBuffer.previousPos();
    }

    public Position nextPos()
    {
        return lineBuffer.nextPos();
    }
}
