package darkchessserver;

import java.util.ArrayList;

public class King extends Piece
{
    private boolean hasMovedYet;
    
    public King(String team)
    {
        super(team);
        setName('♚');
        setWhiteName('♔');
        hasMovedYet = false;
    }
    
    public ArrayList<Point> getMoveLogicMoves(Point p, Piece[][] grid, boolean moving)
    {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        for (int i = p.getY()-1; i <= p.getY()+1; i++)
        {
            for (int j = p.getX()-1; j <= p.getX()+1; j++)
            {
                if (isValidSquare(new Point(j, i), grid) >= 0) validMoves.add(new Point(j, i));
            }
        }
        
        /* Castling check */
        if (moving && !hasMovedYet)
        {
            //look in the 4 cardinal directions
            for (int i = 0; i < 8; i += 2)
            {
                boolean c = false;
                int multi = 1;
                Point fP = new Point(p.getX() + getDX(i)*2, p.getY() + getDY(i)*2);
                
                while (true) //loop for white space, break true on teammate rook, break false on anything else
                {
                    Point nP = new Point(p.getX() + getDX(i)*multi, p.getY() + getDY(i)*multi);
                    if (isValidSquare(nP, grid) == -2) break; //break if out of bounds
                    else if (isValidSquare(nP, grid) == 0) {} //do nothing if null
                    else if (isValidSquare(nP, grid) == -1) //do additional checks if its your teammate
                    {
                        //it's a rook and it's on your team and it hasn't been moved
                        if (grid[nP.getY()][nP.getX()].getName() == '♜' && grid[nP.getY()][nP.getX()].getTeam().equals(getTeam()))
                        {
                            Rook rook = (Rook) grid[nP.getY()][nP.getX()];
                            if (!rook.getHasMovedYet())
                            {
                                c = true;
                                break;
                            } else break;
                        } else break;
                    }
                    multi++;
                }
                
                if (c) validMoves.add(fP); //everything is valid. add the point.
                
            }
        }
    
        return validMoves;
    }
    
    public King clone()
    {
        King king = new King(getTeam());
        king.setHasMovedYet(getHasMovedYet());
        return king;
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
