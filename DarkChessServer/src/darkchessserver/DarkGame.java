package darkchessserver;


import java.util.ArrayList;
import java.util.Scanner;

public class DarkGame
{
    private DarkChessServer server;
    private Piece[][] grid;
    private ArrayList<String> teams;
    private ArrayList<ArrayList<Piece>> captured;
    private int currentTeamIndex;
    private Point currentPoint;
    
    public DarkGame(DarkChessServer server)
    {        
        this.server = server;
        teams = new ArrayList<String>();
        teams.add("WHITE");
        teams.add("BLACK");
        currentTeamIndex = 0;
        currentPoint = new Point(-1, -1);
        initGrid();
        captured = new ArrayList<ArrayList<Piece>>();
        for (int i = 0; i < teams.size(); i++)
        {
            captured.add(new ArrayList<Piece>());
        }
        
        //for debugging purposes
        //board.update();
    }
    
    /* Sets up the chess board with pieces */
    public void initGrid()
    {
        grid = new Piece[8][8];
        for (int i = 0; i < grid.length; i++)
        {
            for (int j = 0; j < grid[i].length; j++)
            {
                grid[i][j] = null;
            }
        }
        
        /* White Team */
        grid[7][0] = new Rook(teams.get(0));
        grid[7][1] = new Knight(teams.get(0));
        grid[7][2] = new Bishop(teams.get(0));
        grid[7][3] = new Queen(teams.get(0));
        grid[7][4] = new King(teams.get(0));
        grid[7][5] = new Bishop(teams.get(0));
        grid[7][6] = new Knight(teams.get(0));
        grid[7][7] = new Rook(teams.get(0));
        for (int i = 0; i < 8; i++)
        {
            grid[6][i] = new Pawn(teams.get(0), "NORTH");
        }
        
        
        /* Black Team */
        grid[0][0] = new Rook(teams.get(1));
        grid[0][1] = new Knight(teams.get(1));
        grid[0][2] = new Bishop(teams.get(1));
        grid[0][3] = new Queen(teams.get(1));
        grid[0][4] = new King(teams.get(1));
        grid[0][5] = new Bishop(teams.get(1));
        grid[0][6] = new Knight(teams.get(1));
        grid[0][7] = new Rook(teams.get(1));
        for (int i = 0; i < 8; i++)
        {
            grid[1][i] = new Pawn(teams.get(1), "SOUTH");
        }
    }
    
    /* Click Logic */
    public int click(Point p)
    {        
        /* Attempt to first move to given point, using the currentPoint as a base */
        boolean moveSuccessful = attemptMove(currentPoint, p, grid);

        /* Perform special logic if a move is made */
        if (moveSuccessful)
        {
            /* Specific movement logic */
            if (grid[p.getY()][p.getX()].getName() == '♚') //king moving
            {
                King king = (King) grid[p.getY()][p.getX()];
                king.setHasMovedYet(true);                
            } else if (grid[p.getY()][p.getX()].getName() == '♜') //rook moving
            {
                Rook rook = (Rook) grid[p.getY()][p.getX()];
                rook.setHasMovedYet(true);
            } else if (grid[p.getY()][p.getX()].getName() == '♟') //pawn logic
            {
                Pawn pawn = (Pawn) grid[p.getY()][p.getX()];               
                if (Math.abs(p.getX() - currentPoint.getX()) == 2 || Math.abs(p.getY() - currentPoint.getY()) == 2) pawn.setPawnLogic(2); //marking for double pawn push
                else pawn.setPawnLogic(1);
                
                //addition pawn logic: check for piece promotion
                if (p.getY() == 0 || p.getY() == grid.length-1)
                {
                    String pieceName = server.requestPiece();
                    transform(pieceName, p.getX(), p.getY());
                }
            }
            
            /* Scanning housework */
            boolean whiteVictory = true;
            boolean blackVictory = true;
            for (int i = 0; i < grid.length; i++)
            {
                for (int j = 0; j < grid[i].length; j++)
                {
                    if (grid[i][j] != null)
                    {
                        if (grid[i][j].getName() == '♟')
                        {
                            Pawn pawn = (Pawn) grid[i][j];
                            if (pawn.getPawnLogic() < 3) pawn.setPawnLogic(pawn.getPawnLogic() - 1);
                        } else if (grid[i][j].getName() == '♚')
                        {
                            if (grid[i][j].getTeam().equals("WHITE")) blackVictory = false;
                            else if (grid[i][j].getTeam().equals("BLACK")) whiteVictory = false;
                        }
                    }
                }
            }
            
            //Check for victory (remember you can take kings in this version)
            if (whiteVictory) server.selectWinner("WHITE");
            else if (blackVictory) server.selectWinner("BLACK");
        }
        
        /* If that doesn't work, we try to select that space as a new currentPoint */
        if (!moveSuccessful) selectPiece(p);
        
        /* If it did work, switch sides and reset current point */
        else 
        {
            currentTeamIndex = (currentTeamIndex + 1) % teams.size();
            currentPoint = new Point(-1, -1);
        }
        
        //return 1 for move made, 0 for selection made, -1 for nothing made
        if (moveSuccessful) return 1;
        else if (currentPoint.getX() == -1 && currentPoint.getY() == -1) return 0;
        else return -1;
    }
    
