package step.learning.basics;

import android.widget.Toast;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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

    //region Operations with one parameter

    public static void Sqrt() {
        if (currentNumber == 0) {
            GeneralToaster("Can not square root from zero");
            return;
        } else if (currentNumber < 0) {
            GeneralToaster("Can not square root from negative number");
            return;
        } else {
            String currentNumberClear = GeneralLogicClear(String.format(Locale.getDefault(), "%.10f", currentNumber));
            if (views.getTvHistory().getText().length() == 0) {
                views.getTvHistory().setText(String.format(Locale.getDefault(), "√/(%s)=", currentNumberClear));
            } else {
                String result = String.format(Locale.getDefault(), "√/(%s)", views.getTvHistory().getText());
                views.getTvHistory().setText(result);
            }
            RemoveExtraEqualsSign();

            currentNumber = java.lang.Math.sqrt(currentNumber);

            String result = GeneralLogicClear(String.format(Locale.getDefault(), "%.10f", currentNumber));
            views.getTvResult().setText(result);
        }
    }

    /**
     * Invert logic
     */
    public static void Invert() {
        if (currentNumber == 0) {
            GeneralToaster("Cannot divide by zero");
            return;
        } else {
            String currentNumberClear = GeneralLogicClear(String.format(Locale.getDefault(), "%.10f", currentNumber));
            if (views.getTvHistory().getText().length() == 0) {
                views.getTvHistory().setText(String.format(Locale.getDefault(), "1/(%s)", currentNumberClear));
            } else {
                String result = String.format(Locale.getDefault(), "1/(%s)", views.getTvHistory().getText());
                views.getTvHistory().setText(result);
            }
            RemoveExtraEqualsSign();

            currentNumber = 1 / currentNumber;

            String result = GeneralLogicClear(String.format(Locale.getDefault(), "%.10f", currentNumber));
            views.getTvResult().setText(result);
        }
    }

    /**
     * Change +-
     */
    public static void pmLogic() {
        if (currentNumber == 0) {
            GeneralToaster("Cannot be negative zero");
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

    //endregion

    public static void GeneralToaster(String message) {
        Toast.makeText(CalcActivity.context, message, Toast.LENGTH_SHORT).show();

        views.getTvHistory().setText(message);
        views.getTvResult().setText("Input error");

        CalcActivity.error = true;
    }

    public static void RemoveExtraEqualsSign() {
        if (views.getTvHistory().getText().toString().contains("=")) {
            views.getTvHistory().setText(views.getTvHistory().getText().toString().replace("=", ""));
        }
        views.getTvHistory().append("=");
    }

    public static String GeneralLogicClear(String result) {
        String newResult = result;
        while (newResult.endsWith("0") || newResult.endsWith(Buttons.signComa)) {
            newResult = newResult.substring(0, newResult.length() - 1);
        }

        return newResult;
    }
}
