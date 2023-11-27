package com.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class HitoriController {
    public static long recentTime = 0;
    public static int recentVariable = 0;
    public static int recentClause = 0;

    public static void main(String[] args) {
        // for (int i = 1; i <= 20; i++) {
        // runHitoriSolverSingleTest(i);
        // }
        runHitoriSolverSingleTest(4);
        // compareResultsOfMultipleSolvers(18);
    }

    private static void runHitoriSolverSingleTest(int testNumber) {
        int[][] input = readInput("input/input" + testNumber + ".txt");
        // OriginCESolver solver = new OriginCESolver(input.length, input);
        OptimizedCESolver solver = new OptimizedCESolver(input.length, input);
        solver.solve();
        logResult(testNumber, solver.result);
        System.out.println(solver.time);
        // check result
        if (solver.validateResult())
            System.out.print("CORRECT\n");
        else
            System.out.print("INCORRECT\n");
        assert (solver.validateResult());
    }

    private static void runOriginSolverToCompare(OutputStreamWriter writer, int testNumber, int[][] input)
            throws IOException {
        OriginCESolver originSolver = new OriginCESolver(input.length, input);
        originSolver.solve();
        assert (originSolver.validateResult());
        writer.write(testNumber + ", " + input.length + ", ");
        writer.write(originSolver.solver.nVars() + ", " + originSolver.solver.nConstraints() + ", ");
        if (originSolver.time > 0)
            writer.write(originSolver.time + ", ");
        else if (originSolver.time == 0)
            writer.write("timeout, ");
        else
            writer.write("memory out, ");
    }

    private static void runOptimizedSolverToCompare(OutputStreamWriter writer, int testNumber, int[][] input)
            throws IOException {
        OptimizedCESolver optimizedSolver = new OptimizedCESolver(input.length, input);
        optimizedSolver.solve();
        writer.write(optimizedSolver.solver.nVars() + ", " + optimizedSolver.solver.nConstraints() + ", ");
        if (optimizedSolver.time > 0)
            writer.write(optimizedSolver.time + " ");
        else if (optimizedSolver.time == 0)
            writer.write("timeout");
        else
            writer.write("memory out");
        writer.write("\n");
    }

    private static void compareResultsOfMultipleSolvers(int totalTests) {
        String filePath = "output/comparison.csv";
        try (
                FileOutputStream fos = new FileOutputStream(filePath, false);
                OutputStreamWriter writer = new OutputStreamWriter(fos);) {
            writer.write(", , CE, , ,CE+\n");
            writer.write("test, size, variables, clauses, time, variables, clauses, time\n");
            for (int testNumber = 1; testNumber <= totalTests; testNumber++) {
                int[][] input = readInput("input/input" + testNumber + ".txt");
                runOriginSolverToCompare(writer, testNumber, input);
                runOptimizedSolverToCompare(writer, testNumber, input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void logResult(int testNumber, int[][] result) {
        String filePath = "output/output" + testNumber + ".txt";
        try (
                FileOutputStream fos = new FileOutputStream(filePath, false);
                OutputStreamWriter writer = new OutputStreamWriter(fos);) {
            for (int i = 0; i < result.length; i++) {
                for (int j = 0; j < result.length; j++) {
                    writer.write(result[i][j] + "  ");
                    if (result[i][j] > -10)
                        writer.write(" ");
                    if (result[i][j] > 0 && result[i][j] < 10)
                        writer.write(" ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[][] runSolver(int[][] input, int type) {
        if (type == 2) {
            OptimizedCESolver hitoriSolver = new OptimizedCESolver(input.length, input);
            hitoriSolver.solve();
            recentTime = hitoriSolver.time;
            recentClause = hitoriSolver.solver.nConstraints();
            recentVariable = hitoriSolver.solver.nVars();
            return hitoriSolver.result;
        }
        OriginCESolver hitoriSolver = new OriginCESolver(input.length, input);
        hitoriSolver.solve();
        recentTime = hitoriSolver.time;
        recentClause = hitoriSolver.solver.nConstraints();
        recentVariable = hitoriSolver.solver.nVars();
        return hitoriSolver.result;
    }

    public static boolean validateHitori(int[][] matrix) {
        int matrixSize = matrix.length;
        BaseSolver validation = new BaseSolver();
        return (validation.checkColumns(matrix, matrixSize) && validation.checkRows(matrix, matrixSize)
                && validation.checkConnections(matrix, matrixSize));
    }

    public static int[][] readInput(String path) {
        int matrixSize;
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            String firstElement = scanner.next();
            matrixSize = Integer.parseInt(firstElement);

            int[][] array = new int[matrixSize][matrixSize];
            int num = 0;
            while (scanner.hasNext()) {
                String element = scanner.next();
                if (element.equals("."))
                    array[num / matrixSize][num % matrixSize] = 0;
                else
                    array[num / matrixSize][num % matrixSize] = Integer.parseInt(element);
                num++;
            }
            scanner.close();

            return array;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[0][0];
    }

    public static int[][] readFileInput(File file) {
        int matrixSize;
        try {
            Scanner scanner = new Scanner(file);
            String firstElement = scanner.next();
            matrixSize = Integer.parseInt(firstElement);

            int[][] array = new int[matrixSize][matrixSize];
            int num = 0;
            while (scanner.hasNext()) {
                String element = scanner.next();
                if (element.equals("."))
                    array[num / matrixSize][num % matrixSize] = 0;
                else
                    array[num / matrixSize][num % matrixSize] = Integer.parseInt(element);
                num++;
            }
            scanner.close();

            return array;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[0][0];
    }

    public static void printArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                System.out.print(array[i][j] + "  ");
                if (array[i][j] < 10)
                    System.out.print(" ");
            }
            System.out.println();
        }
    }
}