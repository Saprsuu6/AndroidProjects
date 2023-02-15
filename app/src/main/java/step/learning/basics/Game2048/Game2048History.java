package step.learning.basics.Game2048;

import android.util.Pair;

import java.util.ArrayList;

public class Game2048History {
    private ArrayList<int[][]> fieldHistory = new ArrayList<>();
    private int score;

    public Pair<Integer, int[][]> getLastHistory() throws Exception {
        if (fieldHistory.size() == 0) {
            throw new Exception("You cannot undo at the start of the game");
        }

        if (fieldHistory.size() == 1) {
            throw new Exception(String.format("You cannot undo greater then %d steps", fieldHistory.size()));
        }

        int[][] lastHistory = fieldHistory.get(fieldHistory.size() - 1).clone();
        fieldHistory.remove(fieldHistory.size() - 1);

        return new Pair<Integer, int[][]>(score, lastHistory);
    }

    public void SaveHistory(int[][] cells, int score) {
        // region Check is equals
        if (fieldHistory.size() != 0) {
            int isEqualWithPrev = 0;

            int[][] temp = fieldHistory.get(fieldHistory.size() - 1);
            for (int i = 0; i < 4; ++i) {
                for (int j = 0; j < 4; ++j) {
                    if (cells[i][j] == temp[i][j]) {
                        isEqualWithPrev += 1;
                    }
                }
            }

            if (isEqualWithPrev == 16) {
                System.out.println("isEquals");
                return;
            }
        }
        //endregion

        // region Cloning
        int[][] clone = new int[4][4];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                clone[i][j] = cells[i][j];
            }
        }
        //endregion

        if (fieldHistory.size() == 3) {
            fieldHistory.remove(0);
        }

        fieldHistory.add(clone);
        this.score = score;
    }
}
