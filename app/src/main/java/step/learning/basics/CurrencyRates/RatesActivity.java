package step.learning.basics.CurrencyRates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import step.learning.basics.R;

public class RatesActivity extends AppCompatActivity {
    private TextView exchangeRate;
    private Services services;
    private List<RateDAO> rates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        services = new Services(this);

        exchangeRate = findViewById(R.id.exchangeRate);

        new Thread(() -> {
            String content = services.LoadUrl();
            ParseContent(content);
        }).start();
    }

    private void ParseContent(String content) {
        rates = new ArrayList<>();

        try {
            JSONArray rates = new JSONArray(content);

            for (int i = 0; i < rates.length(); i++) {
                this.rates.add(new RateDAO(rates.getJSONObject(i)));
            }

            //new Thread(this::ShowRates).start();
            runOnUiThread(this::ShowRates);
        } catch (JSONException ex) {
            Log.d("LoadUrl", "JSONException: " + ex.getMessage());
        }
    }

    @SuppressLint({"UseCompatLoadingForColorStateLists", "DiscouragedApi"})
    private void ShowRates() {
        Resources resources = getResources();

        android.graphics.drawable.Drawable ratesBgLeft = AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.currency_rate_cell_left);
        android.graphics.drawable.Drawable ratesBgRight = AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.currency_rate_cell_right);

        LinearLayout container = findViewById(R.id.rates);

        LinearLayout.LayoutParams layoutParamsLeft = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParamsLeft.setMargins(15, 10, 15, 10);
        layoutParamsLeft.gravity = Gravity.START;

        LinearLayout.LayoutParams layoutParamsRight = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParamsRight.setMargins(15, 10, 15, 10);
        layoutParamsRight.gravity = Gravity.END;

        runOnUiThread(() -> exchangeRate.setText(rates.get(0).getExchangeDate()));

        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (RateDAO rate : rates) {
            sb.append(String.format(Locale.getDefault(), "%s \"%s\"\nRate: %f",
                    rate.getTxt(), rate.getCc(), rate.getRate()));

            TextView textView = new TextView(this);
            textView.setText(sb.toString());
            textView.setTextSize(20);
            textView.setElevation(20);
            textView.setTextColor(resources.getColorStateList(resources.getIdentifier("black", "color", this.getPackageName()), getTheme()));
            textView.setPadding(15, 10, 15, 10);

            if (counter % 2 == 0) {
                textView.setLayoutParams(layoutParamsLeft);
                textView.setBackground(ratesBgLeft);
            } else {
                textView.setLayoutParams(layoutParamsRight);
                textView.setBackground(ratesBgRight);
            }

            container.addView(textView);
            counter++;
            sb.setLength(0);
        }
    }

//    private void GetRatesTxt() {
//        StringBuilder sb = new StringBuilder();
//
//        runOnUiThread(() -> exchangeRate.setText(rates.get(0).getExchangeDate()));
//
//        for (RateDAO rate : rates) {
//            sb.append(String.format(Locale.getDefault(), "%s \"%s\"\nRate: %f\n\n",
//                    rate.getTxt(), rate.getCc(), rate.getRate()));
//        }
//
//        runOnUiThread(() -> tvJson.setText(sb.toString()));
//    }
}