    /* Select a piece  - HAS -1 SUPPOERT */
    public void selectPiece(Point p)
    {
        if (p.getY() == currentPoint.getY() && p.getX() == currentPoint.getX()) //reslect logic
        {
            currentPoint = new Point(-1, -1);
            //System.out.println("Selection successful.");
        } else if (p.getY() >= 0 && p.getY() < grid.length && p.getX() >= 0 && p.getX() < grid[p.getY()].length) //in bounds
        {
            //and also is a square filled by one of the current team's pieces
            if (grid[p.getY()][p.getX()] != null && grid[p.getY()][p.getX()].getTeam().equals(teams.get(currentTeamIndex)))
            {
                currentPoint = p;
                //System.out.println("Selection successful.");
            } else
            {
                currentPoint = new Point(-1, -1);
                //System.out.println("Selection failed.");
            }
        } else
        {
            currentPoint = new Point(-1, -1);
            //System.out.println("Selection failed.");
        }
    }
    
    /* Attempts to move a piece, returns false if it doesn't work - HAS -1 SUPPORT*/
    public boolean attemptMove(Point p1, Point p2, Piece[][] grid)
    {
        if (isValidMove(p1, p2))
        {
            move(p1, p2, grid);
            return true;
        }
        return false;
    }
    
    /* Actually move the pieces */
    public void move(Point p1, Point p2, Piece[][] grid)
    {
        /* Check first to see if it's an en passant */
        if (grid[p1.getY()][p1.getX()].getName() == '♟' && p1.getY() != p2.getY() && p1.getX() != p2.getX() && grid[p2.getY()][p2.getX()] == null)
        {
            Pawn pawn = (Pawn) grid[p1.getY()][p1.getX()]; //grab the pawn
            Point fP = new Point(p1.getX() + pawn.getDX(pawn.getDir()), p1.getY() + pawn.getDY(pawn.getDir())); //get foward direction point
            Point nP1 = new Point(p1.getX(), p2.getY());
            Point nP2 = new Point(p2.getX(), p1.getY());
            if (fP.getX() == nP1.getX() && fP.getY() == nP1.getY())
            {
                captured.get(currentTeamIndex).add(grid[nP2.getY()][nP2.getX()]);
                grid[nP2.getY()][nP2.getX()] = null;
            }
            else
            {
                captured.get(currentTeamIndex).add(grid[nP1.getY()][nP1.getX()]);
                grid[nP1.getY()][nP1.getX()] = null;
            }
        }
        
        /* Check to see if you castled */
        else if (grid[p1.getY()][p1.getX()].getName() == '♚')
        {
            int dX = p2.getX() - p1.getX();
            int dY = p2.getY() - p1.getY();
            if (dX == 2 || dY == 2 || dX == -2 || dY == -2)
            {
                if (dX > 0) dX = 1;
                else if (dX == 0) dX = 0;
                else dX = -1;
                if (dY > 0) dY = 1;
                else if (dY == 0) dY = 0;
                else dY = -1;
                
                System.out.println("dX: " + dX + " dY: " + dY);
                
                Point nP = new Point(-1, -1);
                int mult = 1;
                while (true)
                {
                    nP = new Point(p2.getX() + dX*mult, p2.getY() + dY*mult);
                    System.out.println(nP);
                    if (grid[nP.getY()][nP.getX()] != null)
                    {
                        char med = grid[nP.getY()][nP.getX()].getName();
                        if (med == '♜')
                        {
                            Point rP = new Point(p1.getX() + dX, p1.getY() + dY);
                            move(nP, rP, grid);
                            break;
                        }
                    }
                    mult++;
                }
            }
        }
        
        if (isValidSquare(p2, grid) == 1) captured.get(currentTeamIndex).add(grid[p2.getY()][p2.getX()]);
        grid[p2.getY()][p2.getX()] = grid[p1.getY()][p1.getX()];
        grid[p1.getY()][p1.getX()] = null; 
    }
    
    /* Checks if a move made is a valid one - HAS -1 SUPPORT */
    public boolean isValidMove(Point p1, Point p2)
    {
        if (p1.getY() < 0 || p1.getY() >= grid.length || p1.getX() < 0 || p1.getX() >= grid[p1.getY()].length) return false;
        if (p2.getY() < 0 || p2.getY() >= grid.length || p2.getX() < 0 || p2.getX() >= grid[p2.getY()].length) return false;
        if (grid[p1.getY()][p1.getX()] == null) return false;
        ArrayList<Point> validMoves = grid[p1.getY()][p1.getX()].getValidMoves(p1, grid);
        for (int i = 0; i < validMoves.size(); i++)
        {
            if (validMoves.get(i).equals(p2)) return true;
        }
        return false;
    }
    
