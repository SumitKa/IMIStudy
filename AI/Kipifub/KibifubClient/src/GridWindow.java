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