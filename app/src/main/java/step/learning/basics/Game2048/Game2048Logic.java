package step.learning.basics.Game2048;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048Logic {
    private final Game2048Activity activity;
    private int[][] cells = new int[4][4];
    private final Random random = new Random();
    private int score;
    private int bestScore = 0;
    private int firstNum; // первое отображаемое число
    private int goal;
    private final Game2048History history = new Game2048History();
    private final String bestScoreFileName = "best_score.txt";

    public void setContinuePlaying(boolean continuePlaying) {
        isContinuePlaying = continuePlaying;
    }

    private boolean isContinuePlaying = false;  // продолжение игры после набора 2048

    public Game2048Logic(Game2048Activity activity) {
        this.activity = activity;

        try {
            LoadBestScore();
        } catch (IOException e) {
            Log.d("saveBestScore", e.getMessage());
            bestScore = 0;
        }

        FirstSteps();
    }

    // region Start of the game
    public void FirstSteps() {
        score = 0;
        activity.ShowBestScoreInfo(bestScore);

        firstNum = random.nextInt(10) == 0 ? 4 : 2;
        goal = firstNum == 2 ? 4 : 8;
    }

    public void NewGame() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                cells[i][j] = 0;
            }
        }

        FirstSteps();

        SpawnCell();
        SpawnCell();
    }
    // endregion

    /**
     * Spawn random cell
     *
     */
    public void SpawnCell() {
        List<Integer> freeCells = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (cells[i][j] == 0) {
                    freeCells.add(i * 10 + j);   // сохраняем координаты свободной ячейки одним числом
                }
            }
        }

        int cnt = freeCells.size();
        if (cnt == 0) return;

        int rnd = random.nextInt(cnt);

        int x = freeCells.get(rnd) / 10;
        int y = freeCells.get(rnd) % 10;

        if (firstNum != 0) {
            cells[x][y] = firstNum;
            firstNum = 0;
        } else {
            cells[x][y] = random.nextInt(10) == 0 ? 4 : 2;
        }

        activity.ShowAnimation(x, y);

        ShowField();
    }

    /**
     * Show cell on the screen
     */
    private void ShowField() {
        activity.ShowCells(cells);

        // отображение текстовой информации
        activity.ShowScoreInfo(score);
        activity.ShowGoalInfo(goal);

        SaveScore();

        // проверяем условие победы
        if (!isContinuePlaying) {
            if (isWin()) {
                activity.ShowWinDialog();
            }
        }
    }

    private void SaveScore() {
        if (score > bestScore) {
            bestScore = score;
            try {
                SaveBestScore();
            } catch (IOException e) {
                Log.d("saveBestScore", e.getMessage());
                return;
            }

            activity.ShowBestScoreInfo(bestScore);
        }
    }

    public void Undo() {
        try {
            Pair<Integer, int[][]> pair = history.getLastHistory();
            score = pair.first;
            cells = pair.second;

            ShowField();
        } catch (Exception e) {
            activity.ShowToast(e.getMessage());
        }
    }

    //region Moving
    public boolean MoveLeft() {
        history.SaveHistory(cells, score);

        boolean result = false;
        for (int i = 0; i < 4; ++i) {
            boolean needRepeat = true;
            while (needRepeat) {
                needRepeat = false;
                for (int j = 0; j < 3; ++j) {
                    if (cells[i][j] == 0) {
                        for (int k = j + 1; k < 4; ++k) {
                            if (cells[i][k] != 0) {
                                cells[i][k - 1] = cells[i][k];
                                cells[i][k] = 0;
                                needRepeat = true;
                                result = true;
                            }
                        }
                        cells[i][3] = 0;
                    }
                }
            }

            // Слияние
            for (int j = 0; j < 3; ++j) {
                if (cells[i][j] != 0 && cells[i][j] == cells[i][j + 1]) {
                    cells[i][j] *= 2;
                    SetGoal(cells[i][j]);

                    System.arraycopy(cells[i], j + 1 + 1, cells[i], j + 1, 3 - (j + 1));

                    cells[i][3] = 0;
                    result = true;

                    ChangeScore(cells[i][j]);
                }
            }
        }

        return result;
    }

    public boolean MoveRight() {
        history.SaveHistory(cells, score);

        boolean result = false;
        for (int i = 3; i >= 0; --i) {
            boolean needRepeat = true;
            while (needRepeat) {
                needRepeat = false;
                for (int j = 3; j > 0; --j) {
                    if (cells[i][j] == 0) {
                        for (int k = j - 1; k >= 0; --k) {
                            if (cells[i][k] != 0) {
                                cells[i][k + 1] = cells[i][k];
                                cells[i][k] = 0;
                                needRepeat = true;
                                result = true;
                            }
                        }

                        cells[i][0] = 0;
                    }
                }
            }

            // Слияние
            for (int j = 3; j > 0; --j) {
                if (cells[i][j] != 0 && cells[i][j] == cells[i][j - 1]) {
                    cells[i][j] *= 2;
                    SetGoal(cells[i][j]);

                    System.arraycopy(cells[i], 0, cells[i], 1, j - 1);

                    cells[i][0] = 0;
                    result = true;

                    ChangeScore(cells[i][j]);
                }
            }
        }

        return result;
    }

    public boolean MoveTop() {
        history.SaveHistory(cells, score);

        boolean result = false;
        for (int i = 0; i < 4; ++i) {
            boolean needRepeat = true;
            while (needRepeat) {
                needRepeat = false;
                for (int j = 0; j < 3; ++j) {
                    if (cells[j][i] == 0) {
                        for (int k = j + 1; k < 4; ++k) {
                            if (cells[k][i] != 0) {
                                cells[k - 1][i] = cells[k][i];
                                cells[k][i] = 0;
                                needRepeat = true;
                                result = true;
                            }
                        }
                        cells[3][i] = 0;
                    }
                }
            }

            // Слияние
            for (int j = 0; j < 3; ++j) {
                if (cells[j][i] != 0 && cells[j][i] == cells[j + 1][i]) {
                    cells[j][i] *= 2;
                    SetGoal(cells[j][i]);

                    for (int k = j + 1; k < 3; ++k) {
                        cells[k][i] = cells[k + 1][i];
                    }

                    cells[3][i] = 0;
                    result = true;

                    ChangeScore(cells[j][i]);
                }
            }
        }

        return result;
    }

    public boolean MoveBottom() {
        history.SaveHistory(cells, score);

        boolean result = false;
        for (int i = 3; i >= 0; --i) {
            boolean needRepeat = true;
            while (needRepeat) {
                needRepeat = false;
                for (int j = 3; j > 0; --j) {
                    if (cells[j][i] == 0) {
                        for (int k = j - 1; k >= 0; --k) {
                            if (cells[k][i] != 0) {
                                cells[k + 1][i] = cells[k][i];
                                cells[k][i] = 0;
                                needRepeat = true;
                                result = true;
                            }
                        }

                        cells[0][i] = 0;
                    }
                }
            }

            // Слияние
            for (int j = 3; j > 0; --j) {
                if (cells[j][i] != 0 && cells[j][i] == cells[j - 1][i]) {
                    cells[j][i] *= 2;
                    SetGoal(cells[j][i]);

                    for (int k = j - 1; k > 0; --k) {
                        cells[k][i] = cells[k - 1][i];
                    }

                    cells[0][i] = 0;
                    result = true;

                    ChangeScore(cells[j][i]);
                }
            }
        }

        return result;
    }

    private void ChangeScore(int result) {
        score += result;
    }

    private void SetGoal(int result) {
        if (result == goal) {
            goal *= 2;
        }
    }
    //endregion

    //region Work with file
    private void SaveBestScore() throws IOException {
        try (FileOutputStream fos = activity.openFileOutput(bestScoreFileName, Context.MODE_PRIVATE)) {
            DataOutputStream writer = new DataOutputStream(fos);
            writer.writeInt(bestScore);

            writer.flush();
            writer.close();
        }
    }

    private void LoadBestScore() throws IOException {
        try (FileInputStream fis = activity.openFileInput(bestScoreFileName)) {
            DataInputStream reader = new DataInputStream(fis);
            bestScore = reader.readInt();

            reader.close();
        }
    }
    //endregion


    private boolean isWin() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (cells[i][j] == 8) { // change 2048
                    return true;
                }
            }
        }
        return false;
    }
}

// TODO: dialog, undo


