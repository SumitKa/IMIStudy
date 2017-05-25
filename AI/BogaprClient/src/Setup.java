import lenz.htw.bogapr.Move;

import java.util.ArrayList;
import java.util.List;

public class Setup {

    // [y][x] & einer = top stone
    int[][] gamePitch = new int[7][];


    int points1 = 0, points2 = 0, points3 = 0;
    boolean player1 = true, player2 = true, player3 = true;


    public class Field
    {
        public int X, Y, Count, Top;

        public Field(int x, int y, int count, int top)
        {
            X = x; Y = y; Count = count; Top = top;
        }
    }

    // TODO ...

    public void setUpGamePitch()
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

        setUpPlayer();
    }

    public void setUpPlayer()
    {
        gamePitch[1][0] = gamePitch[1][1] = gamePitch[1][2] = 111;
        gamePitch[5][0] = gamePitch[6][1] = gamePitch[6][2] = 222;
        gamePitch[5][10] = gamePitch[6][10] = gamePitch[6][11] = 333;
    }

    public void printPitch(int[][] pitch)
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


    public int move(int[][] pitch, Move move)
    {
        int player = removeStone(pitch, move.fromX, move.fromY);
        int points = addStone(pitch, move.toX, move.toY, player);

        return points;
    }

    public int addStone(int[][] pitch, int x, int y, int player)
    {
        int points = getPoints(pitch, x, y, player);
        int stones = pitch[y][x] * 10 + player;
        pitch[y][x] = stones;

        return points;
    }

    public int getPoints(int[][] pitch, int x, int y, int player)
    {
        int points = 0;

        int currentTopPlayer = getTop(pitch, x, y);

        if(currentTopPlayer != 0 && currentTopPlayer != player)
        {
            points++;
        }

        if(isEndMove(x, y, player))
        {
            points += 5;
        }

        return points;
    }

    public boolean isEndMove(int x, int y, int player)
    {
        boolean isEnd = false;
        if((player == 1 && y == 6 && x % 2 == 0)
                ||(player == 2 && y * 2 == x)
                ||(player == 3 && x == 0))
            isEnd = true;
        return isEnd;
    }

    public boolean shouldEndGame(int player)
    {
        boolean shouldEnd = false;
        if((player == 1 && points1 > points2 && points3 > points3)
                ||(player == 2 && points2 > points1 && points3 > points3)
                ||(player == 3 && points3 > points1 && points2 > points3))
            shouldEnd = true;
        return shouldEnd;
    }

    public int removeStone(int[][] pitch, int x, int y)
    {
        int player = getTop(pitch, x, y);
        pitch[y][x] = pitch[y][x] / 10;
        return player;
    }

    // ggf. beim setzten direkt in einer liste abspeichern
    public List<Field> getTops(int[][] pitch, int player)
    {
        List<Field> tops = new ArrayList<>();

        for (int y = 0; y < pitch.length; y++)
            for(int x = 0; x < pitch[y].length; x++)
                if(getTop(pitch, x, y) == player)
                    tops.add(new Field(x, y, getCount(pitch, x, y), player));

        //System.out.println(tops.size() + " tops for player " + player);

        return tops;
    }

    public int getTop(int[][] pitch, int x, int y)
    {
        int player = pitch[y][x] % 10;
        return player;
    }

    // teuer -> direkt beim setzen merken
    public int getCount(int[][] pitch, int x, int y)
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

    public boolean hasNotMax(int[][] pitch, int x, int y)
    {
        return pitch[y][x] / 100 == 0;
    }

    public int getNextPlayer(int currentPlayer)
    {
        do {
            currentPlayer = currentPlayer < 3 ? currentPlayer + 1 : 1;
        }while(!doesPlayerExist(currentPlayer));

        return currentPlayer;
    }

    public boolean doesPlayerExist(int player)
    {
        if((player == 1 && player1)
                || (player == 2 && player2)
                || (player == 3 && player3))
            return  true;
        return false;
    }



}
