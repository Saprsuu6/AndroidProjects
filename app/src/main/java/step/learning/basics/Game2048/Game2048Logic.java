package step.learning.basics.Game2048;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import step.learning.basics.R;

public class Game2048Logic {
    private final int[][] cells = new int[4][4];
    private final TextView[][] tvCells;
    private final Random random = new Random();
    private int score = 0;
    private int bestScore = 0;
    private final TextView scoreView;
    private final TextView bestScoreView;

    public Game2048Logic(TextView[][] tvCells, TextView scoreView, TextView bestScoreView) {
        this.tvCells = tvCells;
        this.scoreView = scoreView;
        this.bestScoreView = bestScoreView;
    }

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
        cells[x][y] = random.nextInt(10) == 0 ? 4 : 2;

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
                tvCells[i][j].setTextAppearance(
                        resources.getIdentifier(
                                "Cell_" + cells[i][j],
                                "style",
                                Game2048Activity.context.getPackageName()
                        ));

                tvCells[i][j].setBackgroundTintList(resources.getColorStateList(resources.getIdentifier(
                                "game_bg_" + cells[i][j],
                                "color",
                                Game2048Activity.context.getPackageName()
                        ),
                        Game2048Activity.context.getTheme()));
            }
        }
        scoreView.setText(Game2048Activity.context.getString(R.string.btnScore, score));
        bestScoreView.setText(Game2048Activity.context.getString(R.string.btnBestScore, bestScore));
    }

    public boolean MoveLeft() {
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

            for (int j = 0; j < 3; ++j) {
                if (cells[i][j] != 0 && cells[i][j] == cells[i][j + 1]) {
                    cells[i][j] *= 2;

                    for (int k = j + 1; k < 3; ++k) {
                        cells[i][k] = cells[i][k + 1];
                    }

                    cells[i][3] = 0;
                    result = true;
                    score += cells[i][j];
                }
            }
        }

        if (result) ShowField();
        return result;
    }

    public boolean MoveRight() {
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

            for (int j = 3; j > 0; --j) {
                if (cells[i][j] != 0 && cells[i][j] == cells[i][j - 1]) {
                    cells[i][j] *= 2;

                    for (int k = j - 1; k > 0; --k) {
                        cells[i][k] = cells[i][k - 1];
                    }

                    cells[i][0] = 0;
                    result = true;
                    score += cells[i][j];
                }
            }
        }

        if (result) ShowField();
        return result;
    }
}


