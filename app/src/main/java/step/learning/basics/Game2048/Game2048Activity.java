package step.learning.basics.Game2048;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

import step.learning.basics.R;

public class Game2048Activity extends AppCompatActivity {
    public static Context context;
    public static Animation spawnAnimation;
    public ArrayList<String> swipes = new ArrayList<>();
    private Game2048Logic logic;
    private Button newGame;
    private Button undo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Game2048Activity.context = this.getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        // загрузка анимации как ресурса
        spawnAnimation = AnimationUtils.loadAnimation(this, R.anim.spawn_cell);
        spawnAnimation.reset();

        logic = new Game2048Logic(FindTextViews(), new TextView[]{findViewById(R.id.score), findViewById(R.id.bestScore), findViewById(R.id.btn_infoGoal)});
        findViewById(R.id.layout_2048).setOnTouchListener(new OnSwipeListener(Game2048Activity.this) {
            @Override
            public void OnSwipeRight() {
                if (logic.MoveRight()) {
                    logic.SpawnCell(spawnAnimation);
                    swipes.add("right");
                } else
                    Toast.makeText(Game2048Activity.this, "No Right Move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeLeft() {
                if (logic.MoveLeft()) {
                    logic.SpawnCell(spawnAnimation);
                    swipes.add("left");
                } else
                    Toast.makeText(Game2048Activity.this, "No Left Move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeTop() {
                if (logic.MoveTop()) {
                    logic.SpawnCell(spawnAnimation);
                    swipes.add("top");
                } else
                    Toast.makeText(Game2048Activity.this, "No Top Move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeBottom() {
                if (logic.MoveBottom()) {
                    logic.SpawnCell(spawnAnimation);
                    swipes.add("bottom");
                } else
                    Toast.makeText(Game2048Activity.this, "No Bottom Move", Toast.LENGTH_SHORT).show();
            }
        });

        newGame = findViewById(R.id.newGame);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logic.NewGame();
            }
        });

        undo = findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logic.Undo(swipes.get(swipes.size() - 1));
            }
        });

        logic.SpawnCell(Game2048Activity.spawnAnimation);
        logic.SpawnCell(Game2048Activity.spawnAnimation);
    }

    /**
     * Code initialize 2048 buttons
     */
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
}
