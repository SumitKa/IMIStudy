import lenz.htw.kipifub.ColorChange;
import lenz.htw.kipifub.net.NetworkClient;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.awt.Color;

public class Client implements Callable<NetworkClient> {

    public static final int BOARD_SIZE = 1024;
    public static final int AVERAGE_BOARD_CELL_SIZE = 16;

    private String _hostName, _teamName;
    private int _playerNumber;

    // player, bot
    private Bot _bots[][];

    // y, x
    private Color[][] _board;
    private Color[][] _averageBoard;

    private GridWindow _gridWindow;

    private AStar _aStar;

    private boolean _draw = true;
    private boolean _randomMovement = false;

    public Client(String hostName, String teamName) {
        this._hostName = hostName;
        this._teamName = teamName;

        if (_teamName == "KaHo") {

            if (_draw)
                _gridWindow = new GridWindow();
        }

        _aStar = new AStar();
    }

    @Override
    public NetworkClient call() throws Exception {

        NetworkClient client = new NetworkClient(_hostName, _teamName);

        _playerNumber = client.getMyPlayerNumber();

        setupBots(client);
        setupBoard(client);

        drawGridBoard(true);

        send(client);
        return client;
    }

    public NetworkClient send(NetworkClient client) throws InterruptedException {

        new Thread() {
            public void run() {
                update(client);
            }
        }.start();

        return client;
    }

    long _lastDrawTime = System.currentTimeMillis();

    public synchronized void update(NetworkClient client) {
        while (client.isAlive()) {
            updateBoard(client);

            if (_lastDrawTime + 300 < System.currentTimeMillis()) {

                updateAverageBoard();

                if (_randomMovement)
                    randomMovement(client);
                else
                    intelligentMovement(client);

                drawGridBoard(false);

                _lastDrawTime = System.currentTimeMillis();
            }
        }
    }

    public void intelligentMovement(NetworkClient _client) {
        for (int i = 0; i < 3; i++) {
            Bot bot = _bots[_playerNumber][i];
            if(bot != null) {
                List<AStar.Cell> wayPoints = _aStar.getWaipoints(_averageBoard, AVERAGE_BOARD_CELL_SIZE,
                        bot.getXPosition(), bot.getYPosition(), 160, 160);

                bot.setPath(wayPoints);
            }
            //_client.setMoveDirection(i, );
        }
    }

    public void randomMovement(NetworkClient client) {
        Random rng = new Random();

        int x = 1, y = 1;
        for (int i = 0; i < 3; i++) {
            if (rng.nextBoolean())
                x *= -1;
            if (rng.nextBoolean())
                y *= -1;

            client.setMoveDirection(i, x, y);
        }
    }

    private void setupBoard(NetworkClient client) {
        _board = new Color[BOARD_SIZE][BOARD_SIZE];

        for (int y = 0; y < _board.length; y++)
            for (int x = 0; x < _board[y].length; x++) {
                _board[y][x] = client.isWalkable(x, y) ? Color.WHITE : Color.BLACK;
            }

        setupAverageBoard();
        updateAverageBoard();
    }

    private void setupBots(NetworkClient client) {
        _bots = new Bot[3][3];

        for (int player = 0; player < _bots.length; player++)
            for (int bot = 0; bot < _bots[player].length; bot++)
                _bots[player][bot] = new Bot(client, player, bot);
    }

    private void setupAverageBoard() {
        _averageBoard = new Color[_board.length / AVERAGE_BOARD_CELL_SIZE][_board[0].length / AVERAGE_BOARD_CELL_SIZE];
    }

    public void updateBoard(NetworkClient client) {
        ColorChange colorChange = client.pullNextColorChange();

        if (colorChange != null) {
            int x = colorChange.x;
            int y = colorChange.y;

            Bot currentBot = _bots[colorChange.player][colorChange.bot];
            currentBot.setXPosition(x);
            currentBot.setYPosition(y);

            for (int xx = -currentBot.getInfluence(); xx <= currentBot.getInfluence(); xx++)
                for (int yy = -currentBot.getInfluence(); yy <= currentBot.getInfluence(); yy++)
                    // TODO circle???
                    if (y + yy > 0 && y + yy < _board.length && x + xx > 0 && x + xx < _board[0].length)
                        _board[y + yy][x + xx] = new Color(client.getBoard(x + xx, y + yy));
        }
    }

    public void updateAverageBoard() {
        for (int y = 0; y < _averageBoard.length; y++)
            for (int x = 0; x < _averageBoard[y].length; x++)
                if (_averageBoard[y][x] != Color.BLACK)
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
        if (_gridWindow != null) {
            if (_averageBoard != null)
                _gridWindow.drawBoard(_averageBoard, complete);
            if (_bots != null)
                _gridWindow.drawBots(_bots);
        }
    }
}
