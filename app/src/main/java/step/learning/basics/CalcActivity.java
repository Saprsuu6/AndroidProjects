package step.learning.basics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalcActivity extends AppCompatActivity {
    public static Context context;
    private Views views;
    private Buttons buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalcActivity.context = super.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        views = new Views(findViewById(R.id.tvHistory), findViewById(R.id.tvResult));
        views.getTvHistory().setText("");
        views.getTvResult().setText("0");

// alternative to findViewById
//        for (int i = 0; i < 10; i++) {
//            findViewById(getResources().getIdentifier("btn" + i, "id", getPackageName());
//        }

        buttons = new Buttons(FindNumbers(), FindOperations(), findViewById(R.id.btnPlusMinus), findViewById(R.id.btnComa), views);
    }

    /**
     * Call when destroy activity. Use to save data
     *
     * @param outState to saved object
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("history", views.getTvHistory().getText());
        outState.putCharSequence("result", views.getTvResult().getText());
        Log.d("onSaveInstance", "Data saves");
    }

    /**
     * Call after create activity. Use to change data
     *
     * @param savedInstanceState daved object
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        views.getTvHistory().setText(savedInstanceState.getCharSequence("history"));
        views.getTvResult().setText(savedInstanceState.getCharSequence("result"));
    }

    private List<View> FindNumbers() {
        return Arrays.asList(findViewById(R.id.btnSeven), findViewById(R.id.btnEight), findViewById(R.id.btnNine), findViewById(R.id.btnFour), findViewById(R.id.btnFive), findViewById(R.id.btnSix), findViewById(R.id.btnOne), findViewById(R.id.btnTwo), findViewById(R.id.btnThree), findViewById(R.id.btnNull));
    }

    private HashMap<View, String> FindOperations() {
        return new HashMap<View, String>() {{
            put(findViewById(R.id.btnPercent), "percent");
            put(findViewById(R.id.btnClearE), "clearE");
            put(findViewById(R.id.btnClearAll), "clearAll");
            put(findViewById(R.id.btnBackspace), "backSpace");
            put(findViewById(R.id.btnInverse), "inverse");
            put(findViewById(R.id.btnSquare), "square");
            put(findViewById(R.id.btnSqrt), "sqrt");
            put(findViewById(R.id.btnDivide), "divide");
            put(findViewById(R.id.btnMultiply), "multiply");
            put(findViewById(R.id.btnMinus), "minus");
            put(findViewById(R.id.btnPlus), "plus");
            put(findViewById(R.id.btnIs), "is");
        }};
    }
}