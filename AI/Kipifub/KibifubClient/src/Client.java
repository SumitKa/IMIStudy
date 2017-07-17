import lenz.htw.kipifub.ColorChange;
import lenz.htw.kipifub.net.NetworkClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client implements Callable<NetworkClient> {

	public static final int BOARD_SIZE = 1024;
	public static final int AVERAGE_BOARD_CELL_SIZE = 16;

	private String _hostName, _teamName;
	private int _playerNumber;

	private NetworkClient _client;

	private int[] _botInfluence = new int[3];

	// y, x
	private Color[][] _board;
	private Color[][] _averageBoard;

	private GridWindow _gridWindow;

	private AStar _aStar;

	private boolean _draw = true;

	public Client(String hostName, String teamName) {
		this._hostName = hostName;
		this._teamName = teamName;

		if(_teamName == "KaHo") {

			if(_draw)
				_gridWindow = new GridWindow();
		}

		_aStar = new AStar();
	}

	@Override
	public NetworkClient call() throws Exception {

		NetworkClient client = new NetworkClient(_hostName, _teamName);

		_playerNumber = client.getMyPlayerNumber();
		_botInfluence[0] = client.getInfluenceRadiusForBot(0);
		_botInfluence[1] = client.getInfluenceRadiusForBot(1);
		_botInfluence[2] = client.getInfluenceRadiusForBot(2);

		setupBoard(client);

		drawGridBoard(true);

		send(client);
		return client;
	}

	public NetworkClient send(NetworkClient client) throws InterruptedException {

		this._client = client;

		new Thread(){
			public void run(){
				update(client);
			}
		}.start();

		//update(client);

		return client;
	}

	public synchronized void update(NetworkClient client)
	{
		while(client.isAlive())
		{
			updateBoard(client);
			updateAverageBoard();

			RandomMovement(client);

			drawGridBoard(false);
		}
	}

	public void RandomMovement(NetworkClient client) {
		Random rng = new Random();

		if (rng.nextInt() > 1000000000) {
			int x = 1, y = 1;
			for (int i = 0; i < 3; i++) {
				if (rng.nextBoolean())
					x *= -1;
				if (rng.nextBoolean())
					y *= -1;

				client.setMoveDirection(i, x, y);
			}
		}
	}

	private void setupBoard(NetworkClient client)
	{
		_board = new Color[BOARD_SIZE][BOARD_SIZE];

		for(int y = 0; y < _board.length; y++)
			for(int x = 0; x < _board[y].length; x++) {
				_board[y][x] = client.isWalkable(x, y) ? Color.WHITE : Color.BLACK;
			}

		setupAverageBoard();
		updateAverageBoard();
	}

	private void setupAverageBoard()
	{
		_averageBoard = new Color[_board.length / AVERAGE_BOARD_CELL_SIZE][_board[0].length / AVERAGE_BOARD_CELL_SIZE];
	}

	public void updateBoard(NetworkClient client) {
		ColorChange colorChange = client.pullNextColorChange();

		if (colorChange != null) {
			int x = colorChange.x;
			int y = colorChange.y;
			for (int xx = -_botInfluence[colorChange.bot]; xx <= _botInfluence[colorChange.bot]; xx++)
				for (int yy = -_botInfluence[colorChange.bot]; yy <= _botInfluence[colorChange.bot]; yy++)
					// TODO circle???
					if (y + yy > 0 && y + yy < _board.length && x + xx > 0 && x + xx < _board[0].length)
						_board[y + yy][x + xx] = new Color(client.getBoard(x + xx, y + yy));
		}
	}

	public void updateAverageBoard() {
		for (int y = 0; y < _averageBoard.length; y++)
			for (int x = 0; x < _averageBoard[y].length; x++)
				if(_averageBoard[y][x] != Color.BLACK)
					_averageBoard[y][x] = getAverageColor(x, y, AVERAGE_BOARD_CELL_SIZE);
	}

	private Color getAverageColor(int x, int y, int areaSize) {
		double averageR = 0;
		double averageG = 0;
		double averageB = 0;

		for (int yy = 0; yy < areaSize; yy++)
			for (int xx = 0; xx < areaSize; xx++) {

				int yPixel = y * areaSize + yy;
				int xPixel = x * areaSize + xx;

				averageR += ((double) _board[yPixel][xPixel].getRed() / (areaSize * areaSize));
				averageG += ((double) _board[yPixel][xPixel].getGreen() / (areaSize * areaSize));
				averageB += ((double) _board[yPixel][xPixel].getBlue() / (areaSize * areaSize));
			}
		//System.out.println("R: " + averageR + " G: " + averageG + " B: " + averageB + " area: " + areaSize);

		return new Color((int) averageR, (int) averageG, (int) averageB);
	}

	public void drawGridBoard(boolean complete) {
		if (_gridWindow != null)
			_gridWindow.drawBoard(_averageBoard, complete);
	}
}
