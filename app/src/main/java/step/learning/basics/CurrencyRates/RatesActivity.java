package step.learning.basics.CurrencyRates;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import step.learning.basics.R;

public class RatesActivity extends AppCompatActivity {
    private TextView tvJson;
    private Services services;
    private List<RateDAO> rates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        services = new Services(this);

        tvJson = findViewById(R.id.json);
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

            new Thread(this::ShowRates).start();
        } catch (JSONException ex) {
            Log.d("LoadUrl", "JSONException: " + ex.getMessage());
        }
    }

    private void ShowRates() {
        StringBuilder sb = new StringBuilder();

        for (RateDAO rate : rates) {
            sb.append(String.format(Locale.getDefault(), "%s \"%s\"\nRate: %f\nExchange Date: %s\n\n",
                    rate.getTxt(), rate.getCc(), rate.getRate(), rate.getExchangeDate()));
        }

        runOnUiThread(() -> tvJson.setText(sb.toString()));
    }
}