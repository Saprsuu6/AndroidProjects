package step.learning.basics.Game2048;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import step.learning.basics.Calculator.CalcActivity;
import step.learning.basics.R;

public class Game2048Activity extends AppCompatActivity {
    public static Context context;
    private Animation spawnAnimation;
    private Game2048Logic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Game2048Activity.context = this.getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        // загрузка анимации как ресурса
        spawnAnimation = AnimationUtils.loadAnimation(this, R.anim.spawn_cell);
        spawnAnimation.reset();

        logic = new Game2048Logic(FindTextViews());
        findViewById(R.id.layout_2048).setOnTouchListener(new OnSwipeListener(Game2048Activity.this) {
            @Override
            public void OnSwipeRight() {
                if (logic.MoveRight()) logic.SpawnCell(spawnAnimation);
                else
                    Toast.makeText(Game2048Activity.this, "No Right Move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeLeft() {
                if (logic.MoveLeft()) logic.SpawnCell(spawnAnimation);
                else
                    Toast.makeText(Game2048Activity.this, "No Left Move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeTop() {
                Toast.makeText(Game2048Activity.this, "Top", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeBottom() {
                Toast.makeText(Game2048Activity.this, "Bottom", Toast.LENGTH_SHORT).show();
            }
        });

        logic.SpawnCell(spawnAnimation);
    }

    /**
     * Code initialize 2048 buttons
     */
    private TextView[][] FindTextViews() {
        TextView[][] tvCells = new TextView[4][4];

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                tvCells[i][j] = this.findViewById(   // идентификаторы ячеек "cell" + i + j
                        getResources().getIdentifier(
                                "cell" + i + j,
                                "id",
                                getPackageName()
                        )
                );
            }
        }

        return tvCells;
    }
}
