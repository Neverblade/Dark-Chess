package darkchessserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.StringTokenizer;

/*
Message Structure:
    All messages start with their team name on one line.
    Client asks server to make a move:
        CLICK
        X Y
    Server giving board information:
        UPDATE
        characters of all the board tiles, '-' for empty, no spaces
        N XYXYXY...
        StringOfCapturedWhites StringOfCapturedBlacks (use NULL if there's nothing captured)

*/

public class DarkChessServer
{
    private final int PORT = 12345;
    private static int serverID = 0;
    private ArrayList<Handler> handlers = new ArrayList<Handler>();
    private HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    private DarkGame game = new DarkGame(this);
    
    private boolean whiteFilled = false;
    private boolean blackFilled = false;
    private boolean gameOver = false;
    private String winnerTeam;
    
    public DarkChessServer() throws Exception
    {
        System.out.println("Opening server...");
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("Server opened.");
        try
        {
            System.out.println("Waiting for players...");
            while (true)
            {  
                Handler client = new Handler(listener.accept());
                System.out.println("A player joined as " + client.team);
                client.start();
            }
            
            
        } finally
        {
            listener.close();
            System.out.println("Closing listener.");
        }
    }
    
    public String requestPiece()
    {
        for (int i = 0; i < handlers.size(); i++)
        {
            //the handler team must match the current team
            if (handlers.get(i).getTeam().equals(game.getCurrentTeam()))
            {
                return handlers.get(i).requestPiece();
            }
        }
        return null;
    }

    public class Handler extends Thread
    {
        private String team;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private int ID;
        
        private boolean running = false;
        
        public Handler(Socket socket)
        {
            this.socket = socket;
            this.ID = serverID;
            serverID++;
            
            //assign him a team based off current available slots
            String team;
            if (!whiteFilled && !blackFilled)
            {
                System.out.println("White and black are available.");
                if (Math.random() < .5)
                {
                    whiteFilled = true;
                    team = "WHITE";
                }
                else
                {
                    blackFilled = true;
                    team = "BLACK";
                }
            } else if (!whiteFilled) 
            {
                System.out.println("White is available.");
                whiteFilled = true;
                team = "WHITE";
            }
            else if (!blackFilled)
            {
                System.out.println("Black is available.");
                blackFilled = true;
                team = "BLACK";
            }
            else
            {
                System.out.println("Both teams are filled.");
                team = "SPECTATOR";
            }
            this.team = team;
        }

        //called when start() is called
        public void run()
        {
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                handlers.add(this);
                
                //send the team name first
                out.println(team);
                
                //send them board information
                update(out, team);
                
                //wait for input from the client
                running = true;
                while (running)
                {
                    String message = in.readLine();
                    digest(message);
                }
                
            } catch (Exception e)
            {
                //System.out.println("Something occurred while setting up the client.");
                //System.out.println(e);
            } finally 
            {
                System.out.println(team + " is leaving the server.");
                System.out.println("Removing team...");
                if (team.equals("WHITE"))
                {
                    System.out.println("Removing white.");
                    whiteFilled = false;
                }
                else if (team.equals("BLACK"))
                {
                    System.out.println("Removing black.");
                    blackFilled = false;
                }
                if (out != null) 
                {
                    System.out.println("Removing stream....");
                    handlers.remove(this);
                }
                System.out.println(team + " has left the server.");
                try { socket.close(); } catch (Exception e) { System.out.println("Failed to close client socket."); }
            }
        }
        
        //called when a message is recieved from the client
        public void digest(String message) throws Exception
        {
            if (team.equals(game.getCurrentTeam()) && !gameOver) //action is only allowed by the current player
            {
                int z = -2;
                String action = in.readLine();
                if (action == null) System.out.println("Met null when trying to read action.");
                if (action.equals("CLICK"))
                {
                    StringTokenizer coordinates = new StringTokenizer(in.readLine());
                    int x = Integer.parseInt(coordinates.nextToken());
                    int y = Integer.parseInt(coordinates.nextToken());
                    z = game.click(new Point(x, y));
                }
                
                //update the board for active players and spectators
                for (int i = 0; i < handlers.size(); i++)
                {
                    if (handlers.get(i).getTeam().equals(game.getCurrentTeam()) || handlers.get(i).getTeam().equals("SPECTATOR") || z == 1)
                    {
                        update(handlers.get(i).getWriter(), handlers.get(i).getTeam());
                    }
                }
                
                //check if the game is over
                if (gameOver)
                {
                    for (Handler h : handlers)
                    {
                        h.getWriter().println("WINNER");
                        h.getWriter().println(winnerTeam);
                    }
                }
            }
        }
        
        public void update(PrintWriter writer, String team)
        {
            System.out.println("Updating data to writer...");
            writer.println("UPDATE");
            writer.println(game.getCurrentTeam());
            writer.println(translateUnicodeToLetter(game.getBoardData(team)));
            writer.println(game.getValidMovesData());
            writer.println(translateUnicodeToLetter(game.getCapturedData()));
        }
        
        public String requestPiece()
        {
            out.println("PIECEREQUEST");
            running = false;
            while (true)
            {
                try
                {
                    String message = in.readLine();
                    if (message.equals("PIECECHOSEN"))
                    {
                        running = true;
                        return in.readLine();
                    }
                } catch (Exception e) { System.out.println("Piece requesting failed."); }
            }
        }
        
        public String translateUnicodeToLetter(String s)
        {
            s = s.replaceAll("♚", "k");
            s = s.replaceAll("♛", "q");
            s = s.replaceAll("♞", "i");
            s = s.replaceAll("♝", "b");
            s = s.replaceAll("♜", "r");
            s = s.replaceAll("♟", "p");
            s = s.replaceAll("♔", "K");
            s = s.replaceAll("♕", "Q");
            s = s.replaceAll("♘", "I");
            s = s.replaceAll("♗", "B");
            s = s.replaceAll("♖", "R");
            s = s.replaceAll("♙", "P");
            return s;
        }
        
        public String translateLetterToUnicode(String s)
        {
            s = s.replaceAll("k", "♚");
            s = s.replaceAll("q", "♛");
            s = s.replaceAll("i", "♞");
            s = s.replaceAll("b", "♝");
            s = s.replaceAll("r", "♜");
            s = s.replaceAll("p", "♟");
            s = s.replaceAll("K", "♔");
            s = s.replaceAll("Q", "♕");
            s = s.replaceAll("I", "♘");
            s = s.replaceAll("B", "♗");
            s = s.replaceAll("R", "♖");
            s = s.replaceAll("P", "♙");
            return s;
        }
        
        public String getTeam()
        {
            return team;
        }
        
        public int getID()
        {
            return ID;
        }
        
        public PrintWriter getWriter()
        {
            return out;
        }
    }
    
    public void selectWinner(String winnerTeam)
    {
        gameOver = true;
        this.winnerTeam = winnerTeam;
    }
    
    public static void main(String[] args) throws Exception
    {
        DarkChessServer server = new DarkChessServer();
    }
}
