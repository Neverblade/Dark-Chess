package darkchessserver;


import java.util.ArrayList;



public class Pawn extends Piece
{
    private String dirString;
    private int dir;
    private int pawnLogic;
    
    public Pawn(String team, String dir)
    {
        super(team);
        setName('♟');
        setWhiteName('♙');
        this.dirString = dir;
        this.dir = getDirIndex(dir);
        pawnLogic = 3; //3 is not moved yet, 2 means it moved, but it's quickly changed to 1, which means it just did a double push
    }

    public ArrayList<Point> getMoveLogicMoves(Point p, Piece[][] grid, boolean moving)
    {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        for (int i = dir - 1; i <= dir + 1; i++)
        {
            int nDir = i % 8;
            Point nP = new Point(p.getX() + getDX(nDir), p.getY() + getDY(nDir));            
            if (nDir == dir) //moving forward
            {
                if (isValidSquare(nP, grid) == 0)
                {
                    validMoves.add(nP);
                    Point dP = new Point(p.getX() + getDX(nDir)*2, p.getY() + getDY(nDir)*2);
                    if (isValidSquare(dP, grid) == 0 && pawnLogic == 3) validMoves.add(dP);
                }
            } else if (isValidSquare(nP, grid) == 1) //looking at diagonals
            {
                validMoves.add(nP);
            }
        }
        
        /* En Passant checking logic */
        for (int i = dir - 2; i <= dir + 2; i+= 4)
        {
            int nDir = i % 8;
            Point nP = new Point(p.getX() + getDX(nDir), p.getY() + getDY(nDir));
            if (isValidSquare(nP, grid) == 1 && grid[nP.getY()][nP.getX()].getName() == '♟') //if there's a pawn to the left/right
            {
                Pawn pawn = (Pawn) grid[nP.getY()][nP.getX()];
                if (pawn.getPawnLogic() == 1) //opponent's pawn just did a double pawn push
                {
                    int dDir = (i + dir) / 2;
                    Point dP = new Point(p.getX() + getDX(dDir), p.getY() + getDY(dDir));
                    validMoves.add(dP);
                }
            }
        }
        
        return validMoves;
    }
    
    public Pawn clone()
    {
        Pawn pawn = new Pawn(getTeam(), dirString);
        pawn.setPawnLogic(getPawnLogic());
        return pawn;
    }
    
    public int getPawnLogic()
    {
        return pawnLogic;
    }
    
    public void setPawnLogic(int x)
    {
        pawnLogic = x;
    }
    
    public int getDir()
    {
        return dir;
    }
}
