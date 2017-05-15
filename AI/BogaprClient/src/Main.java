import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import lenz.htw.bogapr.Move;
import lenz.htw.bogapr.net.NetworkClient;

// java -Djava.library.path=lib/native -jar bogapr.jar

public class Main
{
    static class Field
    {
        public int X, Y, Count;

        public Field(int x, int y, int count)
        {
            X = x; Y = y; Count = count;
        }
    }

    // [y][x] & einer = top stone
    static int[][]pitch = new int[7][];

    public static void SetUpPitch()
    {
        for (int y = 0; y <= 6; y++)
        {
            // no [6][12]
            if (y != 6) pitch[y] = new int[y * 2 + 1];
            else pitch[y] = new int[12];

            for (int x = 0; x < pitch[y].length; x++) {
                pitch[y][x] = 0;
            }
        }

        // no [0][0] and no [6][0]
        pitch[0][0] = pitch[6][0] = 999;

        SetUpPlayer();
    }

    public static void SetUpPlayer()
    {
        pitch[1][0] = pitch[1][1] = pitch[1][2] = 111;
        pitch[5][0] = pitch[6][1] = pitch[6][2] = 222;
        pitch[5][10] = pitch[6][10] = pitch[6][11] = 333;
    }

    public static void PrintPitch()
    {
        for(int y = pitch.length - 1; y >= 0; y--)
        {
            for(int x = 0; x < pitch[y].length; x++)
            {
                System.out.print("[" + pitch[y][x] + "]");
            }

            System.out.println();
        }
    }

    public static void AddStone(int x, int y, int player)
    {
        pitch[y][x] = pitch[y][x] * 10 + player;
    }

    public static int RemoveStone(int x, int y)
    {
        int player = GetTop(x, y);
        pitch[y][x] = pitch[y][x] / 10;

        return player;
    }

    // ggf. beim setzten direkt in einer liste abspeichern
    public static List<Field> GetTops(int player)
    {
        List<Field> tops = new ArrayList<>();

        for (int y = 0; y < pitch.length; y++)
        {
            for(int x = 0; x < pitch[y].length; x++)
            {
                if(GetTop(x, y) == player)
                {
                    tops.add(new Field(x, y, GetCount(x, y)));
                }
            }
        }

        return tops;
    }

    public static int GetTop(int x, int y)
    {
        return pitch[y][x] % 10;
    }

    // teuer -> direkt beim setzten merken
    public static int GetCount(int x, int y)
    {
        int pitchInt = pitch[y][x];
        int count = 0;

        if(pitchInt % 10 != 0)
        {
            count++;
            if(pitchInt % 100 != 0)
            {
                count++;
                if(pitchInt / 100 != 0)
                    count++;
            }
        }

        return count;
    }

    public static boolean HasMax(int x, int y)
    {
        return pitch[y][x] / 100 != 0;
    }

    public static boolean IsMax(int fieldInt)
    {
        return fieldInt / 100 != 0;
    }

    public static Move GetValidMove(int player)
    {
        List<Field> tops = GetTops(player);
        List<Move> validMoves = GetValidMoves(tops);

        Random random = new Random();
        // funktioniert random so???
        return validMoves.get(random.nextInt(validMoves.size()));
    }

    public static List<Move> GetValidMoves(List<Field> tops)
    {
        List<Move> moves = new ArrayList<>();

        for(int i = 0; i < tops.size(); i++)
        {
            Field field = tops.get(i);

            moves.addAll(GetValidMoves(field));
        }

        return  moves;
    }

