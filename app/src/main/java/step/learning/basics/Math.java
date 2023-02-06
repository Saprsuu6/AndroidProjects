package step.learning.basics;

import android.widget.Button;
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

    /**
     * Invert logic
     */
    public static void Invert() {
        if (currentNumber == 0) {
            Toast.makeText(CalcActivity.context, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String currentNumberClear = GeneralLogicClear(String.format("%.10f", currentNumber));
            views.getTvHistory().setText(String.format("1/(%s)=", currentNumberClear));
            currentNumber = 1 / currentNumber;

            String result = GeneralLogicClear(String.format("%.10f", currentNumber));

            views.getTvResult().setText(result);
        }
    }

    /**
     * Change +-
     */
    public static void pmLogic() {
        if (currentNumber == 0) {
            Toast.makeText(CalcActivity.context, "Cannot be negative zero", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (currentNumber > 0) {
                views.getTvResult().setText(Buttons.signMinus + views.getTvResult().getText().toString());
            } else if (currentNumber < 0) {
                views.getTvResult().setText(
                        views.getTvResult().getText().toString().replace(Buttons.signMinus, ""));
            }

            currentNumber *= -1;
        }
    }

    public static String GeneralLogicClear(String result) {
        String newResult = result;
        while (newResult.endsWith("0") || newResult.endsWith(Buttons.signComa)) {
            newResult = newResult.substring(0, newResult.length() - 1);
        }

        return newResult;
    }
}
