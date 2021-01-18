package c0.util.tokenizer;

import c0.util.Position;

import java.util.ArrayList;

public class LineBuffer
{
    ArrayList<String> buffer = new ArrayList<>();

    Position ptrNxt = new Position(0, 0);

    Position ptr = new Position(0, 0);

    /**
     * get buffer previous position
     *
     * @return Previous Position
     * @throws BufferError Illegal position
     */
    public Position previousPos()
    {
        if (ptr.row == 0 && ptr.col == 0)
        {
            throw new BufferError("Previous from the Beginning");
        }
        if (ptr.col == 0)
        {
            return new Position(ptr.row - 1, buffer.get(ptr.row - 1).length() - 1);
        }
        return new Position(ptr.row, ptr.col - 1);
    }

    /**
     * get buffer current position
     *
     * @return Current Position
     */
    public Position currentPos()
    {
        return new Position(ptr);
    }

    /**
     * get buffer next position
     *
     * @return Next Position
     * @throws BufferError Illegal position
     */
    public Position nextPos()
    {
        if (ptr.row >= buffer.size())
        {
            throw new BufferError("Exceed file size, already reach EOF.");
        }
        if (ptr.col == buffer.get(ptr.row).length() - 1)
        {
            return new Position(ptr.row + 1, 0);
        }
        return new Position(ptr.row, ptr.col + 1);
    }

    /**
     * move ptr
     */
    public void moveNext()
    {
        ptr = ptrNxt;
    }

    /**
     * get next char
     *
     * @return next char in buffer
     */
    public char getNextChar()
    {
        if (isEOF())
        {
            return 0;
        }
        char ch = buffer.get(ptrNxt.row).charAt(ptrNxt.col);
        ptrNxt = nextPos();
        return ch;
    }

    /**
     * check if reach the End Of File
     *
     * @return T/F
     */
    public Boolean isEOF()
    {
        return ptr.row >= buffer.size();
    }

    public void append(String line)
    {
        buffer.add(line);
    }

    @Override
    public String toString()
    {
        var res = String.valueOf(buffer);
        return res.substring(1, res.length() - 1);
    }
}

class BufferError extends Error
{
    public BufferError()
    {

    }

    public BufferError(String message)
    {
        super(message);
    }
}