package darkchessclient;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JOptionPane;



public class ChessClient
{
    private final int PORT = 12345;
    private BufferedReader in;
    private PrintWriter out;
    private ChessClientBoard board;
    private String team;
    
    public ChessClient()
    {

    }
    
    public void run() throws Exception
    {
        System.out.println("Attempting to join a server.");
        Socket socket = new Socket(getServerAddress(), PORT);
        System.out.println("Joined the server.");
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        //waiting for a team name
        System.out.println("Waiting for team name...");
        team = in.readLine();
        if (!team.equals("SPECTATOR") && !team.equals("WHITE") && !team.equals("BLACK")) System.out.println("Team name doesn't work.");
        if (team.equals("SPECTATOR")) board = new ChessClientBoard(this, "WHITE");
        else board = new ChessClientBoard(this, team);
        System.out.println("Received team name " + team);
        
        //accepting input from the server
        while (true)
        {
            String line = in.readLine();
            if (line.equals("UPDATE"))
            {
                //System.out.println("Updating info.");
                update();
            } else if (line.equals("PIECEREQUEST"))
            {
                String x = getPromotionPiece();
                out.println("PIECECHOSEN");
                out.println(x);
            } else if (line.equals("WINNER"))
            {
                board.selectWinner(in.readLine());
            }
        }        
    }
    
    //takes in input from the server to update info in the board
    public void update() throws Exception
    {
        board.updateInfo(in.readLine(), in.readLine(), in.readLine(), in.readLine());
    }
    
    //takes in a click and sends it as data to the server
    public void sendClick(int x, int y)
    {
        out.println(team);
        out.println("CLICK");
        out.println(x + " " + y);
    }

    //ask the user for the server IP
    public String getServerAddress()
    {
        return JOptionPane.showInputDialog(board, "Enter IP address:", "Welcome to Chess", JOptionPane.PLAIN_MESSAGE);
    }
    
    //gets a choice of a piece from the user
    public String getPromotionPiece()
    {
        Object[] choices = {"QUEEN", "KNIGHT", "ROOK", "BISHOP"};
        return (String) JOptionPane.showInputDialog(board, "Choose a piece to promote to:", "Piece Promotion", JOptionPane.PLAIN_MESSAGE, null, choices, "QUEEN");
    }
    
    public static void main(String[] args) throws Exception
    {
        ChessClient client = new ChessClient();
        client.run();
    }
}
