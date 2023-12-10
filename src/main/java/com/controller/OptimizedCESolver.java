package com.controller;

import java.util.*;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public class OptimizedCESolver extends BaseSolver{
    public String status = "UNSAT";
    public ArrayList<int[]> cnfClause;
    public boolean[][] blackPosible;

    OptimizedCESolver(int matrixSize, int[][] input) {
        this.matrixSize = matrixSize;
        this.input = input;
        this.cnfClause = new ArrayList<int[]>();
    }

    public void solve() {
        try {
            setupPosibleBlackCells();
            generateNotBlackClauses();
            generateFirstRuleClause();
            generateSecondRuleClause();
            generateThirdRuleClause();
        } catch (OutOfMemoryError e) {
            System.out.println("memory out");
            this.time = -1;
            this.result = new int[matrixSize][matrixSize];
            for (int i = 0; i < matrixSize; i++)
                for (int j = 0; j < matrixSize; j++)
                    this.result[i][j] = 0;
            return;
        }
        try {
            solveSAT();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }

    }

    public void setupPosibleBlackCells() {
        blackPosible = new boolean[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) 
            for (int j = 0; j < matrixSize; j++) {
                for (int k = 0; k < matrixSize; k++) {
                    if (k != j && input[i][j] == input[i][k]) {
                        blackPosible[i][j] = true;
                        break;
                    }

                    if (k != i && input[i][j] == input[k][j]) {
                        blackPosible[i][j] = true;
                        break;
                    }
                }
            }
    }

    public void generateNotBlackClauses() {
        for (int i = 0; i < matrixSize; i++) 
            for (int j = 0; j < matrixSize; j++) {
                if (!blackPosible[i][j]) {
                    int[] cnf = {makeIdForW(i + 1, j + 1)};
                    cnfClause.add(cnf);
                }
            }
    }

    // Luật này để xác định mỗi hàng và mỗi cột không có 2 ô trắng có giá trị bằng nhau
    public void generateFirstRuleClause() {
        for (int i = 0; i < matrixSize; i++) {
            for (int j1 = 0; j1 < matrixSize; j1++) {
                for (int j2 = j1 + 1; j2 < matrixSize; j2++) {
                    if (input[i][j1] > 0 && input[i][j1] == input[i][j2]) {
                        ArrayList<Integer> cnf = new ArrayList<Integer>();
                        cnf.add(-makeIdForW(i + 1, j1 + 1));
                        cnf.add(-makeIdForW(i + 1, j2 + 1));
                        cnfClause.add(arrayListToArray(cnf));
                    }
                }
            }
        }

        for (int j = 0; j < matrixSize; j++) {
            for (int i1 = 0; i1 < matrixSize; i1++) {
                for (int i2 = i1 + 1; i2 < matrixSize; i2++) {
                    if (input[i2][j] > 0 && input[i1][j] == input[i2][j]) {
                        ArrayList<Integer> cnf = new ArrayList<Integer>();
                        cnf.add(-makeIdForW(i1 + 1, j + 1));
                        cnf.add(-makeIdForW(i2 + 1, j + 1));
                        cnfClause.add(arrayListToArray(cnf));
                    }
                }
            }
        }
    }
    // Luật này để xác định hai ô nằm sát nhau không được cùng đen
    //      => 2 ô sát nhau phải có ít nhất một ô trắng
    // Với ô ij hiện tại ta chỉ cần xét ô bên phải và ô bên dưới thôi. 
    public void generateSecondRuleClause() {
        for (int i = 1; i <= matrixSize; i++) {
            for (int j = 1; j <= matrixSize; j++) {
                if (j != matrixSize) {
                    //fix here
                    if (blackPosible[i - 1][j - 1] && blackPosible[i - 1][j]) {
                        ArrayList<Integer> cnfRow = new ArrayList<Integer>();
                        cnfRow.add(makeIdForW(i, j));
                        cnfRow.add(makeIdForW(i, j + 1));
                        cnfClause.add(arrayListToArray(cnfRow));
                    }
                  
                }

                if (i != matrixSize) {
                    //fix here
                    if (blackPosible[i - 1][j - 1] && blackPosible[i][j - 1]) {
                        ArrayList<Integer> cnfColumn = new ArrayList<Integer>();
                        cnfColumn.add(makeIdForW(i, j));
                        cnfColumn.add(makeIdForW(i + 1, j));
                        cnfClause.add(arrayListToArray(cnfColumn));
                    }
                  
                }

            }
        }
    }

    // Luật này để tránh cycle và chain
    public void generateThirdRuleClause() {
        // nếu 2 ô ij, kl chéo nhau là ô đen thì Cijkl hoặc Cklij  là true
        for (int i = 1; i < matrixSize; i++) {
            for (int j = 1; j <= matrixSize; j++) {
                makeCNFToMakeC(i, j, i + 1, j - 1);
                makeCNFToMakeC(i, j, i + 1, j + 1);
            }
        }
        
        //Tính chất bắc cầu
        for (int u = 2; u < matrixSize; u++) {
            for (int v = 2; v < matrixSize; v++) {
                for (int i = 2; i < matrixSize; i++) {
                    for (int j = 2; j < matrixSize; j++) {
                        //chỉ xét các ô i,j ko phải biên làm cầu nối
                        //xét các ô x,y là ô chéo kề của ô i,j
                        //fix here
                        if (!blackPosible[u - 1][v - 1] || !blackPosible[i - 1][j - 1]) continue;
                        if (u == i && v == j) continue;
                        if ((u + v) % 2 != (i + j) % 2) continue;
                        for (int p1 = 0; p1 < 2; p1++) 
                            for (int p2 = 0; p2 < 2; p2++) {
                                int x = i + ((p1 == 1) ? 1 : -1); 
                                int y = j + ((p2 == 1) ? 1 : -1);
                                if (x == 1 || x == matrixSize || y == 1 || y == matrixSize) continue;
                                //fix here
                                if (!blackPosible[x - 1][y - 1]) continue;
                                ArrayList<Integer> cnf = new ArrayList<>();
                                cnf.add(-makeIdForC(u, v, i, j));
                                cnf.add(-makeIdForC(i, j, x, y));
                                cnf.add(makeIdForC(u, v, x, y));
                                cnfClause.add(arrayListToArray(cnf));
                            }
                    }
                }
            }
        }

        // không bao giờ có 2 hướng chỉ vào một ô -> hướng thống nhất
        for (int i = 2; i < matrixSize; i++) {
            for (int j = 2; j < matrixSize; j++) {
                makeCNFToMakeOneDirection(i, j, i - 1, j - 1, i - 1, j + 1);
                makeCNFToMakeOneDirection(i, j, i - 1, j - 1, i + 1, j - 1);
                makeCNFToMakeOneDirection(i, j, i - 1, j - 1, i + 1, j + 1);
                makeCNFToMakeOneDirection(i, j, i - 1, j + 1, i + 1, j - 1);
                makeCNFToMakeOneDirection(i, j, i - 1, j + 1, i + 1, j + 1);
                makeCNFToMakeOneDirection(i, j, i + 1, j - 1, i + 1, j + 1);
            }
        }

        // tránh tạo thành cycle
        for (int i = 1; i <= matrixSize; i++) {
            for (int j = 1; j <= matrixSize; j++) {
                //fix here
                if (!blackPosible[i - 1][j - 1]) continue;
                int ijijCId = makeIdForC(i, j, i, j);
                ArrayList<Integer> cnf = new ArrayList<>();
                cnf.add(-ijijCId);
                cnfClause.add(arrayListToArray(cnf));
            }
        }
    }

    private void makeCNFToMakeC(int i, int j, int k, int l) {
        if (!isValidPosition(i, j) || !isValidPosition(k, l)) return;
        if (!blackPosible[i - 1][j - 1]) return;
        if (!blackPosible[k - 1][l - 1]) return;
        int ijWId = makeIdForW(i, j);
        int klWId = makeIdForW(k, l);
        int ijklCId = makeIdForC(i, j, k, l);
        int klijCId = makeIdForC(k, l, i, j);
        ArrayList<Integer> cnf1 = new ArrayList<>();
        ArrayList<Integer> cnf2 = new ArrayList<>();
        cnf1.add(ijWId);
        cnf1.add(klWId);
        cnf1.add(-ijklCId);
        cnf1.add(-klijCId);

        cnf2.add(ijWId);
        cnf2.add(klWId);
        cnf2.add(ijklCId);
        cnf2.add(klijCId);

        cnfClause.add(arrayListToArray(cnf1));
        cnfClause.add(arrayListToArray(cnf2));

        if (k == 1 || k == matrixSize || l == 1 || l == matrixSize) {
            int[] clause = {-makeIdForC(i, j, k, l)};
            cnfClause.add(clause);
        }

        if (i == 1 || i == matrixSize || j == 1 || j == matrixSize) {
            int[] clause = {-makeIdForC(k, l, i, j)};
            cnfClause.add(clause);
        }
    }

    private void makeCNFToMakeOneDirection(int i, int j, int k, int l, int m, int n) {
        if (!blackPosible[i - 1][j - 1]) return;
        if (!blackPosible[k - 1][l - 1]) return;
        if (!blackPosible[m - 1][n - 1]) return;
        int klijCId = makeIdForC(k, l, i, j);
        int mnijCId = makeIdForC(m, n, i, j);
        ArrayList<Integer> cnf = new ArrayList<>();
        cnf.add(-klijCId);
        cnf.add(-mnijCId);
        cnfClause.add(arrayListToArray(cnf));
    }
    private void solveSAT() throws ContradictionException {
        this.result = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++)
            for (int j = 0; j < matrixSize; j++)
                this.result[i][j] = this.input[i][j];
        try {
            boolean isSAT = false;
            try { 
                for (int[] element : cnfClause) {
                    solver.addClause(new VecInt(element));
                }
                System.out.println("So luong menh de CE+: " + Integer.toString(solver.nConstraints()));

                long startTime = System.currentTimeMillis();
                isSAT = solver.isSatisfiable();
                long endTime = System.currentTimeMillis();
                this.time = endTime - startTime + 1;
            } catch (OutOfMemoryError e) {
                this.time = -1;
                System.out.println("too many clauses");
            }

            if (isSAT) {
                int[] model = solver.model();
                for (int i = 0; i < model.length; i++) {
                    int id = Math.abs(model[i]);
                    if (id <= matrixSize * matrixSize) {
                      
                        if (model[i] < 0) {
                            // System.out.println(model[i]);
                            int[] pos = IdWToPosition(id);
                            //System.out.println("i: " + Integer.toString(pos[0]) + ",j: " + Integer.toString(pos[1]));
                            this.result[pos[0] - 1][pos[1] - 1] = -this.input[pos[0] - 1][pos[1] - 1];
                        }
                    } /*else {

                        if (model[i] > 0) {
                            // System.out.println(model[i]);
                            int[] pos = IdCToPosition(id);
                            System.out.println("i: " + Integer.toString(pos[0]) + ",j: " + Integer.toString(pos[1])
                                    + ",k: " + Integer.toString(pos[2]) + ",l: " + Integer.toString(pos[3]));
                        }
                    }*/
                    // System.out.println(model[i]);

                }

            } else {
                System.out.println("UNSAT");
                for (int i = 0; i < matrixSize; i++)
                    for (int j = 0; j < matrixSize; j++)
                        this.result[i][j] = 0;
            }
        } catch (TimeoutException e) {
            System.out.println("Timeout");
            this.time = 0;
            for (int i = 0; i < matrixSize; i++)
                for (int j = 0; j < matrixSize; j++)
                    this.result[i][j] = 0;
        }
    }

    private boolean isValidPosition(int i, int j) {
        return (i >= 1 && i <= matrixSize && j >= 1 && j <= matrixSize);
    }

    private int makeIdForW(int i, int j) {
        return (i - 1) * matrixSize + j;
    }

    private int[] IdWToPosition(int id) {
        int i = (id - 1) / matrixSize + 1;
        int j = (id - 1) % matrixSize + 1;
        ArrayList<Integer> pos = new ArrayList<>();
        pos.add(i);
        pos.add(j);
        return arrayListToArray(pos);
    }

    private int makeIdForC(int i, int j, int k, int l) {
        return (matrixSize * matrixSize) + (i - 1) * matrixSize * matrixSize * matrixSize
                + (j - 1) * matrixSize * matrixSize
                + (k - 1) * matrixSize + l;
    }

    private int[] IdCToPosition(int id) {
        id = id - matrixSize * matrixSize;
        int i = (id - 1) / (matrixSize * matrixSize * matrixSize) + 1;
        int l = (id - 1) % matrixSize + 1;
        int k = ((id - l) / matrixSize) % matrixSize + 1;
        int j = (((id - l - (k - 1) * matrixSize) / (matrixSize * matrixSize)) % matrixSize) + 1;
        ArrayList<Integer> pos = new ArrayList<>();
        pos.add(i);
        pos.add(j);
        pos.add(k);
        pos.add(l);
        return arrayListToArray(pos);
    }

    private int[] arrayListToArray(ArrayList<Integer> arrayList) {
        int[] array = new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }
        return array;
    }
}