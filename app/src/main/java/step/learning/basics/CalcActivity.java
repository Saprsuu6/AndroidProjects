package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.Arrays;
import java.util.List;

public class CalcActivity extends AppCompatActivity {
    private Views views;
    private Buttons buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        views = new Views(findViewById(R.id.tvHistory), findViewById(R.id.tvResult));
        views.getTvHistory().setText("");
        views.getTvResult().setText("0");

        buttons = new Buttons(FindNumbers(), FindOperations(),
                findViewById(R.id.btnPlusMinus), findViewById(R.id.btnComa), views);
    }

    private List<View> FindNumbers() {
        return Arrays.asList(
                findViewById(R.id.btnSeven),
                findViewById(R.id.btnEight),
                findViewById(R.id.btnNine),
                findViewById(R.id.btnFour),
                findViewById(R.id.btnFive),
                findViewById(R.id.btnSix),
                findViewById(R.id.btnOne),
                findViewById(R.id.btnTwo),
                findViewById(R.id.btnThree),
                findViewById(R.id.btnNull));
    }

    private List<View> FindOperations() {
        return Arrays.asList(
                findViewById(R.id.btnPercent),
                findViewById(R.id.btnClearE),
                findViewById(R.id.btnClearAll),
                findViewById(R.id.btnBackspace),
                findViewById(R.id.btnInverse),
                findViewById(R.id.btnSquare),
                findViewById(R.id.btnSqrt),
                findViewById(R.id.btnDivide),
                findViewById(R.id.btnMultiply),
                findViewById(R.id.btnMinus),
                findViewById(R.id.btnPlus),
                findViewById(R.id.btnIs));
    }
}