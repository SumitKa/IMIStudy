import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DrawWindow extends JFrame{

    public DrawWindow(){
         super("Draw");

         //you can set the content pane of the frame 
         //to your custom class.

         setContentPane(new DrawPane());

         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

         setSize(1024, 1024);

         setVisible(true); 
    }

     //create a component that you can actually draw on.
     class DrawPane extends JPanel{
       public void paintComponent(Graphics g)
       {
          paintGrid(g, 16);
          paintField(g, 16, 2, 2, Color.RED);
          paintField(g, 16, 2, 3, Color.RED);
          paintField(g, 16, 2, 4, Color.RED);
       }
       
       public void paintGrid(Graphics g, int gridSize)
       {
    	   for(int i = 0; i < 1024 / gridSize; i++)
    	   {
    		   g.setColor(Color.BLACK);
    		   g.drawLine(i * gridSize, 0, i*gridSize, g.getClipBounds().height);
    		   g.drawLine(0, i * gridSize, g.getClipBounds().width, i*gridSize);
    	   }
       }
       
       public void paintField(Graphics g, int gridSize, int x, int y, Color color)
       {
    	   g.setColor(color);
    	   g.fillRect(x * gridSize + 1, y * gridSize + 1, gridSize - 1, gridSize - 1);
       }
       
       public void paintBoard(Graphics g, int gridSize, Client.Field[][] board)
       {
    	   for(int y = 0; y < board.length / gridSize; y = y+gridSize)
       		for(int x = 0; x < board[y].length / gridSize; x = x+gridSize)
       		{
       			paintField(g, gridSize, x, y, board[y][x].get());
       		}
       }
    }
 }