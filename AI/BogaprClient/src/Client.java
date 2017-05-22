import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;


import lenz.htw.bogapr.Move;
import lenz.htw.bogapr.net.NetworkClient;

// java -Djava.library.path=lib/native -jar bogapr.jar

public class Client implements Callable<NetworkClient>
{
    static private String hostName, teamName;
    static private BufferedImage logo;

    // [y][x] & einer = top stone
    int[][] gamePitch = new int[7][];
    int points1 = 0, points2 = 0, points3 = 0;

    public Client(String hostName, String teamName, BufferedImage logo) {
        this.hostName = hostName;
        this.teamName = teamName;
        this.logo = logo;
    }

    @Override
    public NetworkClient call() throws Exception {

        SetUpGamePitch();

        NetworkClient networkClient = new NetworkClient(hostName, teamName, logo);

        int latency = networkClient.getExpectedNetworkLatencyInMilliseconds();
        int time = networkClient.getTimeLimitInSeconds();
        int playerNumber = networkClient.getMyPlayerNumber();
        boolean calcutating = false;

        for (;;) {
            Move receiveMove;
            while ((receiveMove = networkClient.receiveMove()) != null) {

                int player = GetTop(gamePitch, receiveMove.fromX, receiveMove.fromY);
                int points = Move(gamePitch, receiveMove);

                if(player == 1)
                    points1 += points;
                else if(player == 2)
                    points2 += points;
                else if(player == 3)
                    points3 += points;

                if (playerNumber == 0) {
                    PrintPitch(gamePitch);
                    System.out.println("player " + player + " got " + points + " points with this move");
                    System.out.println("points: player 1: " + points1 + " player 2: " + points2 + " player 3: " + points3);
                    System.out.println();
                    System.out.println("Next move");
                }
            }

            networkClient.sendMove(GetValidMove(playerNumber + 1, 1));
        }
    }

    private class Field
    {
        public int X, Y, Count;

        public Field(int x, int y, int count)
        {
            X = x; Y = y; Count = count;
        }
    }

    private class Moves
    {
        public List<Move> Moves = new ArrayList<>();

        public int Points = 0;

        public Moves(Move move, int points)
        {
            AddMove(move, points);
        }

        public void AddMove(Move move, int points)
        {
            Moves.add(move);
            Points += points;
        }
    }

    public void SetUpGamePitch()
    {
        for (int y = 0; y <= 6; y++)
        {
            gamePitch[y] = new int[y * 2 + 1];

            for (int x = 0; x < gamePitch[y].length; x++) {
                gamePitch[y][x] = 0;
            }
        }

        // no [0][0] and no [6][0] and no [6][12]
        gamePitch[0][0] = gamePitch[6][0] = gamePitch[6][12] = 999;

        SetUpPlayer();
    }

    public void SetUpPlayer()
    {
        gamePitch[1][0] = gamePitch[1][1] = gamePitch[1][2] = 111;
        gamePitch[5][0] = gamePitch[6][1] = gamePitch[6][2] = 222;
        gamePitch[5][10] = gamePitch[6][10] = gamePitch[6][11] = 333;
    }

    public void PrintPitch(int[][] pitch)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for(int y = pitch.length - 1; y >= 0; y--)
        {
            for(int x = 0; x < pitch[y].length; x++)
                stringBuilder.append("[" + pitch[y][x] + "]");
            stringBuilder.append("\n");
        }

