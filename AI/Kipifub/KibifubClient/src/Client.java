import lenz.htw.kipifub.ColorChange;
import lenz.htw.kipifub.net.NetworkClient;

import java.util.Random;
import java.util.concurrent.Callable;

import com.sun.java.swing.plaf.windows.resources.windows;

import java.awt.Color;

public class Client implements Callable<NetworkClient> {
    private  String hostName, teamName;
    
    private NetworkClient client;
    
    // y, x
    private Field[][] board;
    
    private DrawWindow window;
    
    public static class Field
    {
    	public enum Value
    	{
    		Black,
    		White
    	}
    	
    	private Color color;
    	
    	public Field(int rgb)
    	{
    		color = new Color(rgb);
    		//this.b = rgb & 255;
        	//this.g = (rgb >> 8) & 255;
        	//this.r = (rgb >> 16) & 255;
    	}
    	
    	public Field(int r, int g, int b)
    	{
    		color = new Color(r, g, b);
    	}
    	
    	public Field(Value color)
    	{
    		switch (color) {
			case Black:
				SetBlack();
				break;
			case White:
				SetWhite();
				break;
			default:
				break;
			}
    	}
    	
    	public Field(boolean walkable)
    	{
    		if(walkable)
    			SetWhite();
    		else
    			SetBlack();
    	}
    	
    	public Color get()
    	{
    		return color;
    	}
    	
    	public boolean isBlack()
    	{
    		return color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0;
    	}
    	
    	public String toString()
    	{
    		if(isBlack())
    			return "000000000";
    		
    		return color.getRed() + "" + color.getGreen() + "" + color.getBlue();
    	}
    	
    	public void print()
    	{
    		System.out.println(toString());
    	}
    	
    	public void paint(DrawWindow window)
    	{
    		//if(window != null)
    			//window.paint
    	}
    	
    	private void SetBlack()
    	{
    		color = Color.BLACK;
    	}
    	
    	private void SetWhite()
    	{
    		color = Color.WHITE;
    	}
    }

    public Client(String hostName, String teamName) {
        this.hostName = hostName;
        this.teamName = teamName;
    }

    @Override
    public NetworkClient call() throws Exception {
    	
        NetworkClient networkClient = new NetworkClient(hostName, teamName);
        
        setupBoard();
        saveWalkableBoard(networkClient);
        
        if(teamName == "KaHo")
        {
        	System.out.println("Board after setup:");
        	printBoard();
        	
        	window = new DrawWindow();
        }
        
        send(networkClient);
        return networkClient;
    }

    int printCounter = 0;
    
    public NetworkClient send(NetworkClient client) {
    	
    	this.client = client;
        Random rng = new Random();
        int test;
        for (int i = 0; i < 3; i++) {
            test = (rng.nextInt() * 2) - 1;
            if (test < 1) {
                client.setMoveDirection(i, -1, -1);
            }
            else if (test > -1) {
                client.setMoveDirection(i, 1, 1);
            }
        }
        
        System.out.println("Start update");
        update();
        
        return client;
    }
    
    public void setupBoard()
    {
    	 board = new Field[1024][1024];
    }
    
    public void update()
    {
    	while(true)
    	{
    		updateColorBoard(client.pullNextColorChange());
		        
	        if(teamName == "KaHo" && printCounter % 100000 == 0)
	        {
	        	//System.out.println("Board after update " + printCounter + ": ");
	        	
		        //printBoard();
	        	
	        	//System.out.println("Update: " + printCounter);
	        }
	        printCounter++;
    	}
    }
    
    public void saveWalkableBoard(NetworkClient client)
    {
    	for(int y = 0; y < board.length; y++)
    		for(int x = 0; x < board[y].length; x++)
    			board[y][x] = new Field(client.isWalkable(x, y));
    }
    
    public void updateColorBoard(ColorChange colorChange)
    {
    	if(colorChange == null) {}
    		//System.out.println("No color change");
    	else
    		System.out.println(colorChange.x +" " + colorChange.y + " player: " + colorChange.player + " bot: " + colorChange.bot);
    		//board[colorChange.y][ColorChange.x] = colorChange.
    		
    		
    		/*for(int y = 0; y < board.length; y++)
    		for(int x = 0; x < board[y].length; x++)
    			if(!board[y][x].isBlack())
    			{
    				board[y][x] = new Color(client.getBoard(x, y) + colorChange.);
    			}
    			*/
    }
    
    public void printBoard()
    {
    	StringBuilder stringBuilder = new StringBuilder();
    	for(int y = 0; y < board.length; y+=15)
    	{
    		for(int x = 0; x < board[y].length; x+=15)
                stringBuilder.append(board[y][x].toString() + " ");
    		stringBuilder.append("\n");
    	}
    	System.out.println(stringBuilder);
    }
}
