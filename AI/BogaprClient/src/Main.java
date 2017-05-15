import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import lenz.htw.bogapr.Move;
import lenz.htw.bogapr.net.NetworkClient;

// java -Djava.library.path=lib/native -jar bogapr.jar

public class Main
{
    class Field
    {
        public int X, Y, Count;

        public Field(int x, int y, int count)
        {
            X = x; Y = y; Count = count;
        }
    }

    // [y][x]
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

    public void AddStone(int x, int y, int player)
    {
        pitch[y][x] = pitch[y][x] / 10 + player * 100;
    }

    public void RemoveStone(int x, int y)
    {
        //pitch[y][x] =
    }

    public List<Field> GetTops(int player)
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

    public int GetTop(int x, int y)
    {
        return pitch[y][x] / 100;
    }


    public int GetCount(int x, int y)
    {

 //       int player = fieldInt / 100;
   //     int position = 0;
//
//        if(player != 0)
//        {
//            position++;
//
//            if(fieldInt % 100 / 10 != 0)
//            {
//                position++;
//                if(fieldInt % 100 % 10 != 0)
//                {
//                    position++;
//                }
//            }
//        }
//        return fieldCountd
        return 0;
    }

    public static Move GetValidMove(int player)
    {
        return new Move(0, 0, 0, 0);
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
                    //Zug in meine Brettrepräsentation einarbeiten
                }
                //berechne tollen Zug


                networkClient.sendMove(new Move(1, 1, 0, 2));

//                if(playerNumber == 0)
//                {
//                    networkClient.sendMove(new Move(0, 1, 1, 3));//5, 6, 2, 6));
//                }
//                else if(playerNumber == 1)
//                {
//                    networkClient.sendMove(new Move(0, 1, 1, 3));
//                }
//                else
//                {
//                    networkClient.sendMove(new Move(0, 1, 1, 3));
//                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("", e);
        }
    }

    private int[][] GetRandomFieldOfPlayer(Player player)
    {
        //List<int[][]> possibleFields = new List<int[][]>();

        for(int x = 0; x < 12; x++)
            for(int y = 0; y < 6; y++)
            {
                if(configuration[x][y].Player == player)
                {
                    return int[x][y];
                }
            }

        return null;
    }

    private void SetStartConfiguration()
    {
        // blue
        configuration[10][6] = new FieldConfiguration(3, Player.One);
        configuration[11][6] = new FieldConfiguration(3, Player.One);
        configuration[10][5] = new FieldConfiguration(3, Player.One);

        // red
        configuration[0][1] = new FieldConfiguration(3, Player.Two);
        configuration[1][1] = new FieldConfiguration(3, Player.Two);
        configuration[2][1] = new FieldConfiguration(3, Player.Two);

        // green
        configuration[5][0] = new FieldConfiguration(3, Player.Three);
        configuration[1][6] = new FieldConfiguration(3, Player.Three);
        configuration[2][6] = new FieldConfiguration(3, Player.Three);
    }
}
