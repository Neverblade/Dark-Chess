package darkchessclient;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class SpriteStore
{
    private HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
    
    //capital letters are black, lower case rare white
    
    public SpriteStore()
    {
        //STILL DOING IT MANUALLY BOYS
        try
        {
            images.put("WHITE_BISHOP", ImageIO.read(getClass().getResourceAsStream("/res/0_bishop.png")));
            images.put("WHITE_KING", ImageIO.read(getClass().getResourceAsStream("/res/0_king.png")));
            images.put("WHITE_KNIGHT", ImageIO.read(getClass().getResourceAsStream("/res/0_knight.png")));
            images.put("WHITE_PAWN", ImageIO.read(getClass().getResourceAsStream("/res/0_pawn.png")));
            images.put("WHITE_QUEEN", ImageIO.read(getClass().getResourceAsStream("/res/0_queen.png")));
            images.put("WHITE_ROOK", ImageIO.read(getClass().getResourceAsStream("/res/0_rook.png")));
            images.put("BLACK_BISHOP", ImageIO.read(getClass().getResourceAsStream("/res/1_bishop.png")));
            images.put("BLACK_KING", ImageIO.read(getClass().getResourceAsStream("/res/1_king.png")));
            images.put("BLACK_KNIGHT", ImageIO.read(getClass().getResourceAsStream("/res/1_knight.png")));
            images.put("BLACK_PAWN", ImageIO.read(getClass().getResourceAsStream("/res/1_pawn.png")));
            images.put("BLACK_QUEEN", ImageIO.read(getClass().getResourceAsStream("/res/1_queen.png")));
            images.put("BLACK_ROOK", ImageIO.read(getClass().getResourceAsStream("/res/1_rook.png")));
            images.put("B", getImage("WHITE_BISHOP"));
            images.put("K", getImage("WHITE_KING"));
            images.put("I", getImage("WHITE_KNIGHT"));
            images.put("P", getImage("WHITE_PAWN"));
            images.put("Q", getImage("WHITE_QUEEN"));
            images.put("R", getImage("WHITE_ROOK"));
            images.put("b", getImage("BLACK_BISHOP"));
            images.put("k", getImage("BLACK_KING"));
            images.put("i", getImage("BLACK_KNIGHT"));
            images.put("p", getImage("BLACK_PAWN"));
            images.put("q", getImage("BLACK_QUEEN"));
            images.put("r", getImage("BLACK_ROOK"));
        } catch (Exception e) { System.out.println("Image reading failed."); }
    }
    
    public BufferedImage getImage(String key)
    {
        return images.get(key);
    }
}
