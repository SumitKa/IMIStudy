import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GridWindow extends JFrame{

    private DrawPane _drawPane;

    public GridWindow(){
        super("Grid");

        _drawPane = new DrawPane();
        setContentPane(_drawPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1024, 1024);

        setVisible(true);
    }

    public void drawBoard(Color[][] board, boolean complete)
    {
        _drawPane.drawBoard(_drawPane.getGraphics(), board, complete);
    }

    public void drawBots(Bot[][] bots)
    {
        _drawPane.drawBots(_drawPane.getGraphics(), bots);
    }

    //create a component that you can actually draw on.
    class DrawPane extends JPanel
    {
//        int _gridSize = 4;

//        public void paintComponent(Graphics g)
//        {
//            drawGrid(g);
//        }

//        private void drawGrid(Graphics g)
//        {
//            for(int i = 0; i < 1024 / _gridSize; i++)
//            {
//                g.setColor(Color.GRAY);
//                g.drawLine(i * _gridSize, 0, i*_gridSize, g.getClipBounds().height);
//                g.drawLine(0, i * _gridSize, g.getClipBounds().width, i*_gridSize);
//            }
//        }

        public void drawBot(Graphics g, Bot bot) {
            // player 0 = red, 1 = green, 2 = blue
            if (bot.getPlayerNumber() == 0)
                g.setColor(new Color(100, 0, 0));
            else if (bot.getPlayerNumber() == 1)
                g.setColor(new Color(0, 100, 0));
            else if (bot.getPlayerNumber() == 2)
                g.setColor(new Color(0, 0, 100));

            g.drawOval(bot.getXPosition(), bot.getYPosition(), bot.getBotNumber() * 10, bot.getBotNumber() * 10);
        }

        public void drawBots(Graphics g, Bot[][] bots )
        {
            for(int player = 0; player < bots.length; player++)
                for(int bot = 0; bot < bots[player].length; bot++)
                    drawBot(g, bots[player][bot]);
        }

        public void drawField(Graphics g, int gridSize, int x, int y, Color color, boolean complete) {

//            System.out.println(complete);
            if (complete || (!isBlack(color) && !isWhite(color))) {
                g.setColor(color);
                g.fillRect(x * gridSize + 1, y * gridSize + 1, gridSize - 1, gridSize - 1);
            }
        }

        public void drawBoard(Graphics g, Color[][] board, boolean complete)
        {
            for(int y = 0; y < board.length; y++)
                for(int x = 0; x < board[y].length; x++)
                    drawField(g,1024 / board.length,  x, y, board[y][x], complete);
        }

        private boolean isBlack(Color color) { return color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0; }
        private boolean isWhite(Color color) { return color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255; }
    }
}