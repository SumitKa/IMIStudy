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
    private int _currentXDirection, _currentYDirection;
    private int _goalPositionX, _goalPositionY;

    private float _pathDistance;
    public long _lastPathTime;

    private List<AStar.Cell> _path;

    public int getPlayerNumber() {
        return _playerNumber;
    }

    public int getBotNumber() {
        return _botNumber;
    }

    public int getInfluence() {
        return _influence;
    }

    public void setXPosition(int xPosition) {
        _xPosition = xPosition;
    }

    public void setYPosition(int yPosition) {
        _yPosition = yPosition;
    }

    public int getXPosition() {
        return _xPosition;
    }

    public int getYPosition() {
        return _yPosition;
    }

    public void setCurrentXDirection(int direction) {
        _currentXDirection = direction;
    }

    public void setCurrentYDirection(int direction) {
        _currentYDirection = direction;
    }

    public void setGoalPositionX(int position) { _goalPositionX = position; }
    public void setGoalPositionY(int position) { _goalPositionY = position; }
    public int getGoalPositionX() { return _goalPositionX; }
    public int getGoalPositionY() { return _goalPositionY; }

    public int getXDirection() {
        return _currentXDirection;
    }

    public int getYDirection() {
        return _currentYDirection;
    }

    public long getLastPathTime() {
        return _lastPathTime;
    }

    public void setPath(List<AStar.Cell> path) {
        _path = path;
        _pathDistance = getDistanceToPathEnd();
        _lastPathTime = System.currentTimeMillis();
    }

    public boolean hasPath() {
        return _path != null;
    }

    public float getCurrentPathDistance() {
        return _pathDistance;
    }

    public List<AStar.Cell> getPath() {
        return _path;
    }

    public void removeLastWaypoint() {
        if (_path != null) {
            if (_path.size() == 0)
                _path = null;
            else
                _path.remove(_path.size() - 1);
        }
    }

    public AStar.Cell getNextWaypoint() {
        if (_path != null && _path.size() > 0)
            return _path.get(_path.size() - 1);

        return null;
    }

    public boolean reachedNextWaypointAndUpdateWaypoints() {
        AStar.Cell nextWaypoint = getNextWaypoint();

        if (nextWaypoint == null) {
            _path = null;
            return false;
        }
        float value = .5f;
        if (((_currentXDirection >= 0 && nextWaypoint.getX() <= _xPosition + value)
                || (_currentXDirection <= 0 && nextWaypoint.getX() >= _xPosition - value))
                && ((_currentYDirection >= 0 && nextWaypoint.getY() <= _yPosition + value)
                || (_currentYDirection <= 0 && nextWaypoint.getY() >= _yPosition - value))) {

            removeLastWaypoint();
            return true;
        }

        return false;
    }

    public float getDistanceToPathEnd() {
        if (_path == null || _path.size() == 0)
            return 0;

        float xDistance = Math.abs(_xPosition - _path.get(0).getX());
        float yDistance = Math.abs(_yPosition - _path.get(0).getY());
        float distance = (float) Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

        return distance;
    }

    public Bot(NetworkClient client, int playerNumber, int botNumber) {
        _client = client;
        _playerNumber = playerNumber;
        _botNumber = botNumber;
        _influence = client.getInfluenceRadiusForBot(botNumber);
    }
}
