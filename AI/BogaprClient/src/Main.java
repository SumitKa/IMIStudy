import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import javax.imageio.ImageIO;

import lenz.htw.bogapr.Move;
import lenz.htw.bogapr.net.NetworkClient;

// java -Djava.library.path=lib/native -jar bogapr.jar

public class Main
{
    enum Player
    {
        One, Two, Three
    }

    class FieldConfiguration
    {
        public int X;
        public int Y;
        public int Stones;
        public Player Player;

        public FieldConfiguration(int x, int y, int stones, Player player)
        {
            X = x;
            Y = y;
            Stones = stones;
            Player = player;
        }
    }

    // x, y, player
    FieldConfiguration[][] configuration = new FieldConfiguration[13][7];

    public static void main(String[] args)
    {
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
