package darkchessserver;

import java.util.*;

public class Rook extends Piece
{
    public boolean hasMovedYet;
    
    public Rook(String team)
    {
        super(team);
        setName('♜');
        setWhiteName('♖');
        hasMovedYet = false;
    }
    
    public ArrayList<Point> getMoveLogicMoves(Point p, Piece[][] grid, boolean moving)
    {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        
        for (int i = 0; i < 8; i += 2)
        {
            int multiplier = 1;
            while (true)
            {
                int x = p.getX() + getDX(i)*multiplier;
                int y = p.getY() + getDY(i)*multiplier;
                Point newP = new Point(x, y);
                if (isValidSquare(newP, grid) < 0) break;
                validMoves.add(newP);
                multiplier++;
                if (grid[newP.getY()][newP.getX()] != null) break;
            }
        }
        
        return validMoves;
    }
    
    public Rook clone()
    {
        Rook rook = new Rook(getTeam());
        rook.setHasMovedYet(getHasMovedYet());
        return rook;
    }
    
    public boolean getHasMovedYet()
    {
        return hasMovedYet;
    }
    
    public void setHasMovedYet(boolean b)
    {
        hasMovedYet = b;
    }
}
