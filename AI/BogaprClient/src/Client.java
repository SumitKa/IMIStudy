import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import lenz.htw.bogapr.Move;
import lenz.htw.bogapr.net.NetworkClient;

// java -Djava.library.path=lib/native -jar bogapr.jar

public class Client implements Callable<NetworkClient> {
    static private String hostName, teamName;
    static private BufferedImage logo;

    Setup setup;

    public Client(String hostName, String teamName, BufferedImage logo) {
        this.hostName = hostName;
        this.teamName = teamName;
        this.logo = logo;
    }

    @Override
    public NetworkClient call() throws Exception {

        NetworkClient networkClient = new NetworkClient(hostName, teamName, logo);

        int latency = networkClient.getExpectedNetworkLatencyInMilliseconds();
        int time = networkClient.getTimeLimitInSeconds();
        int playerNumber = networkClient.getMyPlayerNumber() + 1;

        setup = new Setup(playerNumber);
        setup.setUpGamePitch();

        int player = 0;

        for (; ; ) {
            Move receiveMove;
            while ((receiveMove = networkClient.receiveMove()) != null) {

                int currentplayer = setup.getTop(setup.gamePitch, receiveMove.fromX, receiveMove.fromY);

                int points = setup.move(setup.gamePitch, receiveMove, false);

                if (currentplayer == 1) {
                    setup.points1 += points;
                    if (player != 0 && player != 3)
                        setup.player3 = false;
                } else if (currentplayer == 2) {
                    setup.points2 += points;
                    if (player != 0 && player != 1)
                        setup.player1 = false;
                } else if (currentplayer == 3) {
                    setup.points3 += points;
                    if (player != 0 && player != 2)
                        setup.player2 = false;
                }

                player = currentplayer;

                if (playerNumber == 1) {
                    setup.printPitch(setup.gamePitch);
                    System.out.println("player " + player + " got " + points + " points with this move");
                    System.out.println("points: player 1: " + setup.points1 + " player 2: " + setup.points2 + " player 3: " + setup.points3);
                    System.out.println();
                    System.out.println("Next move player " + setup.getNextPlayer(player));
                    System.out.println("Latency: " + latency);
                }
            }

            if (playerNumber == 1)
                networkClient.sendMove(getBestMove(playerNumber, 5));
            else
                networkClient.sendMove(getRandomMove(playerNumber));
        }

    }

    public Move getRandomMove(int player) {
        List<Setup.Field> tops = setup.getTops(setup.gamePitch, player);
        List<Move> validMoves = getValidMoves(setup.gamePitch, tops);

        Random random = new Random();
        return validMoves.get(random.nextInt(validMoves.size()));
    }

    public Move getBestMove(int player, int depth) {
        int[][] calculatedPitch = new int[setup.gamePitch.length][];

        for (int y = 0; y < setup.gamePitch.length; y++) {
            calculatedPitch[y] = new int[setup.gamePitch[y].length];
            for (int x = 0; x < setup.gamePitch[y].length; x++)
                calculatedPitch[y][x] = setup.gamePitch[y][x];
        }

        setup.bestMoves.clear();

        getMoveAlphaBeta(calculatedPitch, player, 0, depth, -1000, 1000);

        Random random = new Random();
        int rand = random.nextInt(setup.bestMoves.size());

        System.out.println(setup.playerNumber + " COOSE: " + setup.bestMoves.size() + " !  " + rand + " , " + setup.bestMoves.get(rand));
        return setup.bestMoves.get(rand);
    }


