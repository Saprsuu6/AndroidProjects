package step.learning.basics.CurrencyRates;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Services {
    private RatesActivity ratesActivity;

    public Services(RatesActivity ratesActivity) {
        this.ratesActivity = ratesActivity;
    }

    public String LoadUrl() {
        StringBuilder sb = new StringBuilder();
        String content = new String();

        try (InputStream inputStream = new URL("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json").openStream()) {
            int sym;
            while ((sym = inputStream.read()) != -1) {
                sb.append((char) sym);
            }

            content = new String(
                    sb.toString().getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8);
        } catch (NetworkOnMainThreadException ex) {
            Log.d("LoadUrl", "NetworkOnMainThreadException: " + ex.getMessage());
        } catch (MalformedURLException ex) {
            Log.d("LoadUrl", "MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            Log.d("LoadUrl", "IOException: " + ex.getMessage());
        } finally {
            return content;
        }
    }
}
