package step.learning.basics;

import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Locale;

public class Math {
    public static void setViews(Views views) {
        Math.views = views;
    }

    public static void setCurrentNumber(double currentNumber) {
        Math.currentNumber = currentNumber;
    }

    public static double getCurrentNumber() {
        return currentNumber;
    }

    private static double currentNumber;
    private static Views views;

    public static void Invert() {
//        Toast
//                .makeText(getApplicationContext.this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
//        return;

        if (currentNumber != 0) {
            currentNumber = 1 / currentNumber;

            String result = String.format("%.10f", currentNumber);
            System.out.println(result);
            System.out.println(result.endsWith("0"));
            while(result.endsWith("0") || result.endsWith(".")) {
                result = result.substring(0, result.length() - 1);
                System.out.println(result);
            }

            views.getTvHistory().setText(String.format("1/(%s)=", currentNumber));
            views.getTvResult().setText(result);
        }
    }

    /**
     * Change +-
     */
    public static void pmLogic(boolean positiveNumber) {
        if (currentNumber != 0) {
            if (currentNumber > 0) {
                views.getTvResult().setText("-" + views.getTvResult().getText().toString());
            } else if (currentNumber < 0) {
                views.getTvResult().setText(
                        views.getTvResult().getText().toString().replace("-", ""));
            }

            currentNumber *= -1;
        }
    }
}
