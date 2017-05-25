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

    private class MovePoint
    {
        public Move returnMove;

        public int returnPoints;

        public MovePoint(Move returnMove, int returnPoints)
        {
            this.returnMove = returnMove;
            this.returnPoints = returnPoints;
        }
    }

    /*public class MoveValue {

        public int returnValue;
        public Move returnMove;

        public MoveValue() {
            returnValue = 0;
        }

        public MoveValue(int returnValue) {
            this.returnValue = returnValue;
        }

        public MoveValue(Move returnMove, int returnValue) {
            this.returnValue = returnValue;
            this.returnMove = returnMove;
        }
    }*/

    //MovePoint currentBestMove = new MovePoint();

    static private String hostName, teamName;
    static private BufferedImage logo;

    int playerNumber;

    Setup setup = new Setup();


    public Client(String hostName, String teamName, BufferedImage logo) {
        this.hostName = hostName;
        this.teamName = teamName;
        this.logo = logo;
    }

    @Override
    public NetworkClient call() throws Exception {

        setup.setUpGamePitch();

        NetworkClient networkClient = new NetworkClient(hostName, teamName, logo);

        int latency = networkClient.getExpectedNetworkLatencyInMilliseconds();
        int time = networkClient.getTimeLimitInSeconds();
        playerNumber = networkClient.getMyPlayerNumber() + 1;
        int player = 0;

        for (;;) {
            Move receiveMove;
            while ((receiveMove = networkClient.receiveMove()) != null) {

                int currentplayer = setup.getTop(setup.gamePitch, receiveMove.fromX, receiveMove.fromY);

                int points = setup.move(setup.gamePitch, receiveMove);

                if(currentplayer == 1) {
                    setup.points1 += points;
                    if(player != 0 && player != 3)
                        setup.player3 = false;
                }
                else if(currentplayer == 2) {
                    setup.points2 += points;
                    if(player != 0 && player != 1)
                        setup.player1 = false;
                }
                else if(currentplayer == 3) {
                    setup.points3 += points;
                    if(player != 0 && player != 2)
                        setup.player2 = false;
                }

                player = currentplayer;

                if (playerNumber == 1) {
                    setup.printPitch(setup.gamePitch);
                    System.out.println("player " + player + " got " + points + " points with this move");
                    System.out.println("points: player 1: " + setup.points1 + " player 2: " + setup.points2 + " player 3: " + setup.points3);
                    System.out.println();
                    System.out.println("Next move player " + setup.getNextPlayer(player));
                }
            }

            networkClient.sendMove(getBestMove(playerNumber, 3));
            //networkClient.sendMove(getRandomMove(playerNumber));
        }

    }

    public Move getRandomMove(int player)
    {
//        int maxPoints = 0;
//        int[][]calculatedPitch = new int[gamePitch.length][];
//
//        for(int y = 0; y < gamePitch.length; y++) {
//            calculatedPitch[y] = new int[gamePitch[y].length];
//            for (int x = 0; x < gamePitch[y].length; x++)
//                calculatedPitch[y][x] = gamePitch[y][x];
//        }

        List<Setup.Field> tops = setup.getTops(setup.gamePitch, player);
        List<Move> validMoves = getValidMoves(setup.gamePitch, tops);

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

    public Move getBestMove(int player, int depth)
    {
        int[][]calculatedPitch = new int[setup.gamePitch.length][];

        for(int y = 0; y < setup.gamePitch.length; y++) {
            calculatedPitch[y] = new int[setup.gamePitch[y].length];
            for (int x = 0; x < setup.gamePitch[y].length; x++)
                calculatedPitch[y][x] = setup.gamePitch[y][x];
        }

        Move bestMove = getMoveMinMax(calculatedPitch, player, depth, 0, 0).returnMove;

        return bestMove;
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



    public MovePoint getMoveMinMax(int[][] pitch, int player, int depth, int alpha, int beta)
    {
        List<MovePoint> bestMoves = new ArrayList<>();
        MovePoint bestMove = new MovePoint(null, player == playerNumber ? alpha : beta);

        List<Setup.Field> tops = setup.getTops(pitch, player);
        for(Move move : getValidMoves(pitch, tops))
        {
            int points = setup.move(pitch, move);

            //System.out.println("Move " + move + " points: " + points);

            MovePoint nextMove = null;
            if(depth - 1 > 0) {
                nextMove = getMoveMinMax(pitch, setup.getNextPlayer(player), depth - 1, alpha, beta);
            }

            if (player == playerNumber) {

                if(nextMove != null)
                    points +=nextMove.returnPoints;
                //System.out.println("player me, points: " + points + " alpha: " + alpha + " best points: " + bestMove.Points);

                if(points >= bestMove.returnPoints)
                {
                    if(points > bestMove.returnPoints)
                    {
                        bestMoves.clear();
                    }


                    alpha = points;
                    bestMove = new MovePoint(move, alpha);
                    bestMoves.add(bestMove);
                    if(alpha != 0) System.out.println("alpha player: " + player + "move: " + bestMove.returnMove + " depth: " + depth + " alpha: " + alpha + " beta: " + beta);
                }
            }
            else {

                if(nextMove != null)
                    points -= nextMove.returnPoints;
                //System.out.println("player enemy, points: " + points);

                if(points <= bestMove.returnPoints)
                {
                    if(points < bestMove.returnPoints)
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

            setup.move(pitch, new Move(move.toX, move.toY, move.fromX, move.fromY));
        }

        Random random = new Random();
        int rand = random.nextInt(bestMoves.size());
        if(depth == 4) System.out.println("move size: " + bestMoves.size() +" random: "  + rand + " move: " + bestMoves.get(rand).returnMove + " points: " + bestMove.returnPoints);
        return bestMoves.get(rand);
    }


    public List<Move> getValidMoves(int[][] pitch, List<Setup.Field> tops)
    {
        List<Move> moves = new ArrayList<>();

        for(int i = 0; i < tops.size(); i++)
        {
            Setup.Field field = tops.get(i);
            moves.addAll(getValidMoves(pitch, field));
        }

        return  moves;
    }

    public List<Move> getValidMoves(int[][] pitch, Setup.Field field)
    {
        int x = field.X, y = field.Y;

        List<Move> moves = new ArrayList<>();

        if(field.Count == 1) {
            if (x > 0 && setup.hasNotMax(pitch, x - 1, y))
                moves.add(new Move(x, y, x - 1, y));
            if (x < pitch[y].length - 1 && setup.hasNotMax(pitch, x + 1, y))
                moves.add(new Move(x, y, x + 1, y));
            if (x % 2 != 0 && y > 1 && setup.hasNotMax(pitch, x - 1, y - 1))
                moves.add(new Move(x, y, x - 1, y - 1));
            if (x % 2 == 0 && y < pitch.length - 1 && setup.hasNotMax(pitch, x + 1, y + 1))
                moves.add(new Move(x, y, x + 1, y + 1));
        }
        else if(field.Count == 2) {
            if (x > 1) {
                if (setup.hasNotMax(pitch, x - 2, y))
                    moves.add(new Move(x, y, x - 2, y));
                if(setup.hasNotMax(pitch, x - 2, y - 1))
                    moves.add(new Move(x, y, x - 2, y - 1));
            }
            if (x < pitch[y].length - 2) {
                if (setup.hasNotMax(pitch, x + 2, y))
                    moves.add(new Move(x, y, x + 2, y));
                if (setup.hasNotMax(pitch, x, y - 1))
                    moves.add(new Move(x, y, x, y - 1));
            }
            if (y < pitch.length - 1) {
                if (setup.hasNotMax(pitch, x, y + 1))
                    moves.add(new Move(x, y, x, y + 1));
                if (setup.hasNotMax(pitch, x + 2, y + 1))
                    moves.add(new Move(x, y, x + 2, y + 1));
            }
        }
        else if(field.Count == 3) {
            if (x > 2 && setup.hasNotMax(pitch, x - 3, y))
                moves.add(new Move(x, y, x - 3, y));
            if (x < pitch[y].length - 3 && setup.hasNotMax(pitch, x + 3, y))
                moves.add(new Move(x, y, x + 3, y));

            if (y > 1) {
                if (x > 2 && setup.hasNotMax(pitch, x - 3, y - 1))
                    moves.add(new Move(x, y, x - 3, y - 1));
                if (x < pitch[y].length - 3 && setup.hasNotMax(pitch, x + 1, y - 1))
                    moves.add(new Move(x, y, x + 1, y - 1));
            }
            if (y < pitch.length - 1) {
                if (x > 0 && setup.hasNotMax(pitch, x - 1, y + 1))
                    moves.add(new Move(x, y, x - 1, y + 1));
                if (x < pitch[y].length - 1 && setup.hasNotMax(pitch, x + 3, y + 1))
                    moves.add(new Move(x, y, x + 3, y + 1));
            }

            if (x % 2 == 0) {
                if (x > 0 && x < pitch[y].length -1 && setup.hasNotMax(pitch, x - 1, y - 1))
                    moves.add(new Move(x, y, x - 1, y - 1));
                if (y < pitch.length - 2) {
                    if (setup.hasNotMax(pitch, x + 1, y + 2))
                        moves.add(new Move(x, y, x + 1, y + 2));
                    if (setup.hasNotMax(pitch, x + 3, y + 2))
                        moves.add(new Move(x, y, x + 3, y + 2));
                }
            }
            else {
                if (y > 2) {
                    if (x > 2 && setup.hasNotMax(pitch, x - 3, y - 2))
                        moves.add(new Move(x, y, x - 3, y - 2));
                    if (x < pitch[y].length - 3 && setup.hasNotMax(pitch, x - 1, y - 2))
                        moves.add(new Move(x, y, x - 1, y - 2));
                }
                if (y < pitch.length - 1) {
                    if (setup.hasNotMax(pitch, x + 1, y + 1))
                        moves.add(new Move(x, y, x + 1, y + 1));
                }
            }
        }

        return moves;
    }




}
