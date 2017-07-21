import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.Vec2f;
import lenz.htw.kipifub.ColorChange;
import lenz.htw.kipifub.net.NetworkClient;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.awt.Color;

public class Client implements Callable<NetworkClient> {

    public static final int BOARD_SIZE = 1024;
    public static final int AVERAGE_BOARD_GRID_SIZE = 16;
    public static final int UPDATE_TIME = 30;
    public static final int MAX_PATH_TIME = 5000;
    public static final int DRAW_TIME = 300;

    private boolean DRAW = true;
    private boolean RANDOM_MOVEMENT = false;

    private String _hostName, _teamName;
    private int _playerNumber;

    // player, bot
    private Bot _bots[][];

    // y, x
    private Color[][] _board;

    private GridWindow _gridWindow;

    private AStar _aStar;

    long _lastDrawTime = System.currentTimeMillis();
    long _lastUpdateTime = System.currentTimeMillis();

    public Client(String hostName, String teamName) {
        this._hostName = hostName;
        this._teamName = teamName;

        if (_teamName == "KaHo") {

            if (DRAW)
                _gridWindow = new GridWindow();
        } else {
            DRAW = false;
            //RANDOM_MOVEMENT = true;
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


    public synchronized void update(NetworkClient client) {
        while (client.isAlive()) {

            if (_lastUpdateTime + UPDATE_TIME < System.currentTimeMillis()) {
                updateBoard(client);
            }

            if (RANDOM_MOVEMENT)
                randomMovement(client);
            else
                intelligentMovement(client);

            if (DRAW && _lastDrawTime + DRAW_TIME < System.currentTimeMillis()) {

                new Thread() {
                    public void run() {
                        drawGridBoard(true);
                    }
                }.start();

                _lastDrawTime = System.currentTimeMillis();
            }
        }
    }

    public void intelligentMovement(NetworkClient _client) {
        for (int i = 0; i < 3; i++) {
            Bot bot = _bots[_playerNumber][i];

            if (bot != null) {

                Random random = new Random();
                boolean newPath = false;

                if (bot.getDistanceToPathEnd() <= 2 || bot.getLastPathTime() + MAX_PATH_TIME < System.currentTimeMillis())// )//bot.getCurrentPathDistance() / 3)
                {
                    int gridSize = AVERAGE_BOARD_GRID_SIZE;

                    Color[][] averageBoard = getAverageBoard(_board, gridSize);
                    Vec2d goalPosition = getGoalPosition(averageBoard, bot);
                    int goalPositionX = (int) goalPosition.x * gridSize;
                    int goalPositionY = (int) goalPosition.y * gridSize;

                    List<AStar.Cell> wayPoints = _aStar.getWaipoints(averageBoard, bot, gridSize,
                            bot.getXPosition(), bot.getYPosition(), goalPositionX, goalPositionY);

                    bot.setGoalPositionX(goalPositionY);
                    bot.setGoalPositionY(goalPositionY);
                    bot.setPath(wayPoints);
//
//                    if (_playerNumber == 0)
//                        System.out.println("x new new path " + bot.getBotNumber()+" "  + System.currentTimeMillis() +" " + bot.getGoalPositionX() + " " + bot.getGoalPositionY());

                    newPath = true;

                    if (wayPoints == null) {
                        int xDirection = 1, yDirection = 1;
                        if (random.nextBoolean())
                            xDirection *= -1;
                        if (random.nextBoolean())
                            yDirection *= -1;

                        bot.setCurrentXDirection(xDirection);
                        bot.setCurrentYDirection(yDirection);

                        _client.setMoveDirection(i, xDirection, yDirection);
                    }

//                    if (wayPoints != null && wayPoints.size() > 0)
//                        System.out.println("player: " + bot.getPlayerNumber() + " bot: " + bot.getBotNumber() + " x " + bot.getXPosition() + " y " + bot.getYPosition()
//                                + " goal " + bot.getNextWaypoint().getX() + " " + bot.getNextWaypoint().getY());
//                    else
//                        System.out.println(bot.getPlayerNumber() + " +" + bot.getBotNumber() + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }

                if (bot.reachedNextWaypointAndUpdateWaypoints()) {

                    if (!newPath && bot.getLastPathTime() + 1000 < System.currentTimeMillis()) {
                        int gridSize = AVERAGE_BOARD_GRID_SIZE;
                        Color[][] averageBoard = getAverageBoard(_board, gridSize);

                        List<AStar.Cell> wayPoints = _aStar.getWaipoints(averageBoard, bot, gridSize,
                                bot.getXPosition(), bot.getYPosition(), bot.getGoalPositionX(), bot.getGoalPositionY());

//                        if (_playerNumber == 0)
//                            System.out.println("new path " + bot.getBotNumber()+" "   + System.currentTimeMillis()  +" " + bot.getGoalPositionX() + " " + bot.getGoalPositionY());
                        bot.setPath(wayPoints);
                    }

                    if (bot.getNextWaypoint() != null) {
                        int xDirection = bot.getNextWaypoint().getX() - bot.getXPosition();// ? -1 : 1;
                        int yDirection = bot.getNextWaypoint().getY() - bot.getYPosition();// ? -1 : 1;

                        bot.setCurrentXDirection(xDirection);
                        bot.setCurrentYDirection(yDirection);

                        _client.setMoveDirection(i, xDirection, yDirection);
                    }
                }
            }
        }
    }

    public Vec2d getGoalPosition(Color[][] board, Bot bot) {
        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            int randomX = random.nextInt(board[0].length);
            int randomY = random.nextInt(board.length);
            Color color = board[randomY][randomX];
            int value = 200;
            if (_playerNumber == 0) {
                if (color.getRed() < value / 4 && (color.getBlue() > value || color.getGreen() > value))
                    return new Vec2d(randomX, randomY);
            } else if (_playerNumber == 1) {
                if (color.getGreen() < value / 4 && (color.getBlue() > value || color.getRed() > value))
                    return new Vec2d(randomX, randomY);
            } else if (color.getBlue() < value / 4 && (color.getRed() > value || color.getGreen() > value))
                return new Vec2d(randomX, randomY);

        }

        return new Vec2d(0, 0);
    }

    public void randomMovement(NetworkClient client) {
        Random random = new Random();

        int x = 1, y = 1;
        for (int i = 0; i < 3; i++) {
            if (random.nextBoolean())
                x *= -1;
            if (random.nextBoolean())
                y *= -1;

            client.setMoveDirection(i, x, y);
        }
    }

    private void setupBoard(NetworkClient client) {
        _board = new Color[BOARD_SIZE][BOARD_SIZE];

        for (int y = 0; y < _board.length; y++)
            for (int x = 0; x < _board[y].length; x++)
                _board[y][x] = client.isWalkable(x, y) ? Color.WHITE : Color.BLACK;
    }

    private void setupBots(NetworkClient client) {
        _bots = new Bot[3][3];

        for (int player = 0; player < _bots.length; player++)
            for (int bot = 0; bot < _bots[player].length; bot++)
                _bots[player][bot] = new Bot(client, player, bot);
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
                    if (y + yy >= 0 && y + yy < _board.length && x + xx >= 0 && x + xx < _board[0].length)
                        _board[y + yy][x + xx] = new Color(client.getBoard(x + xx, y + yy));
        }
    }

    public Color[][] getAverageBoard(Color[][] board, int areaSize) {
        Color[][] averageBoard = new Color[board.length / areaSize][board[0].length / areaSize];

        for (int y = 0; y < averageBoard.length; y++)
            for (int x = 0; x < averageBoard[y].length; x++)
                averageBoard[y][x] = getAverageColor(board, x, y, areaSize);

        return averageBoard;
    }

    private Color getAverageColor(Color[][] board, int x, int y, int areaSize) {
        double averageR = 0;
        double averageG = 0;
        double averageB = 0;

        for (int yy = 0; yy < areaSize; yy++)
            for (int xx = 0; xx < areaSize; xx++) {

                int yPixel = y * areaSize + yy;
                int xPixel = x * areaSize + xx;

                averageR += ((double) board[yPixel][xPixel].getRed() / (areaSize * areaSize));
                averageG += ((double) board[yPixel][xPixel].getGreen() / (areaSize * areaSize));
                averageB += ((double) board[yPixel][xPixel].getBlue() / (areaSize * areaSize));
            }
        //System.out.println("R: " + averageR + " G: " + averageG + " B: " + averageB + " area: " + areaSize);

        return new Color((int) averageR, (int) averageG, (int) averageB);
    }

    public void drawGridBoard(boolean complete) {
        if (_gridWindow != null) {
            if (_board != null)
                _gridWindow.drawBoard(getAverageBoard(_board, AVERAGE_BOARD_GRID_SIZE), complete);
            if (_bots != null)
                _gridWindow.drawBots(_bots);
        }
    }
}
