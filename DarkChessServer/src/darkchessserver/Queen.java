package darkchessserver;


import java.util.ArrayList;



public class Queen extends Piece
{
    public Queen(String team)
    {
        super(team);
        setName('♛');
        setWhiteName('♕');
    }

    public ArrayList<Point> getMoveLogicMoves(Point p, Piece[][] grid, boolean moving)
    {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        
        for (int i = 0; i < 8; i++)
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
    
    public Queen clone()
    {
        Queen queen = new Queen(getTeam());
        return queen;
    }
}
