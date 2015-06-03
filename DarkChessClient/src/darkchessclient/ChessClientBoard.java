package darkchessclient;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class ChessClientBoard extends javax.swing.JFrame
{
    private ChessClient client;
    private String playerTeam;
    private SpriteStore store;
    private JLabel[][] labelGrid;
    private JLabel[] upperCaptureLabels;
    private JLabel[] lowerCaptureLabels;
    private boolean canBeResized = false;
    
    private String currentTeam;
    private String grid;
    private String moves;
    private String captured;
    
    public ChessClientBoard(ChessClient client, String playerTeam)
    {
        this.client = client;
        this.playerTeam = playerTeam;
        store = new SpriteStore();
        initComponents();
        initGrid();
        initLabels();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(560, 670));
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        setLocation(((int) screenSize.getWidth() - getWidth())/2, ((int) screenSize.getHeight() - getHeight())/2- 30);
        validate();
        setVisible(true);
        repaint();
        validate();
        
        addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent e)
            {
                if (canBeResized) update();
            }
        });
    }
    
    public void updateInfo(String currentTeam, String grid, String moves, String captured)
    {
        this.currentTeam = currentTeam;
        this.grid = grid;
        this.moves = moves;
        this.captured = captured;
        canBeResized = true;
        update();
    }
    
    public void initLabels()
    {
        //row of numbers
        rowPanel.setLayout(new GridLayout(labelGrid.length, 1));
        JLabel[] rows = new JLabel[labelGrid.length];
        String numbers = "12345678";
        if (playerTeam.equals("WHITE")) numbers = "87654321";
        for (int i = 0; i < rows.length; i++)
        {
            rows[i] = new JLabel("" + numbers.charAt(i));
            rows[i].setHorizontalAlignment(SwingConstants.RIGHT);
            rows[i].setVerticalAlignment(SwingConstants.CENTER);
            rowPanel.add(rows[i]);
        }
        
        //columns of letters
        colPanel.setLayout(new GridLayout(1, labelGrid[0].length));
        JLabel[] cols = new JLabel[labelGrid[0].length];
        String letters = "ABCDEFGH";
        if (playerTeam.equals("BLACK")) letters = "HGFEDCBA";
        for (int i = 0; i < cols.length; i++)
        {
            cols[i] = new JLabel("" + letters.charAt(i));
            cols[i].setHorizontalAlignment(SwingConstants.CENTER);
            cols[i].setVerticalAlignment(SwingConstants.TOP);
            colPanel.add(cols[i]);
        }
        
        //white and black capture areas
        lowerCaptureLabels = new JLabel[15];
        upperCaptureLabels = new JLabel[15];
        for (int i = 0; i < lowerCaptureLabels.length; i++)
        {
            lowerCaptureLabels[i] = new JLabel();
            upperCaptureLabels[i] = new JLabel();
            lowerCapturePanel.add(lowerCaptureLabels[i]);
            upperCapturePanel.add(upperCaptureLabels[i]);
            repaint();
        }
    }
    
    public void initGrid()
    {
        labelGrid = new JLabel[8][8];
        gridPanel.setLayout(new GridLayout(labelGrid.length, labelGrid[0].length));
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        for (int i = 0; i < labelGrid.length; i++)
        {
            for (int j = 0; j < labelGrid[i].length; j++)
            {
                /* Borders, backgrounds, text, etc. */
                labelGrid[i][j] = new JLabel();
                labelGrid[i][j].setBorder(border);
                labelGrid[i][j].setOpaque(true);
                if (i%2 != j%2) labelGrid[i][j].setBackground(Color.LIGHT_GRAY);
                labelGrid[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                labelGrid[i][j].setVerticalAlignment(SwingConstants.CENTER);
                
                /* Listener for detecting mouse clicks */
                final int x = j;
                final int y = i;
                labelGrid[y][x].addMouseListener(new MouseAdapter()
                {
                    public void mousePressed(MouseEvent e)
                    {
                        try { client.sendClick(x, y); } catch (Exception e1) { System.out.println("Couldn't send click data."); }
                    }
                });                
            }
        }
        
        if (playerTeam.equals("WHITE"))
        {
            for (int i = 0; i < labelGrid.length; i++)
            {
                for (int j = 0; j < labelGrid[i].length; j++)
                {
                    gridPanel.add(labelGrid[i][j]);
                }
            }            
        } else
        {
            for (int i = labelGrid.length - 1; i >= 0; i--)
            {
                for (int j = labelGrid[i].length - 1; j >= 0; j--)
                {
                    gridPanel.add(labelGrid[i][j]);
                }
            }            
        }
    }

    public void update()
    {
        /* Set up the pieces */
        int fontSize = Math.min(labelGrid[0][0].getWidth(), labelGrid[0][0].getHeight());
        Font font = new Font("Arial Unicode MS", Font.PLAIN, fontSize - 10);
        int charIndex = 0;
        for (int i = 0; i < labelGrid.length; i++)
        {
            for (int j = 0; j < labelGrid[i].length; j++)
            {
                /* Set up the font size */
                labelGrid[i][j].setFont(font);
               
                //clear the label first
                labelGrid[i][j].setText("");
                labelGrid[i][j].setIcon(null);
                
                /* Put in the character */
                if (grid.charAt(charIndex) == 'X') labelGrid[i][j].setText("X");
                else if (grid.charAt(charIndex) == '-') labelGrid[i][j].setText("");
                else labelGrid[i][j].setIcon(getIcon(labelGrid[i][j], "" + grid.charAt(charIndex)));
                //else labelGrid[i][j].setText("" + grid.charAt(charIndex));
                charIndex++;

                labelGrid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }
        
        /* Set up the valid move squares */
        Border blueBorder = new LineBorder(Color.BLUE, 3);
        Border greenBorder = new LineBorder(Color.GREEN, 3);
        Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
        Border compoundBorder = BorderFactory.createCompoundBorder(blackBorder, greenBorder);
        Border compoundBorderBlue = BorderFactory.createCompoundBorder(blackBorder, blueBorder);
        
        //here's out line of input for moves
        StringTokenizer moveTokens = new StringTokenizer(moves);
        
        //process the current point first
        String cP = moveTokens.nextToken();
        int cX = Character.getNumericValue(cP.charAt(0));
        int cY = Character.getNumericValue(cP.charAt(1));
        if (cX != -1 && cY != -1) labelGrid[cY][cX].setBorder(compoundBorderBlue);
        
        //process the rest of the valid moves
        int numMoves = Integer.parseInt(moveTokens.nextToken());
        String movePoints = "";
        if (numMoves > 0) movePoints = moveTokens.nextToken();
        int moveCharIndex = 0;
        for (int i = 0; i < numMoves; i++)
        {
            int x = Character.getNumericValue(movePoints.charAt(moveCharIndex));
            moveCharIndex++;
            int y = Character.getNumericValue(movePoints.charAt(moveCharIndex));
            moveCharIndex++;
            labelGrid[y][x].setBorder(compoundBorder);
        }
        
        //Update the captured piece labels
        /*StringTokenizer capturedTokens = new StringTokenizer(captured);
        String whiteString = capturedTokens.nextToken(); //white
        if (whiteString.equals("NULL")) whiteString = "";
        whiteString = " White: " + whiteString;
        if (playerTeam.equals("BLACK")) upperCapturePanel.setText(whiteString);
        else lowerCapturePanel.setText(whiteString);
        
        String blackString = capturedTokens.nextToken(); //black
        if (blackString.equals("NULL")) blackString = "";
        blackString = " Black: " + blackString;
        if (playerTeam.equals("BLACK")) lowerCapturePanel.setText(blackString);
        else upperCapturePanel.setText(blackString);*/
        
        //Update the captured piece labels
        StringTokenizer capturedTokens = new StringTokenizer(captured);
        String whiteString = capturedTokens.nextToken(); //white
        String blackString = capturedTokens.nextToken(); //black
        for (int i = 0; i < 15; i++)
        {
            if (playerTeam.equals("BLACK")) //black is on the bottom, white on the top
            {
                if (!whiteString.equals("NULL") && i  < whiteString.length()) lowerCaptureLabels[i].setIcon(getIcon(lowerCaptureLabels[i], "" + whiteString.charAt(i)));
                else lowerCaptureLabels[i].setIcon(null);
                if (!blackString.equals("NULL") && i < blackString.length()) upperCaptureLabels[i].setIcon(getIcon(upperCaptureLabels[i], "" + blackString.charAt(i)));
                else upperCaptureLabels[i].setIcon(null);
            } else //white is on the bottom
            {
                if (!blackString.equals("NULL") && i  < blackString.length()) lowerCaptureLabels[i].setIcon(getIcon(lowerCaptureLabels[i], "" + blackString.charAt(i)));
                else lowerCaptureLabels[i].setIcon(null);
                if (!whiteString.equals("NULL") && i < whiteString.length()) upperCaptureLabels[i].setIcon(getIcon(upperCaptureLabels[i], "" + whiteString.charAt(i)));
                else upperCaptureLabels[i].setIcon(null);
            }
        }
        
        //Update label borders to signify whose turn it is
        if ((currentTeam.equals("WHITE") && playerTeam.equals("WHITE")) || (currentTeam.equals("BLACK") && playerTeam.equals("BLACK")))
        {
            lowerCapturePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 0), 1), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
            upperCapturePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        } else
        {
            lowerCapturePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
            upperCapturePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 0), 1), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        }
        
        /* Finally, repaint the board */
        repaint();
    }
    
    public void selectWinner(String winner)
    {
        
    }
    
    public ImageIcon getIcon(JLabel label, String pieceName)
    {
        BufferedImage org = store.getImage(translate(pieceName));
        BufferedImage medImage = new BufferedImage(label.getWidth(), label.getHeight(), org.getType());
        Graphics g = medImage.createGraphics();
        g.drawImage(org, 0, 0, label.getWidth(), label.getHeight(), null);
        g.dispose();
        return new ImageIcon(medImage);
    }
    
    public String translate(String s)
    {
        String charName = "KQRBIPkqrbip";
        String[] fullNames = {"WHITE_KING", "WHITE_QUEEN", "WHITE_ROOK", "WHITE_BISHOP", "WHITE_KNIGHT", "WHITE_PAWN",
                            "BLACK_KING", "BLACK_QUEEN", "BLACK_ROOK", "BLACK_BISHOP", "BLACK_KNIGHT", "BLACK_PAWN"};
        if (charName.indexOf(s) == -1)
        {
            System.out.println("Translation from letter to icon failed: " + s);
            return s;
        }
        return fullNames[charName.indexOf(s)];
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        chessboardPanel = new javax.swing.JPanel();
        gridPanel = new javax.swing.JPanel();
        rowPanel = new javax.swing.JPanel();
        colPanel = new javax.swing.JPanel();
        lowerCapturePanel = new javax.swing.JPanel();
        upperCapturePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        chessboardPanel.setLayout(new java.awt.GridBagLayout());

        gridPanel.setLayout(new java.awt.GridLayout(1, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        chessboardPanel.add(gridPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        chessboardPanel.add(rowPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        chessboardPanel.add(colPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(chessboardPanel, gridBagConstraints);

        lowerCapturePanel.setMinimumSize(new java.awt.Dimension(18, 30));
        lowerCapturePanel.setLayout(new java.awt.GridLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lowerCapturePanel, gridBagConstraints);

        upperCapturePanel.setMinimumSize(new java.awt.Dimension(16, 30));
        upperCapturePanel.setLayout(new java.awt.GridLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(upperCapturePanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chessboardPanel;
    private javax.swing.JPanel colPanel;
    private javax.swing.JPanel gridPanel;
    private javax.swing.JPanel lowerCapturePanel;
    private javax.swing.JPanel rowPanel;
    private javax.swing.JPanel upperCapturePanel;
    // End of variables declaration//GEN-END:variables
}
