package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import step.learning.basics.Calculator.CalcActivity;
import step.learning.basics.Chat.ChatActivity;
import step.learning.basics.CurrencyRates.RatesActivity;
import step.learning.basics.Game2048.Game2048Activity;

public class MainActivity extends AppCompatActivity {
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.context = this.getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = findViewById(R.id.exitButton);
        addButton.setOnClickListener(this::exitButtonClick);

        findViewById(R.id.calcButton).setOnClickListener(this::calcButtonClick);
        findViewById(R.id.Button2048).setOnClickListener(this::Button2048Click);
        findViewById(R.id.ButtonRates).setOnClickListener(this::RatesButtonClick);
        findViewById(R.id.Chat).setOnClickListener(this::ChatButtonClick);
    }

    private void ChatButtonClick(View view) {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        startActivity(chatIntent);
    }

    private void RatesButtonClick(View v) {
        Intent ratesIntent = new Intent(this, RatesActivity.class);
        startActivity(ratesIntent);
    }

    private void calcButtonClick(View v) {
        Intent calcIntent = new Intent(this, CalcActivity.class);
        startActivity(calcIntent);
    }

    private void Button2048Click(View v) {
        Intent game2048Intent = new Intent(this, Game2048Activity.class);
        startActivity(game2048Intent);
    }

    public void exitButtonClick(View v) {
        finish();
    }
}