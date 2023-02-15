package step.learning.basics.Game2048;

import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;
import android.view.animation.Animation;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import step.learning.basics.R;

public class Game2048Logic {
    private int[][] cells = new int[4][4];
    private final TextView[][] tvCells;
    private final Random random = new Random();
    private int score;
    private int bestScore = 0;
    private int firstNum; // первое отображаемое число
    private int goal;
    private Game2048History history = new Game2048History();
    private final TextView[] textInfo; // обекты текстовой информации
    private final String bestScoreFileName = "best_score.txt";
    private boolean isContinuePlaying = false;  // продолжение игры после набора 2048

    public Game2048Logic(TextView[][] tvCells, TextView[] textInfo) {
        this.tvCells = tvCells;
        this.textInfo = textInfo;

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
        textInfo[1].setText(Game2048Activity.context.getString(R.string.btnBestScore, bestScore));

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

        SpawnCell(Game2048Activity.spawnAnimation);
        SpawnCell(Game2048Activity.spawnAnimation);
    }
    // endregion

    /**
     * Spawn random cell
     *
     * @param animation to play creating animation
     * @return creation status
     */
    public boolean SpawnCell(Animation animation) {
        List<Integer> freeCells = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (cells[i][j] == 0) {
                    freeCells.add(i * 10 + j);   // сохраняем координаты свободной ячейки одним числом
                }
            }
        }

        int cnt = freeCells.size();
        if (cnt == 0) return false;

        int rnd = random.nextInt(cnt);

        int x = freeCells.get(rnd) / 10;
        int y = freeCells.get(rnd) % 10;

        if (firstNum != 0) {
            cells[x][y] = firstNum;
            firstNum = 0;
        } else {
            cells[x][y] = random.nextInt(10) == 0 ? 4 : 2;
        }

        tvCells[x][y].startAnimation(animation);

        ShowField();
        return true;
    }

    /**
     * Show cell on the screen
     */
    private void ShowField() {
        Resources resources = Game2048Activity.context.getResources();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                String value = cells[i][j] > 0 ? String.valueOf(cells[i][j]) : "";

                tvCells[i][j].setText(String.valueOf(value));
                tvCells[i][j].setTextAppearance(resources.getIdentifier("Cell_" + cells[i][j], "style", Game2048Activity.context.getPackageName()));

                tvCells[i][j].setBackgroundTintList(resources.getColorStateList(resources.getIdentifier("game_bg_" + cells[i][j], "color", Game2048Activity.context.getPackageName()), Game2048Activity.context.getTheme()));
            }
        }

        // отображение текстовой информации
        textInfo[0].setText(Game2048Activity.context.getString(R.string.btnScore, score));
        textInfo[2].setText(Game2048Activity.context.getString(R.string.btn_infoGoal, goal));

        SaveScore();

        // проверяем условие победы
//        if (!isContinuePlaying) {
//            if (isWin()) {
//                ShowWinDialog();
//            }
//        }
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
            textInfo[1].setText(Game2048Activity.context.getString(R.string.btnBestScore, bestScore));
        }
    }

    //region Undo
    public void Undo() {
        try {
            Pair<Integer, int[][]> pair = history.getLastHistory();
            score = pair.first;
            cells = pair.second;

            ShowField();
        } catch (Exception e) {
            Game2048Activity.ShowToast(e.getMessage());
        }
    }
    //endregion

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

                    for (int k = j + 1; k < 3; ++k) {
                        cells[i][k] = cells[i][k + 1];
                    }

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

                    for (int k = j - 1; k > 0; --k) {
                        cells[i][k] = cells[i][k - 1];
                    }

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
        try (FileOutputStream fos = Game2048Activity.context.openFileOutput(bestScoreFileName, Game2048Activity.context.MODE_PRIVATE)) {
            DataOutputStream writer = new DataOutputStream(fos);
            writer.writeInt(bestScore);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw e;
        }
    }

    private void LoadBestScore() throws IOException {
        try (FileInputStream fis = Game2048Activity.context.openFileInput(bestScoreFileName)) {
            DataInputStream reader = new DataInputStream(fis);
            bestScore = reader.readInt();

            reader.close();
        } catch (IOException e) {
            throw e;
        }
    }
    //endregion

    //region Game finish
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

    private void ShowWinDialog() {
        System.out.println("TODO dialog");

//        new AlertDialog.Builder(Game2048Activity.context, R.style.Theme_Basics)
//                .setTitle("Победа!")
//                .setMessage("Вы собрали 2048")
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        isContinuePlaying = true;
//                    }
//                })
//                .setNegativeButton("Выйти", (dialog, whichButton) -> {
//                    Toast.makeText(Game2048Activity.context, "Скоро доделаем", Toast.LENGTH_SHORT).show();
//                })
//                .setNeutralButton("Заново", (dialog, whichButton) -> {
//                    Toast.makeText(Game2048Activity.context, "Скоро доделаем", Toast.LENGTH_SHORT).show();
//                })
//                .setCancelable(false)  // модальный вариант - не отменяется, нужен выбор
//                .show();
    }
    //endregion
}

// TODO: dialog, undo