    public int getMoveAlphaBeta(int[][] pitch, int player, int depth, int maxDepth, int alpha, int beta) {

        int currentAlpha;

        if (player == setup.playerNumber) {
            List<Setup.Field> tops = setup.getTops(pitch, player);
            for (Move move : getValidMoves(pitch, tops)) {

                currentAlpha = setup.move(pitch, move, true);

                if (depth + 1 < maxDepth) {
                    int nextMovePoints = getMoveAlphaBeta(pitch, setup.getNextPlayer(player), depth + 1, maxDepth, currentAlpha, beta);
                    currentAlpha += nextMovePoints;
                }

                setup.move(pitch, new Move(move.toX, move.toY, move.fromX, move.fromY), false);

                if (depth == 0 && currentAlpha > alpha) {
//                  System.out.println("Clear " + depth + " ! " + currentAlpha + " " + alpha);
                    setup.bestMoves.clear();
                }

                alpha = Integer.max(alpha, currentAlpha);

                if (depth == 0 && currentAlpha >= alpha) {

//                  System.out.println(playerNumber + " " + player + " best move: " + move + " current a: " + currentAlpha + " alpha: " + alpha + " beta: " + beta);
                    setup.bestMoves.add(move);
                }
            }

            if (depth != 0)
                return alpha;
        }
        if (depth == 0)
            return alpha;

        int currentBeta;


        List<Setup.Field> tops = setup.getTops(pitch, player);
        for (Move move : getValidMoves(pitch, tops)) {

            currentBeta = -setup.move(pitch, move, true);

//            if(playerNumber == 1)
//                currentBeta *= 2;
//            if(currentBeta < 0) {
//                System.out.println(playerNumber + " beta minus: " + player + " " + move + "curr: " + currentBeta);
//                PrintPitch(pitch);
//            }
            if (depth + 1 < maxDepth) {
                int nextMovePoints = getMoveAlphaBeta(pitch, setup.getNextPlayer(player), depth + 1, maxDepth, alpha, currentBeta);
                currentBeta += nextMovePoints;
            }

            setup.move(pitch, new Move(move.toX, move.toY, move.fromX, move.fromY), false);

            beta = Integer.min(beta, currentBeta);

//            if(beta > -maxDepth / 2)
//                return  beta;
//            if (currentBeta <= beta) {
//                return currentBeta;
//            }
        }

//        if(beta < 0)
//        System.out.println(playerNumber + " player: " + player + " beta: " + beta + " curr: " + currentBeta );
        return beta;
    }


    public List<Move> getValidMoves(int[][] pitch, List<Setup.Field> tops) {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < tops.size(); i++) {
            Setup.Field field = tops.get(i);
            moves.addAll(getValidMoves(pitch, field));
        }

        return moves;
    }

    public List<Move> getValidMoves(int[][] pitch, Setup.Field field) {
        int x = field.X, y = field.Y;

        List<Move> moves = new ArrayList<>();

        if (field.Count == 1) {
            if (x > 0 && setup.hasNotMax(pitch, x - 1, y))
                moves.add(new Move(x, y, x - 1, y));
            if (x < pitch[y].length - 1 && setup.hasNotMax(pitch, x + 1, y))
                moves.add(new Move(x, y, x + 1, y));
            if (x % 2 != 0 && y > 1 && setup.hasNotMax(pitch, x - 1, y - 1))
                moves.add(new Move(x, y, x - 1, y - 1));
            if (x % 2 == 0 && y < pitch.length - 1 && setup.hasNotMax(pitch, x + 1, y + 1))
                moves.add(new Move(x, y, x + 1, y + 1));
        } else if (field.Count == 2) {
            if (x > 1) {
                if (setup.hasNotMax(pitch, x - 2, y))
                    moves.add(new Move(x, y, x - 2, y));
                if (setup.hasNotMax(pitch, x - 2, y - 1))
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
        } else if (field.Count == 3) {
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
                if (x > 0 && x < pitch[y].length - 1 && setup.hasNotMax(pitch, x - 1, y - 1))
                    moves.add(new Move(x, y, x - 1, y - 1));
                if (y < pitch.length - 2) {
                    if (setup.hasNotMax(pitch, x + 1, y + 2))
                        moves.add(new Move(x, y, x + 1, y + 2));
                    if (setup.hasNotMax(pitch, x + 3, y + 2))
                        moves.add(new Move(x, y, x + 3, y + 2));
                }
            } else {
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