        System.out.println(stringBuilder);
    }

    public int Move(int[][] pitch, Move move)
    {
        int player = RemoveStone(pitch, move.fromX, move.fromY);
        int points = AddStone(pitch, move.toX, move.toY, player);

        return points;
    }

    public int AddStone(int[][] pitch, int x, int y, int player)
    {
        int points = 0;

        int currentTopPlayer = GetTop(pitch, x, y);
        if(currentTopPlayer != 0 && currentTopPlayer != player)
        {
            points++;
        }

        if(IsEndMove(x, y, player))
        {
            points += 5;
        }

        int stones = pitch[y][x] * 10 + player;
        pitch[y][x] = stones;

        return points;
    }

    public boolean IsEndMove(int x, int y, int player)
    {
        boolean isEnd = false;
        if((player == 1 && y == 6 && x % 2 == 0)
         ||(player == 2 && y * 2 == x)
         ||(player == 3 && x == 0))
            isEnd = true;
        return isEnd;
    }

    public boolean ShouldEndGame(int player)
    {
        boolean shouldEnd = false;
        if((player == 1 && points1 > points2 && points3 > points3)
         ||(player == 2 && points2 > points1 && points3 > points3)
         ||(player == 3 && points3 > points1 && points2 > points3))
            shouldEnd = true;
        return shouldEnd;
    }

    public int RemoveStone(int[][] pitch, int x, int y)
    {
        int player = GetTop(pitch, x, y);
        pitch[y][x] = pitch[y][x] / 10;
        return player;
    }

    // ggf. beim setzten direkt in einer liste abspeichern
    public List<Field> GetTops(int[][] pitch, int player)
    {
        List<Field> tops = new ArrayList<>();

        for (int y = 0; y < pitch.length; y++)
            for(int x = 0; x < pitch[y].length; x++)
                if(GetTop(pitch, x, y) == player)
                    tops.add(new Field(x, y, GetCount(pitch, x, y)));

        System.out.println(tops.size() + " tops for player " + player);

        return tops;
    }

    public int GetTop(int[][] pitch, int x, int y)
    {
        int player = pitch[y][x] % 10;
        return player;
    }

    // teuer -> direkt beim setzten merken
    public int GetCount(int[][] pitch, int x, int y)
    {
        int pitchInt = pitch[y][x];
        int count = 0;

        if(pitchInt % 10 != 0)
        {
            count++;
            if(pitchInt / 10 % 10 != 0)
            {
                count++;
                if(pitchInt / 100 % 10 != 0)
                    count++;
            }
        }

        return count;
    }

    public boolean HasNotMax(int[][] pitch, int x, int y)
    {
        return pitch[y][x] / 100 == 0;
    }

    public Move GetValidMove(int player, int depth)
    {
//        int maxPoints = 0;
//        int[][]calculatedPitch = new int[gamePitch.length][];
//
//        for(int y = 0; y < gamePitch.length; y++) {
//            calculatedPitch[y] = new int[gamePitch[y].length];
//            for (int x = 0; x < gamePitch[y].length; x++)
//                calculatedPitch[y][x] = gamePitch[y][x];
//        }

        List<Field> tops = GetTops(gamePitch, player);
        List<Move> validMoves = GetValidMoves(gamePitch, tops);

        System.out.println(validMoves.size() + " valid moves for player " + player);

//        List<Moves> validMovePoints = new ArrayList<>();
//        for(int i = 0; i < validMoves.size(); i++)
//        {
//            Move move = validMoves.get(i);
//            int points = Move(calculatedPitch, move);
//            if(points >= maxPoints) {
//                if(points > maxPoints)
//                {
//                    maxPoints = points;
//                    validMovePoints.clear();
//                }
//                Moves moves = new Moves(move, points);
//                validMovePoints.add(moves);
//            }
//        }

        Random random = new Random();
        Move move = validMoves.get(random.nextInt(validMoves.size()));//.Moves.get(0);

        System.out.println("choose move: " + move);

        return move;
    }

    public List<Move> GetValidMoves(int[][] pitch, List<Field> tops)
    {
        List<Move> moves = new ArrayList<>();

        for(int i = 0; i < tops.size(); i++)
        {
            Field field = tops.get(i);
            moves.addAll(GetValidMoves(pitch, field));
        }

        return  moves;
    }

    public List<Move> GetValidMoves(int[][] pitch, Field field)
    {
        int x = field.X, y = field.Y;

        List<Move> moves = new ArrayList<>();

        if(field.Count == 1) {
            if (x > 0 && HasNotMax(pitch, x - 1, y))
                moves.add(new Move(x, y, x - 1, y));
            if (x < pitch[y].length - 1 && HasNotMax(pitch, x + 1, y))
                moves.add(new Move(x, y, x + 1, y));
            if (x % 2 != 0 && y > 1 && HasNotMax(pitch, x - 1, y - 1))
                moves.add(new Move(x, y, x - 1, y - 1));
            if (x % 2 == 0 && y < pitch.length - 1 && HasNotMax(pitch, x + 1, y + 1))
                moves.add(new Move(x, y, x + 1, y + 1));
        }
        else if(field.Count == 2) {
            if (x > 1) {
                if (HasNotMax(pitch, x - 2, y))
                    moves.add(new Move(x, y, x - 2, y));
                if(HasNotMax(pitch, x - 2, y - 1))
                    moves.add(new Move(x, y, x - 2, y - 1));
            }
            if (x < pitch[y].length - 2) {
                if (HasNotMax(pitch, x + 2, y))
                    moves.add(new Move(x, y, x + 2, y));
                if (HasNotMax(pitch, x, y - 1))
                    moves.add(new Move(x, y, x, y - 1));
            }
            if (y < pitch.length - 1) {
                if (HasNotMax(pitch, x, y + 1))
                    moves.add(new Move(x, y, x, y + 1));
                if (HasNotMax(pitch, x + 2, y + 1))
                    moves.add(new Move(x, y, x + 2, y + 1));
            }
        }
        else if(field.Count == 3) {
            if (x > 2 && HasNotMax(pitch, x - 3, y))
                moves.add(new Move(x, y, x - 3, y));
            if (x < pitch[y].length - 3 && HasNotMax(pitch, x + 3, y))
                moves.add(new Move(x, y, x + 3, y));

            if (y > 1) {
                if (x > 2 && HasNotMax(pitch, x - 3, y - 1))
                    moves.add(new Move(x, y, x - 3, y - 1));
                if (x < pitch[y].length - 3 && HasNotMax(pitch, x + 1, y - 1))
                    moves.add(new Move(x, y, x + 1, y - 1));
            }
            if (y < pitch.length - 1) {
                if (x > 0 && HasNotMax(pitch, x - 1, y + 1))
                    moves.add(new Move(x, y, x - 1, y + 1));
                if (x < pitch[y].length - 1 && HasNotMax(pitch, x + 3, y + 1))
                    moves.add(new Move(x, y, x + 3, y + 1));
            }

            if (x % 2 == 0) {
                if (x > 0 && x < pitch[y].length -1 && HasNotMax(pitch, x - 1, y - 1))
                        moves.add(new Move(x, y, x - 1, y - 1));
                if (y < pitch.length - 2) {
                    if (HasNotMax(pitch, x + 1, y + 2))
                        moves.add(new Move(x, y, x + 1, y + 2));
                    if (HasNotMax(pitch, x + 3, y + 2))
                        moves.add(new Move(x, y, x + 3, y + 2));
                }
            }
            else {
                if (y > 2) {
                    if (x > 2 && HasNotMax(pitch, x - 3, y - 2))
                        moves.add(new Move(x, y, x - 3, y - 2));
                    if (x < pitch[y].length - 3 && HasNotMax(pitch, x - 1, y - 2))
                        moves.add(new Move(x, y, x - 1, y - 2));
                }
                if (y < pitch.length - 1) {
                    if (HasNotMax(pitch, x + 1, y + 1))
                        moves.add(new Move(x, y, x + 1, y + 1));
                }
            }
        }

        return moves;
    }
}
