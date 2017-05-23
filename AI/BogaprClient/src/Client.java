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

    int playerNumber;

    // [y][x] & einer = top stone
    int[][] gamePitch = new int[7][];
    int points1 = 0, points2 = 0, points3 = 0;
    boolean player1 = true, player2 = true, player3 = true;

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
        playerNumber = networkClient.getMyPlayerNumber() + 1;
        int player = 0;

        for (;;) {
            Move receiveMove;
            while ((receiveMove = networkClient.receiveMove()) != null) {

                int currentplayer = GetTop(gamePitch, receiveMove.fromX, receiveMove.fromY);

                int points = Move(gamePitch, receiveMove);

                if(currentplayer == 1) {
                    points1 += points;
                    if(player != 0 && player != 3)
                        player3 = false;
                }
                else if(currentplayer == 2) {
                    points2 += points;
                    if(player != 0 && player != 1)
                        player1 = false;
                }
                else if(currentplayer == 3) {
                    points3 += points;
                    if(player != 0 && player != 2)
                        player2 = false;
                }

                player = currentplayer;

                if (playerNumber == 1) {
                    PrintPitch(gamePitch);
                    System.out.println("player " + player + " got " + points + " points with this move");
                    System.out.println("points: player 1: " + points1 + " player 2: " + points2 + " player 3: " + points3);
                    System.out.println();
                    System.out.println("Next move player " + GetNextPlayer(player));
                }
            }

            networkClient.sendMove(GetBestMove(playerNumber, 4));
            //networkClient.sendMove(GetRandomMove(playerNumber));
        }
    }

    private class Field
    {
        public int X, Y, Count, Top;

        public Field(int x, int y, int count, int top)
        {
            X = x; Y = y; Count = count; Top = top;
        }
    }

    // TODO ...

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
        int points = GetPoints(pitch, x, y, player);
        int stones = pitch[y][x] * 10 + player;
        pitch[y][x] = stones;

        return points;
    }

    public int GetPoints(int[][] pitch, int x, int y, int player)
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
                    tops.add(new Field(x, y, GetCount(pitch, x, y), player));

        //System.out.println(tops.size() + " tops for player " + player);

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

    public int GetNextPlayer(int currentPlayer)
    {
        do {
            currentPlayer = currentPlayer < 3 ? currentPlayer + 1 : 1;
        }while(!DoesPlayerExist(currentPlayer));

        return currentPlayer;
    }

    public boolean DoesPlayerExist(int player)
    {
        if((player == 1 && player1)
        || (player == 2 && player2)
        || (player == 3 && player3))
            return  true;
        return false;
    }

    public Move GetRandomMove(int player)
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
//            int points = GetPoints(gamePitch, move.toX, move.toY, player);
//            if(points >= maxPoints) {
//                if(points > maxPoints)
//                {
//                    System.out.println("p " + points + "mp " + maxPoints);
//                    maxPoints = points;
//                    validMovePoints.clear();
//                }
//
//                System.out.println("xx " + points);
//                Moves moves = new Moves(move, points);
//                validMovePoints.add(moves);
//            }
//        }
//
//        System.out.println(validMovePoints.size() + " valid moves with max point for player " + player);

        Random random = new Random();
        Move move = validMoves.get(random.nextInt(validMoves.size()));//.Moves.get(0);

        System.out.println("choose move: " + move);

        return move;
    }

    public Move GetBestMove(int player, int depth)
    {
        int[][]calculatedPitch = new int[gamePitch.length][];

        for(int y = 0; y < gamePitch.length; y++) {
            calculatedPitch[y] = new int[gamePitch[y].length];
            for (int x = 0; x < gamePitch[y].length; x++)
                calculatedPitch[y][x] = gamePitch[y][x];
        }

        Move bestMove = GetMoveMinMax(calculatedPitch, player, depth, 0, 0).Move;

        return bestMove;
    }

    // TODO woanders hin
    private class MovePoint
    {
        public Move Move;

        public int Points;

        public MovePoint(Move move, int points)
        {
            Move = move;
            Points = points;
        }
    }

    //MovePoint currentBestMove = new MovePoint();

    public MovePoint GetMoveMinMax(int[][] pitch, int player, int depth, int alpha, int beta)
    {
        List<MovePoint> bestMoves = new ArrayList<>();
        MovePoint bestMove = new MovePoint(null, player == playerNumber ? alpha : beta);

        List<Field> tops = GetTops(pitch, player);
        for(Move move : GetValidMoves(pitch, tops))
        {
            int points = Move(pitch, move);

            //System.out.println("Move " + move + " points: " + points);

            MovePoint nextMove = null;
            if(depth - 1 > 0) {
                nextMove = GetMoveMinMax(pitch, GetNextPlayer(player), depth - 1, alpha, beta);
            }

            if (player == playerNumber) {

                if(nextMove != null)
                    points +=nextMove.Points;
                //System.out.println("player me, points: " + points + " alpha: " + alpha + " best points: " + bestMove.Points);

                if(points >= bestMove.Points)
                {
                    if(points > bestMove.Points)
                    {
                        bestMoves.clear();
                    }


                    alpha = points;
                    bestMove = new MovePoint(move, alpha);
                    bestMoves.add(bestMove);
                    if(alpha != 0) System.out.println("alpha player: " + player + "move: " + bestMove.Move + " depth: " + depth + " alpha: " + alpha + " beta: " + beta);
                }
            }
            else {

                if(nextMove != null)
                    points -= nextMove.Points;
                //System.out.println("player enemy, points: " + points);

                if(points <= bestMove.Points)
                {
                    if(points < bestMove.Points)
                    {
                        bestMoves.clear();
                    }

                    beta = points;
                    bestMove = new MovePoint(move, beta);
                    bestMoves.add(bestMove);
                    if(beta != 0)System.out.println("beta player: " + player + " depth: " + depth + " alpha: " + alpha + " beta: " + beta);
                }
            }

//            if(alpha != 0 || beta != 0)
                //System.out.println(playerNumber + " step player: " + player + " depth: "
//                                + depth + " alpha: " + alpha + " beta: " + beta + " bestmove: "
//                                + bestMove.Move + " posints: " + bestMove.Points);

            Move(pitch, new Move(move.toX, move.toY, move.fromX, move.fromY));
        }

        Random random = new Random();
        int rand = random.nextInt(bestMoves.size());
        if(depth == 4) System.out.println("move size: " + bestMoves.size() +" random: "  + rand + " move: " + bestMoves.get(rand).Move + " points: " + bestMove.Points);
        return bestMoves.get(rand);
    }

//    private int miniMax(GameTreeNode currentNode, int depth, int alpha, int beta) {
//        if (depth <= 0 || terminalNode(currentNode.getState())) {
//            return getHeuristic(currentNode.getState());
//        }
//        if (currentNode.getState().getCurrentPlayer().equals(selfColor)) {
//            int currentAlpha = -INFINITY;
//            for (GameTreeNode child : currentNode.getChildren()) {
//                currentAlpha = Math.max(currentAlpha, miniMax(child, depth - 1, alpha, beta));
//                alpha = Math.max(alpha, currentAlpha);
//                if (alpha >= beta) {
//                    return alpha;
//                }
//            }
//            return currentAlpha;
//        }
//        int currentBeta = INFINITY;
//        for (GameTreeNode child : currentNode.getChildren()) {
//            currentBeta = Math.min(currentBeta, miniMax(child, depth - 1, alpha, beta));
//            beta = Math.min(beta, currentBeta);
//            if (beta <= alpha) {
//                return beta;
//            }
//        }
//        return currentBeta;
//    }


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
