import lenz.htw.kipifub.net.NetworkClient;

import java.util.List;

/**
 * Created by chris on 17-Jul-17.
 */
public class Bot {

    private NetworkClient _client;
    private int _playerNumber;
    private int _botNumber;

    private int _xPosition, _yPosition;
    private int _influence;

    private List<AStar.Cell> _path;

    public int getPlayerNumber()
    {
        return _playerNumber;
    }

    public int getBotNumber()
    {
        return _botNumber;
    }

    public int getInfluence() {
        return _influence;
    }

    public void setXPosition(int xPosition) {
        _xPosition = xPosition;
    }
    public int getXPosition() {
        return _xPosition;
    }

    public void setYPosition(int yPosition) {
        _yPosition = yPosition;
    }
    public int getYPosition() {
        return _yPosition;
    }

    public void setPath(List<AStar.Cell> path) {_path = path; }
    public List<AStar.Cell> getPath() { return _path; }

    public Bot(NetworkClient client, int playerNumber, int botNumber) {
        _client = client;
        _playerNumber = playerNumber;
        _botNumber = botNumber;
        _influence = client.getInfluenceRadiusForBot(botNumber);
    }
}
