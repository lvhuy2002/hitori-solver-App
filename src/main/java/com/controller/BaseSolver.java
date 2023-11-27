package com.controller;


import java.util.ArrayList;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;

public class BaseSolver {
    public int matrixSize;
    public int[][] input;
    public ISolver solver = SolverFactory.newDefault();
    public long time = 0;
    public int[][] result;

    public final int[] X = {0, 0, 1, -1};
    public final int[] Y = {1, -1, 0, 0};
    public boolean checkColumns(int[][] result, int matrixSize) {
        for (int j = 0; j < matrixSize; j++) {
            for (int i1 = 0; i1 < matrixSize; i1++) {
                for (int i2 = i1 + 1; i2 < matrixSize; i2++) {
                    if (result[i1][j] > 0 && result[i1][j] == result[i2][j])
                        return false;
                }
            }
        }
        return true;
    }

    public boolean checkRows(int[][] result, int matrixSize) {
        for (int i = 0; i < matrixSize; i++) {
            for (int j1 = 0; j1 < matrixSize; j1++) {
                for (int j2 = j1 + 1; j2 < matrixSize; j2++) {
                    if (result[i][j1] > 0 && result[i][j1] == result[i][j2])
                        return false;
                }
            }
        }
        return true;
    }

    public boolean isValidPosition(int x, int y, int matrixSize) {
        return (x >= 0 && x < matrixSize && y >= 0 && y < matrixSize);
    }

    public boolean checkConnections(int[][] result, int matrixSize) {
        ArrayList<Integer> queue = new ArrayList<>();
        int index = 0;
        if (result[0][0] > 0) queue.add(0);
        else queue.add(1);
        while (index < queue.size()) {
            int u = queue.get(index) / matrixSize;
            int v = queue.get(index) % matrixSize;
            for (int i = 0; i < 4; i++) {
                int x = u + X[i];
                int y = v + Y[i];
                if (isValidPosition(x, y, matrixSize) && result[x][y] > 0) {
                    int newPosition = x * matrixSize + y;
                    if (!queue.contains(newPosition)) 
                        queue.add(newPosition);
                }
            }
            index++;
        }
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int position = i * matrixSize + j;
                if (result[i][j] > 0 && !queue.contains(position)) return false;
            }
        }
        return true;
    }

    public boolean checkBlackAdjacent(int[][] result, int matrixSize) {
        for (int i = 0; i < matrixSize; i++)
            for (int j = 0; j < matrixSize; j++) {
                if (i > 0 && result[i][j] < 0 && result[i - 1][j] < 0) return false;
                if (j > 0 && result[i][j] < 0 && result[i][j - 1] < 0) return false;
            }
        return true;
    }

    public boolean validateResult() {
        int matrixSize = result.length;
        return (checkColumns(result, matrixSize) && checkRows(result, matrixSize) 
                && checkConnections(result, matrixSize) && checkBlackAdjacent(result, matrixSize));
    }
}