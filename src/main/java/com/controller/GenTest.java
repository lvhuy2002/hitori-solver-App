package com.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class GenTest {
    public static void main(String[] args) {
        /*
         * genSingleTest(11, 25);
         * genSingleTest(12, 30);
         * genSingleTest(13, 35);
         * genSingleTest(14, 40);
         * genSingleTest(15, 45);
         * genSingleTest(16, 50);
         * genSingleTest(17, 55);
         * genSingleTest(18, 60);
         * genSingleTest(19, 65);
         */
        genSingleTest(20, 70);
    }

    public static void genSingleTest(int testNumber, int matrixSize) {
        String filePath = "input/input" + testNumber + ".txt";
        try (
                FileOutputStream fos = new FileOutputStream(filePath, false);
                OutputStreamWriter writer = new OutputStreamWriter(fos);) {
            int[][] input = new int[matrixSize][matrixSize];
            int[][] colorMatrix = genColorState(matrixSize);
            Random rand = new Random();
            int maxNumber = matrixSize / 2;
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    if (colorMatrix[i][j] < 0) {
                        input[i][j] = rand.nextInt(maxNumber) + 1;
                        continue;
                    }
                    ArrayList<Integer> choices = new ArrayList<>();
                    for (int value = 1; value <= maxNumber; value++) {
                        boolean check = true;
                        for (int k = 0; k < i; k++)
                            if (colorMatrix[k][j] > 0 && input[k][j] == value) {
                                check = false;
                                break;
                            }
                        for (int k = 0; k < j; k++)
                            if (colorMatrix[i][k] > 0 && input[i][k] == value) {
                                check = false;
                                break;
                            }
                        if (check)
                            choices.add(value);
                    }
                    while (!choices.isEmpty()) {
                        int index = rand.nextInt(choices.size());
                        input[i][j] = choices.get(index);
                        boolean check = true;
                        for (int k = 0; k < i; k++)
                            if (colorMatrix[k][j] > 0 && input[k][j] == input[i][j]) {
                                check = false;
                                break;
                            }
                        for (int k = 0; k < j; k++)
                            if (colorMatrix[i][k] > 0 && input[i][k] == input[i][j]) {
                                check = false;
                                break;
                            }
                        if (check)
                            break;
                        choices.remove(index);
                    }
                    if (choices.isEmpty())
                        input[i][j] = ++maxNumber;
                }
            }
            System.out.println("max number: " + maxNumber);
            writer.write(matrixSize + "\n");
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    writer.write(input[i][j] + "  ");
                    if (input[i][j] < 10)
                        writer.write(" ");
                }
                writer.write("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[][] genSingleTestForApp(int matrixSize) {

        int[][] input = new int[matrixSize][matrixSize];
        int[][] colorMatrix = genColorState(matrixSize);
        Random rand = new Random();
        int maxNumber = matrixSize / 2;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (colorMatrix[i][j] < 0) {
                    input[i][j] = rand.nextInt(maxNumber) + 1;
                    continue;
                }
                ArrayList<Integer> choices = new ArrayList<>();
                for (int value = 1; value <= maxNumber; value++) {
                    boolean check = true;
                    for (int k = 0; k < i; k++)
                        if (colorMatrix[k][j] > 0 && input[k][j] == value) {
                            check = false;
                            break;
                        }
                    for (int k = 0; k < j; k++)
                        if (colorMatrix[i][k] > 0 && input[i][k] == value) {
                            check = false;
                            break;
                        }
                    if (check)
                        choices.add(value);
                }
                while (!choices.isEmpty()) {
                    int index = rand.nextInt(choices.size());
                    input[i][j] = choices.get(index);
                    boolean check = true;
                    for (int k = 0; k < i; k++)
                        if (colorMatrix[k][j] > 0 && input[k][j] == input[i][j]) {
                            check = false;
                            break;
                        }
                    for (int k = 0; k < j; k++)
                        if (colorMatrix[i][k] > 0 && input[i][k] == input[i][j]) {
                            check = false;
                            break;
                        }
                    if (check)
                        break;
                    choices.remove(index);
                }
                if (choices.isEmpty())
                    input[i][j] = ++maxNumber;
            }
        }
        return input;
    }

    public static int[][] genColorState(int matrixSize) {
        int[][] colorMatrix = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++)
            for (int j = 0; j < matrixSize; j++)
                colorMatrix[i][j] = 1;
        BaseSolver solver = new BaseSolver();
        int blackNumber = 0;
        Random rand = new Random();
        while (true) {
            ArrayList<Integer> choices = new ArrayList<>();
            for (int i = 0; i < matrixSize; i++)
                for (int j = 0; j < matrixSize; j++)
                    if (colorMatrix[i][j] > 0)
                        choices.add(i * matrixSize + j);
            while (!choices.isEmpty()) {
                int index = rand.nextInt(choices.size());
                int x = choices.get(index) / matrixSize;
                int y = choices.get(index) % matrixSize;
                if (colorMatrix[x][y] < 0)
                    continue;
                colorMatrix[x][y] = -1;
                if (solver.checkBlackAdjacent(colorMatrix, matrixSize)
                        && solver.checkConnections(colorMatrix, matrixSize))
                    break;
                colorMatrix[x][y] = 1;
                choices.remove(index);
            }
            if (choices.isEmpty())
                break;
            blackNumber++;
        }
        System.out.println("black cells: " + blackNumber);
        if (!solver.checkConnections(colorMatrix, matrixSize) || !solver.checkBlackAdjacent(colorMatrix, matrixSize))
            System.out.println("incorrect color");
        return colorMatrix;
    }
}