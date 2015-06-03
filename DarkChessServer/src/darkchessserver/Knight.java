package darkchessserver;


import java.util.ArrayList;



public class Knight extends Piece
{
    public Knight(String team)
    {
        super(team);
        setName('♞');
        setWhiteName('♘');
    }

    public ArrayList<Point> getMoveLogicMoves(Point p, Piece[][] grid, boolean moving)
    {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        
        for (int i = 0; i < 8; i += 2)
        {
            for (int j = 0; j < 8; j += 2)
            {
                if (i != j && i != (j+4)%8)
                {
                    int x = p.getX() + 2*getDX(i) + getDX(j);
                    int y = p.getY() + 2*getDY(i) + getDY(j);
                    if (isValidSquare(new Point(x, y), grid) >= 0) validMoves.add(new Point(x, y));
                }
            }
        }
        
        return validMoves;
    }
    
    public Knight clone()
    {
        Knight knight = new Knight(getTeam());
        return knight;
    }
}
