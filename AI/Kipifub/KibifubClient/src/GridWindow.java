import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GridWindow extends JFrame {

    private DrawPane _drawPane;

    public GridWindow() {
        super("Grid");

        _drawPane = new DrawPane();
        setContentPane(_drawPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1024, 1024);

        setVisible(true);
    }

    public void drawBoard(Color[][] board, boolean complete) {
        _drawPane.drawBoard(_drawPane.getGraphics(), board, complete);
    }

    public void drawBots(Bot[][] bots) {
        _drawPane.drawBots(_drawPane.getGraphics(), bots);
    }

    //create a component that you can actually draw on.
    class DrawPane extends JPanel {
        public void drawBot(Graphics g, Bot bot) {

            if (bot == null)
                return;

            setBotColor(g, bot);

            g.fillOval(bot.getXPosition(), bot.getYPosition(), 10 + bot.getBotNumber() * 5, 10 + bot.getBotNumber() * 5);
        }

        public void drawBotPath(Graphics g, Bot bot) {
            if (bot == null || bot.getPath() == null)
                return;

            setBotColor(g, bot);

            System.out.println("fff");
            for (int i = 0; i < bot.getPath().size() - 1; i++) {
                AStar.Cell startPoint = bot.getPath().get(i);
                AStar.Cell endPoint = bot.getPath().get(i + 1);
                g.drawLine(startPoint.getX() + 9, startPoint.getY() + 8, endPoint.getX() + 9, endPoint.getY() + 8);
                g.drawLine(startPoint.getX() + 8, startPoint.getY() + 8, endPoint.getX() + 8, endPoint.getY() + 8);
                g.drawLine(startPoint.getX() + 8, startPoint.getY() + 9, endPoint.getX() + 8, endPoint.getY() + 9);

            }
        }

        public void setBotColor(Graphics g, Bot bot) {
            if (bot == null)
                return;

            // player 0 = red, 1 = green, 2 = blue
            if (bot.getPlayerNumber() == 0)
                g.setColor(new Color(100, 0, 0));
            else if (bot.getPlayerNumber() == 1)
                g.setColor(new Color(0, 100, 0));
            else if (bot.getPlayerNumber() == 2)
                g.setColor(new Color(0, 0, 100));
        }

        public void drawBots(Graphics g, Bot[][] bots) {
            for (int player = 0; player < bots.length; player++)
                for (int bot = 0; bot < bots[player].length; bot++) {
                    drawBot(g, bots[player][bot]);
                    drawBotPath(g, bots[player][bot]);
                }
        }

        public void drawField(Graphics g, int gridSize, int x, int y, Color color, boolean complete) {

            if (color == null)
                return;

            if (complete || (!isBlack(color) && !isWhite(color))) {
                g.setColor(color);
                g.fillRect(x * gridSize + 1, y * gridSize + 1, gridSize - 1, gridSize - 1);
            }
        }

        public void drawBoard(Graphics g, Color[][] board, boolean complete) {
            for (int y = 0; y < board.length; y++)
                for (int x = 0; x < board[y].length; x++)
                    drawField(g, 1024 / board.length, x, y, board[y][x], complete);

            drawGrid(g, 1024 / board.length);
        }

        private void drawGrid(Graphics g, int gridSize) {
            for (int i = 0; i < 1024 / gridSize; i++) {
                g.setColor(Color.GRAY);
                g.drawLine(i * gridSize, 0, i * gridSize, 1024);
                g.drawLine(0, i * gridSize, 1024, i * gridSize);
            }
        }

        private boolean isBlack(Color color) {
            return color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0;
        }

        private boolean isWhite(Color color) {
            return color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255;
        }
    }
}