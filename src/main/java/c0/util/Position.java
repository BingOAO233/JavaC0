package c0.util;


public class Position
{
    public int row;
    public int col;

    public Position()
    {
        this.row = 0;
        this.col = 0;
    }

    public Position(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public Position(Position ptr)
    {
        this.row = ptr.row;
        this.col = ptr.col;
    }

    public Position nextCol()
    {
        return new Position(row, col + 1);
    }

    public Position nextRow()
    {
        return new Position(row + 1, 0);
    }

    /**
     * compare a ? b
     *
     * @param a position a
     * @param b position b
     * @return false if a < b, true if a > b
     */
    public static boolean compare(Position a, Position b)
    {
        if (a.row != b.row)
            return a.row > b.row;
        return a.col > b.col;
    }

    /**
     * return max (a, b)
     *
     * @param a position a
     * @param b position b
     * @return Larger one in a & b
     */
    public static Position max(Position a, Position b)
    {
        if (compare(a, b))
            return a;
        return b;
    }

    /**
     * return min (a, b)
     *
     * @param a position a
     * @param b position b
     * @return Smaller one in a & b
     */
    public static Position min(Position a, Position b)
    {
        if (compare(a, b))
            return b;
        return a;
    }

    @Override
    public Position clone() throws CloneNotSupportedException
    {
        return (Position) super.clone();
    }
}
