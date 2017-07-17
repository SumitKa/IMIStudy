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

    public List<Cell> getWaipoints(Color[][] board, int gridSize, int startX, int startY, int endX, int endY) {

        int width = board[0].length;
        int height = board.length;

        startX /= gridSize;
        startY /= gridSize;
        endX /= gridSize;
        endY /= gridSize;

        if (!setupBoard(board, startX, startY, endX, endY))
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

                while (current.getParent() != null) {
                    //System.out.print(" -> " + current.getParent().getX() + " " + current.getParent().getY());
                    current.setPositionToGridSize(gridSize);
                    path.add(current);
                    current = current.getParent();
                }

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
                checkAndUpdateCost(current, t, current.getFinalCost() + V_H_COST);

                if (current.getX() - 1 >= 0) {
                    t = _board[current.getY() - 1][current.getX() - 1];
                    checkAndUpdateCost(current, t, current.getFinalCost() + DIAGONAL_COST);
                }

                if (current.getX() + 1 < width) {
                    t = _board[current.getY() - 1][current.getX() + 1];
                    checkAndUpdateCost(current, t, current.getFinalCost() + DIAGONAL_COST);
                }
            }

            if (current.getX() - 1 >= 0) {
                t = _board[current.getY()][current.getX() - 1];
                checkAndUpdateCost(current, t, current.getFinalCost() + V_H_COST);
            }

            if (current.getX() + 1 < width) {
                t = _board[current.getY()][current.getX() + 1];
                checkAndUpdateCost(current, t, current.getFinalCost() + V_H_COST);
            }

            if (current.getY() + 1 < height) {
                t = _board[current.getY() + 1][current.getX()];
                checkAndUpdateCost(current, t, current.getFinalCost() + V_H_COST);

                if (current.getX() - 1 >= 0) {
                    t = _board[current.getY() + 1][current.getX() - 1];
                    checkAndUpdateCost(current, t, current.getFinalCost() + DIAGONAL_COST);
                }

                if (current.getX() + 1 < width) {
                    t = _board[current.getY() + 1][current.getX() + 1];
                    checkAndUpdateCost(current, t, current.getFinalCost() + DIAGONAL_COST);
                }
            }
        }

        //System.out.println("No path");

        return null;
    }

    private boolean setupBoard(Color[][] board, int startX, int startY, int endX, int endY) {
        int width = board[0].length;
        int height = board.length;

        _board = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!isBlocked(board, x, y)) {
                    _board[y][x] = new Cell(x, y);
                    _board[y][x].setHeuristicCost(Math.abs(x - endX) + Math.abs(y - endY));
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

    private boolean isBlocked(Color[][] board, int x, int y) {
        int blockedValue = 200;
        return board[y][x].getRed() <= blockedValue && board[y][x].getGreen() <= blockedValue && board[y][x].getBlue() <= blockedValue;
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
}