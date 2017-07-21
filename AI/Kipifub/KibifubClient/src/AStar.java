import jdk.nashorn.internal.runtime.regexp.joni.constants.AsmConstants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AStar {
    public static final int DIAGONAL_COST = 14;
    public static final int V_H_COST = 10;

    class Cell {
        private int _heuristicCost = 0; //Heuristic cost
        private int _finalCost = 0; //G+H
        private int _x, _y;
        private Cell _parent;

        Cell(int x, int y) {
            _x = x;
            _y = y;
        }

        public int getX() {
            return _x;
        }

        public int getY() {
            return _y;
        }

        public int getHeuristicCost() {
            return _heuristicCost;
        }

        public void setHeuristicCost(int heuristicCost) {
            _heuristicCost = heuristicCost;
        }

        public int getFinalCost() {
            return _finalCost;
        }

        public void setFinalCost(int finalCost) {
            _finalCost = finalCost;
        }

        public Cell getParent() {
            return _parent;
        }

        public void setParent(Cell parent) {
            _parent = parent;
        }

        public void setPositionToGridSize(int gridSize) {
            _x *= gridSize;
            _y *= gridSize;
        }

        //        @Override
//        public String toString() {
//            return "[" + this.i + ", " + this.j + "]";
//        }
    }

    private Cell[][] _board;
    private PriorityQueue<Cell> _open;
    private boolean _closed[][];

    private boolean[][] _blockedBoard;

    public List<Cell> getWaipoints(Color[][] board, Bot bot, int gridSize, int startX, int startY, int endX, int endY) {

        setupBlockedBoard(board);

        int width = board[0].length;
        int height = board.length;

        startX /= gridSize;
        startY /= gridSize;
        endX /= gridSize;
        endY /= gridSize;

        if (!setupBoard(board, bot, startX, startY, endX, endY))
            return null;

        _closed = new boolean[height][width];
        _open = new PriorityQueue<>((Object o1, Object o2) -> {
            Cell c1 = (Cell) o1;
            Cell c2 = (Cell) o2;

            return c1.getFinalCost() < c2.getFinalCost() ? -1 : c1.getFinalCost() > c2.getFinalCost() ? 1 : 0;
        });

        _open.add(_board[startY][startX]);

        Cell current;

        while (true) {
            current = _open.poll();
            if (current == null) break;
            _closed[current.getY()][current.getX()] = true;

            if (current.getX() == endX && current.getY() == endY) {

                List<Cell> path = new ArrayList<>();

                int lastX = current.getX();
                int lastY = current.getY();

                while (current.getParent() != null) {
                    //.out.print(" -> " + current.getParent().getX() + " " + current.getParent().getY());

                    int currentX = current.getX();
                    int currentY = current.getY();

                    if (getDistance(lastX, currentX) != getDistance(currentX, current.getParent().getX())
                            || getDistance(lastY, currentY) != getDistance(currentY, current.getParent().getY())) {
                        current.setPositionToGridSize(gridSize);
                        path.add(current);
                    }

                    lastX = currentX;
                    lastY = currentY;

                    current = current.getParent();
                }

                current.setPositionToGridSize(gridSize);
                path.add(current);
                //System.out.println();

                return path;
            }

            Cell t;

            // TODO
//            for(int x = -1; x <= 1; x++)
//                for(int y = -1; y <= 1; y++)
//                {
//                    if(x  == 0 && y == 0)
//                        continue;
//
//                    t = _board[current.getY() - 1][current.getX()];
//                    checkAndUpdateCost(current, t, current.getFinalCost() + V_H_COST);
//                }


            if (current.getY() - 1 >= 0) {
                t = _board[current.getY() - 1][current.getX()];
                checkAndUpdateCost(current, t, current.getFinalCost() + V_H_COST + getColorCost(board[current.getY()][current.getX()], bot));

                if (current.getX() - 1 >= 0) {
                    t = _board[current.getY() - 1][current.getX() - 1];
                    checkAndUpdateCost(current, t, current.getFinalCost() + DIAGONAL_COST + getColorCost(board[current.getY()][current.getX()], bot));
                }

                if (current.getX() + 1 < width) {
                    t = _board[current.getY() - 1][current.getX() + 1];
                    checkAndUpdateCost(current, t, current.getFinalCost() + DIAGONAL_COST + getColorCost(board[current.getY()][current.getX()], bot));
                }
            }

            if (current.getX() - 1 >= 0) {
                t = _board[current.getY()][current.getX() - 1];
                checkAndUpdateCost(current, t, current.getFinalCost() + V_H_COST + getColorCost(board[current.getY()][current.getX()], bot));
            }

            if (current.getX() + 1 < width) {
                t = _board[current.getY()][current.getX() + 1];
                checkAndUpdateCost(current, t, current.getFinalCost() + V_H_COST + getColorCost(board[current.getY()][current.getX()], bot));
            }

            if (current.getY() + 1 < height) {
                t = _board[current.getY() + 1][current.getX()];
                checkAndUpdateCost(current, t, current.getFinalCost() + V_H_COST + getColorCost(board[current.getY()][current.getX()], bot));

                if (current.getX() - 1 >= 0) {
                    t = _board[current.getY() + 1][current.getX() - 1];
                    checkAndUpdateCost(current, t, current.getFinalCost() + DIAGONAL_COST + getColorCost(board[current.getY()][current.getX()], bot));
                }

                if (current.getX() + 1 < width) {
                    t = _board[current.getY() + 1][current.getX() + 1];
                    checkAndUpdateCost(current, t, current.getFinalCost() + DIAGONAL_COST + getColorCost(board[current.getY()][current.getX()], bot));
                }
            }
        }

        //System.out.println("No path");

        return null;
    }

    private boolean setupBoard(Color[][] board, Bot bot, int startX, int startY, int endX, int endY) {
        int width = board[0].length;
        int height = board.length;

        _board = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!isBlocked(x, y)) {
                    _board[y][x] = new Cell(x, y);
                    _board[y][x].setHeuristicCost(Math.abs(x - endX) + Math.abs(y - endY));// + getHeuristicColorCost(board[y][x], bot));
                } else
                    _board[y][x] = null;
            }
        }

        if (_board[startY][startX] != null) {
            _board[startY][startX].setFinalCost(0);
            return true;
        } else
            return false;
    }

    private int getColorCost(Color color, Bot bot) {
        int cost = 255;

        if (bot.getPlayerNumber() == 0)
            cost += color.getRed() * 2 - color.getBlue() * 1.2f - color.getGreen() * 1.2f;
        else if (bot.getPlayerNumber() == 1)
            cost += color.getGreen() * 2 - color.getRed() * 1.2f - color.getBlue() * 1.2f;
        else if (bot.getPlayerNumber() == 2)
            cost += color.getBlue() * 2 - color.getRed() * 1.2f - color.getGreen() * 1.2f;

        cost /= 2;

        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50)
            cost += 1000;

        return cost < 0 ? 0 : cost;
    }

    public void setupBlockedBoard(Color[][] board) {
        if (_blockedBoard == null) {
            int width = board[0].length;
            int height = board.length;

            _blockedBoard = new boolean[height][width];

            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) {
                    if (!_blockedBoard[y][x]) {
                        boolean notWhite = board[y][x].getRed() != 255 && board[y][x].getGreen() != 255 && board[y][x].getBlue() != 255;
                        _blockedBoard[y][x] = notWhite;

                        if (notWhite)
                            for (int yy = -1; yy <= 1; yy++)
                                for (int xx = -1; xx <= 1; xx++)
                                    if (y + yy > 0 && y + yy < _blockedBoard.length && x + xx > 0 && x + xx < _blockedBoard[0].length)
                                        _blockedBoard[y + yy][x + xx] = true;
                    }
                }
        }
    }

    private boolean isBlocked(int x, int y) {
        if (_blockedBoard == null || _blockedBoard.length < y || _blockedBoard[0].length < x)
            return false;
        return _blockedBoard[y][x];
    }

    private void checkAndUpdateCost(Cell current, Cell t, int cost) {
        if (t == null || _closed[t.getY()][t.getX()]) return;
        int t_final_cost = t.getHeuristicCost() + cost;

        boolean inOpen = _open.contains(t);
        if (!inOpen || t_final_cost < t.getFinalCost()) {
            t.setFinalCost(t_final_cost);
            t.setParent(current);
            if (!inOpen) _open.add(t);
        }
    }

//    private float getDistance(int startX, int startY, int endX, int endY) {
//        float xDistance = getDistance(startX, endX);
//        float yDistance = getDistance(startY, endY);
//
//        return (float) Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
//    }

    private int getDistance(int start, int end) {
        return Math.abs(start - end);
    }
}