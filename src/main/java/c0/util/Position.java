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
}
