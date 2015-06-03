package darkchessserver;



public class Point
{
    private int x;
    private int y;
    
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public boolean equals(Point other)
    {
        return (x == other.getX() && y == other.getY());
    }
    
    public String toString()
    {
        return "(x = " + x + ", y = " + y + ")";
    }
}
