package step.learning.basics.Game2048;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import step.learning.basics.Calculator.Buttons;
import step.learning.basics.Calculator.CalcActivity;
import step.learning.basics.Calculator.Views;
import step.learning.basics.R;

public class Game2048Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //CalcActivity.context = super.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        findViewById(R.id.layout_2048).setOnTouchListener(new OnSwipeListener(Game2048Activity.this) {
            @Override
            public void OnSwipeRight() {
                Toast.makeText(Game2048Activity.this, "Right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSwipeLeft() {
                Toast.makeText(Game2048Activity.this, "Left", Toast.LENGTH_SHORT).show();
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
    }
}
