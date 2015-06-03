package darkchessserver;


import java.util.ArrayList;



public class Bishop extends Piece
{
    public Bishop(String team)
    {
        super(team);
        setName('♝');
        setWhiteName('♗');
    }
    
    public ArrayList<Point> getMoveLogicMoves(Point p, Piece[][] grid, boolean moving)
    {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        
        for (int i = 1; i < 8; i += 2)
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
    
    public Bishop clone()
    {
        Bishop bishop = new Bishop(getTeam());
        return bishop;
    }
}