    public static List<Move> GetValidMoves(Field field)
    {
        int x = field.X, y = field.Y;

        List<Move> moves = new ArrayList<>();

        switch(field.Count) {
            case 1:
                if (x > 0 && HasMax(x-1, y))
                    moves.add(new Move(x, y, x-1, y));
                if (x < pitch[y].length - 1 && HasMax(x+1, y))
                    moves.add(new Move(x, y, x+1, y));
                if (x % 2 != 0 && y > 1 && HasMax(x-1, y-1))
                    moves.add(new Move(x, y, x-1, y-1));
                if (x % 2 == 0 && y < pitch.length - 1 && HasMax(x+1, y+1))
                    moves.add(new Move(x, y, x+1, y+1));
            case 2:
                if (x > 1 && HasMax(x-2, y))
                    moves.add(new Move(x, y, x-2, y));
                if (x < pitch[y].length - 2 && HasMax(x+2, y))
                    moves.add(new Move(x, y, x+2, y));
                if(y > 1) {
                    if (HasMax(x, y-1))
                        moves.add(new Move(x, y, x, y-1));
                    if (x > 1 && HasMax(x-2, y-1))
                        moves.add(new Move(x, y, x-2, y-1));
                }
                if(y < pitch.length - 1) {
                    if (HasMax(x, y++))
                        moves.add(new Move(x, y, x, y++));
                    if (HasMax(x + 2, y++))
                        moves.add(new Move(x, y, x + 2, y++));
                }
            case 3:
                if (x > 2 && HasMax(x-3, y))
                    moves.add(new Move(x, y, x-3, y));
                if (x < pitch[y].length - 3 && HasMax(x+3, y))
                    moves.add(new Move(x, y, x+3, y));
                if(x % 2 != 0)
                {
                    // TODO....
//                    if (y > 1 && HasMax(x, y--))
//                        moves.add(new Move(x, y, x, y--));
//                    if (y > 1 && HasMax(x - 2, y--))
//                        moves.add(new Move(x, y, x - 2, y--));
//                    if (y < pitch.length - 1 && HasMax(x, y++))
//                        moves.add(new Move(x, y, x, y++));
//                    if (y < pitch.length - 1 && HasMax(x + 2, y++))
//                        moves.add(new Move(x, y, x + 2, y++));
//                    if (y > 1 && HasMax(x - 2, y--))
//                        moves.add(new Move(x, y, x - 2, y--));
//                    if (y < pitch.length - 1 && HasMax(x, y++))
//                        moves.add(new Move(x, y, x, y++));
//                    if (y < pitch.length - 1 && HasMax(x + 2, y++))
//                        moves.add(new Move(x, y, x + 2, y++));
                }
                else
                {
                    if (y > 1) {
                        if (x > 2 && HasMax(x - 3, y - 1))
                            moves.add(new Move(x, y, x - 3, y - 1));
                        if (x < pitch[y].length - 3 && HasMax(x + 1, y - 1))
                            moves.add(new Move(x, y, x + 1, y - 1));
                    }
                    if(y > 2)
                    {
                        if (x > 2 && HasMax(x - 3, y - 2))
                            moves.add(new Move(x, y, x - 3, y - 2));
                        if (x < pitch[y].length - 3 && HasMax(x - 1, y - 2))
                            moves.add(new Move(x, y, x - 1, y - 2));
                    }
                    if(y < pitch.length - 1)
                    {
                        if (HasMax(x - 1, y + 1))
                            moves.add(new Move(x, y, x - 1, y + 1));
                        if (HasMax(x + 1, y + 1))
                            moves.add(new Move(x, y, x + 1, y + 1));
                        if (HasMax(x +3, y +1))
                            moves.add(new Move(x, y, x +3, y +1));
                    }
                }
        }
        return moves;
    }

    public static void Move(Move move)
    {
        int player = RemoveStone(move.fromX, move.fromY);
        AddStone(move.toX, move.toY, player);
    }

    public static void main(String[] args)
    {
        SetUpPitch();
        PrintPitch();

        try
        {
            NetworkClient networkClient = new NetworkClient(null, "CLIENT 0", ImageIO.read(new File("Content/logo.png")));

            int latency = networkClient.getExpectedNetworkLatencyInMilliseconds();
            int time = networkClient.getTimeLimitInSeconds();
            int playerNumber = networkClient.getMyPlayerNumber();

            for (;;) {
                Move receiveMove;
                while ((receiveMove = networkClient.receiveMove()) != null) {
                    Move(receiveMove);
                }

                Move move = GetValidMove(playerNumber + 1);
                networkClient.sendMove(move);
                Move(move);
                PrintPitch();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("", e);
        }
    }
}
