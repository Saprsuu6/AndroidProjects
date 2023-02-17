package step.learning.basics.Game2048;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import step.learning.basics.R;

public class Game2048Activity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static Animation spawnAnimation;
    private Game2048Logic logic;
    private Button newGame;
    private Button undo;
    private TextView[] textInfo;
    private TextView[][] tvCells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Game2048Activity.context = this.getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        // загрузка анимации как ресурса
        spawnAnimation = AnimationUtils.loadAnimation(this, R.anim.spawn_cell);
        spawnAnimation.reset();

        logic = new Game2048Logic(this);
        findViewById(R.id.layout_2048).setOnTouchListener(new OnSwipeListener(Game2048Activity.this) {
            @Override
            public void OnSwipeRight() {
                if (logic.MoveRight()) {
                    logic.SpawnCell();
                } else
                    Toast.makeText(Game2048Activity.this, "No Right Move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeLeft() {
                if (logic.MoveLeft()) {
                    logic.SpawnCell();
                } else
                    Toast.makeText(Game2048Activity.this, "No Left Move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeTop() {
                if (logic.MoveTop()) {
                    logic.SpawnCell();
                } else
                    Toast.makeText(Game2048Activity.this, "No Top Move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeBottom() {
                if (logic.MoveBottom()) {
                    logic.SpawnCell();
                } else
                    Toast.makeText(Game2048Activity.this, "No Bottom Move", Toast.LENGTH_SHORT).show();
            }
        });

        tvCells = FindTextViews();
        newGame = findViewById(R.id.newGame);
        undo = findViewById(R.id.undo);
        textInfo = new TextView[]{
                findViewById(R.id.score),
                findViewById(R.id.bestScore),
                findViewById(R.id.btn_infoGoal)};

        SetListeners();

        logic.SpawnCell();
        logic.SpawnCell();
    }

    private void SetListeners() {
        newGame.setOnClickListener(view -> logic.NewGame());

        undo.setOnClickListener(view -> logic.Undo());
    }

    /**
     * Code initialize 2048 buttons
     */
    @SuppressLint("DiscouragedApi")
    private TextView[][] FindTextViews() {
        TextView[][] tvCells = new TextView[4][4];

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                tvCells[i][j] = this.findViewById(   // идентификаторы ячеек "cell" + i + j
                        getResources().getIdentifier("cell" + i + j, "id", getPackageName()));
            }
        }

        return tvCells;
    }

    public void ShowToast(String message) {
        Toast.makeText(Game2048Activity.context, message, Toast.LENGTH_SHORT).show();
    }

    public void ShowScoreInfo(int score) {
        textInfo[0].setText(getString(R.string.btnScore, score));
    }

    public void ShowBestScoreInfo(int bestScore) {
        if (textInfo == null) {
            ((TextView) findViewById(R.id.bestScore)).setText(getString(R.string.btnBestScore, bestScore));
        } else {
            textInfo[1].setText(getString(R.string.btnBestScore, bestScore));
        }
    }

    public void ShowGoalInfo(int goal) {
        textInfo[2].setText(getString(R.string.btn_infoGoal, goal));
    }

    @SuppressLint("DiscouragedApi")
    public void ShowCells(int[][] cells) {
        Resources resources = this.getResources();

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                String value = cells[i][j] > 0 ? String.valueOf(cells[i][j]) : "";

                tvCells[i][j].setText(value);
                tvCells[i][j].setTextAppearance(resources.getIdentifier("Cell_" + cells[i][j], "style", getPackageName()));

                tvCells[i][j].setBackgroundTintList(resources.getColorStateList(resources.getIdentifier("game_bg_" + cells[i][j], "color", getPackageName()), getTheme()));
            }
        }
    }

    public void ShowAnimation(int x, int y) {
        tvCells[x][y].startAnimation(spawnAnimation);
    }

    public void ShowWinDialog() {
        Resources resources = this.getResources();

        new AlertDialog.Builder(this, R.style.Theme_Basics)
                .setTitle(resources.getString(R.string.dialog_Header))
                .setMessage(resources.getString(R.string.dialog_Message))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(resources.getString(R.string.dialog_continue), (dialog, whichButton)
                        -> logic.setContinuePlaying(true))
                .setNegativeButton(resources.getString(R.string.dialog_exit), (dialog, whichButton)
                        -> finish())
                .setNeutralButton(resources.getString(R.string.dialog_again), (dialog, whichButton)
                        -> logic.NewGame())
                .setCancelable(false)
                .show();
    }
}
