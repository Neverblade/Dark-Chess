package darkchessserver;


import java.awt.Color;
import java.util.ArrayList;



public class Piece {
    
    private char name;
    private char whiteName;
    private String team;
    
    private int[] dX = {1, 1, 0, -1, -1, -1, 0, 1};
    private int[] dY = {0, -1, -1, -1, 0, 1, 1, 1};
    private String[] dN = {"EAST", "NORTHEAST", "NORTH", "NORTHWEST", "WEST", "SOUTHWEST", "SOUTH", "SOUTHEAST"};
    
    public Piece(String team)
    {
        this.team = team;
        this.name = '☻';
        this.whiteName = '☻';
    }
    
    public int getDirIndex(String direction)
    {
        for (int i = 0; i < dN.length; i++)
        {
            if (direction.equals(dN[i])) return i;
        }
        System.out.println("Bad direction.");
        return -1;
    }
    
    public Point getDP(String direction)
    {
        for (int i = 0; i < dN.length; i++)
        {
            if (direction.equals(dN[i])) return new Point(dX[i], dY[i]);
        }
        System.out.println("Bad direction.");
        return null;
    }
    
    public Point getDP(int direction)
    {
        return new Point(dX[direction], dY[direction]);
    }
    
    public int getDX(String direction)
    {
        for (int i = 0; i < dN.length; i++)
        {
            if (direction.equals(dN[i])) return dX[i];
        }
        System.out.println("Bad direction.");
        return -1;
    }
    
    public int getDY(String direction)
    {
        for (int i = 0; i < dN.length; i++)
        {
            if (direction.equals(dN[i])) return dY[i];
        }
        System.out.println("Bad direction.");
        return -1;
    }
    
    public int getDX(int direction)
    {
        return dX[direction];
    }
    
    public int getDY(int direction)
    {
        return dY[direction];
    }
    
    public char getName()
    {
        return this.name;
    }
    
    public char getWhiteName()
    {
        return this.whiteName;
    }
    
    public String getTeam()
    {
        return this.team;
    }
    
    public void setName(char name)
    {
        this.name = name;
    }
    
    public void setWhiteName(char name)
    {
        this.whiteName = name;
    }
    
    /* cloning */
    public Piece clone()
    {
        return new Piece(team);
    }
    
    /* Returns 1 if occupied by enemy, 0 if nothing there, -1 if occupied by teammate, -2 if out of bounds */
    public int isValidSquare(Point p, Piece[][] grid)
    {
        if (p.getY() < 0 || p.getY() >= grid.length || p.getX() < 0 || p.getX() >= grid[p.getY()].length) return -2;
        else if (grid[p.getY()][p.getX()] == null) return 0;
        else if (grid[p.getY()][p.getX()].getTeam().equals(team)) return -1;
        else return 1;
    }
    
    /* Get the moves dictated by capture logic */
    public ArrayList<Point> getMoveLogicMoves(Point p, Piece[][] grid, boolean moving)
    {
       ArrayList<Point> validMoves = new ArrayList<Point>();
       for (int i = 0; i < grid.length; i++)
       {
           for (int j = 0; j < grid[i].length; j++)
           {
               if (grid[i][j] == null) validMoves.add(new Point(j, i));
               else if (!grid[i][j].getTeam().equals(getTeam()) && grid[i][j].getName() != '♚') validMoves.add(new Point (j, i));
           }
       }
       return validMoves;
    }
 
    /* Overhead method for getting all valid moves */
    public ArrayList<Point> getValidMoves(Point p, Piece[][] grid)
    {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        validMoves = getMoveLogicMoves(p, grid, true);
        return validMoves;
    }
}
