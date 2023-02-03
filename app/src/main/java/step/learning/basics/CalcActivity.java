package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalcActivity extends AppCompatActivity {
    private TextView tvHistory;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        tvHistory = findViewById(R.id.tvHistory);
        tvResult = findViewById(R.id.tvResult);
        tvHistory.setText("");
        tvResult.setText("0");

        findViewById(R.id.btnSeven).setOnClickListener(this::digitClick);
    }

    private void pmClick(View v) {

    }

    private void digitClick(View v) {
        // задача: ограничить величиной в 10 цифр
        String digit = ((Button) v).getText().toString();
        String result = tvResult.getText().toString();
        if (result.equals("0")) {
            result = digit;
        } else {
            result += digit;
        }
        tvResult.setText(result);
    }
}