    //transforms a piece (probably a pawn) into another piece
    public void transform(String newPieceName, int x, int y)
    {
        if (newPieceName.equals("QUEEN")) grid[y][x] = new Queen(getCurrentTeam());
        else if (newPieceName.equals("ROOK")) grid[y][x] = new Rook(getCurrentTeam());
        else if (newPieceName.equals("KNIGHT")) grid[y][x] = new Knight(getCurrentTeam());
        else if (newPieceName.equals("BISHOP")) grid[y][x] = new Bishop(getCurrentTeam());
    }
 
    public String getBoardData(String team)
    {
        //set up the boolean grid for determining what can and cannot be seen
        boolean[][] fogGrid = new boolean[grid.length][grid[0].length];
        for (int i = 0; i < fogGrid.length; i++)
        {
            for (int j = 0; j < fogGrid[i].length; j++)
            {
                fogGrid[i][j] = false;
                if (team.equals("SPECTATOR")) fogGrid[i][j] = true;
            }
        }
        
        //go through pieces, get their attack squares, and mark them as seeable on the fogGrid
        for (int i = 0; i < fogGrid.length; i++)
        {
            for (int j = 0; j < fogGrid[i].length; j++)
            {
                if (grid[i][j] != null && grid[i][j].getTeam().equals(team))
                {
                    fogGrid[i][j] = true;
                    ArrayList<Point> attackMoves = grid[i][j].getValidMoves(new Point(j, i), grid);
                    for (int k = 0; k < attackMoves.size(); k++)
                    {
                        fogGrid[attackMoves.get(k).getY()][attackMoves.get(k).getX()] = true;
                    }
                    //special case for pawns: they see all 3 spaces in front of them
                    if (grid[i][j].getName() == '♟')
                    {
                        Pawn pawn = (Pawn) grid[i][j];
                        int dir = pawn.getDir();
                        for (int k = dir-1; k <= dir+1; k++)
                        {
                            int nDir = k % 8;
                            Point dp = pawn.getDP(k);
                            Point nP = new Point(j + dp.getX(), i + dp.getY());
                            if (pawn.isValidSquare(nP, grid) > -2) fogGrid[nP.getY()][nP.getX()] = true;
                        }
                    }
                }
            }
        }
        
        //create the memory string
        String s = "";
        for (int i = 0; i < grid.length; i++)
        {
            for (int j = 0; j < grid[i].length; j++)
            {
                if (fogGrid[i][j] == false) s += "X";
                else if (grid[i][j] == null) s += "-";
                else if (grid[i][j].getTeam().equals("WHITE")) s += grid[i][j].getWhiteName();
                else s += grid[i][j].getName();
            }
        }
        return s;
    }
    
    public String getValidMovesData()
    {
        String s = "";
        ArrayList<Point> validMoves = new ArrayList<Point>();
        int x = currentPoint.getX();
        int y = currentPoint.getY();
        if (y >= 0 && y < grid.length && x >= 0 && x < grid[y].length && grid[y][x] != null)
        {
            validMoves = grid[y][x].getValidMoves(currentPoint, grid);
        }
        s += currentPoint.getX();
        s += currentPoint.getY() + " ";
        s += validMoves.size() + " ";
        for (int i = 0; i < validMoves.size(); i++)
        {
            s += validMoves.get(i).getX();
            s += validMoves.get(i).getY();
        }
        return s;
    }
    
    public String getCapturedData()
    {
        String s = "";
        
        for (int i = 0; i < captured.get(0).size(); i++) //the pieces that white captured
        {
            s += captured.get(0).get(i).getName();
        }
        if (captured.get(0).size() == 0) s += "NULL";
        s += " ";
        for (int i = 0; i < captured.get(1).size(); i++) //the pieces that black captured
        {
            s += captured.get(1).get(i).getWhiteName();
        }
        if (captured.get(1).size() == 0) s += "NULL";
        return s;
    }
    
    /* Bugfixing, prints the grid with symbols */
    public void print(Piece[][] grid)
    {
        for (int i = 0; i < grid.length; i++)
        {
            System.out.print(grid.length - i);
            for (int j = 0; j < grid[i].length; j++)
            {
                if (grid[i][j] == null) System.out.print("-");
                else System.out.print(grid[i][j].getName());
            }
            System.out.println();
        }
        System.out.println(" 12345678");
        System.out.println();
    }
    
    public Piece[][] getGrid()
    {
        return this.grid;
    }
    
    public Point getCurrentPoint()
    {
        return currentPoint;
    }
    
    public String getCurrentTeam()
    {
        return teams.get(currentTeamIndex);
    }
    
    public ArrayList<Piece> getCapturedPieces(int i)
    {
        return captured.get(i);
    }
  
    /* Returns 1 if occupied by enemy, 0 if nothing there, -1 if occupied by teammate, -2 if out of bounds */
    public int isValidSquare(Point p, Piece[][] grid)
    {
        if (p.getY() < 0 || p.getY() >= grid.length || p.getX() < 0 || p.getX() >= grid[p.getY()].length) return -2;
        else if (grid[p.getY()][p.getX()] == null) return 0;
        else if (grid[p.getY()][p.getX()].getTeam().equals(teams.get(currentTeamIndex))) return -1;
        else return 1;
    }
}