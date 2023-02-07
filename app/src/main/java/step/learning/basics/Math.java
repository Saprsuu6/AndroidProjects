package step.learning.basics;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
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

    public static void Sqr() {
        GeneralLogicSetHistory("sqr(%s)");
        RemoveExtraEqualsSign();

        currentNumber = java.lang.Math.pow(currentNumber, 2);

        String result = GeneralLogicClear(String.format(Locale.getDefault(), "%.10f", currentNumber));
        views.getTvResult().setText(result);
    }

    /**
     * Sqrt logic
     */
    public static void Sqrt() {
        if (currentNumber == 0) {
            GeneralToaster("Can not square root from zero");
            return;
        } else if (currentNumber < 0) {
            GeneralToaster("Can not square root from negative number");
            return;
        } else {
            GeneralLogicSetHistory("√/(%s)");
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
            GeneralLogicSetHistory("1/(%s)");
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

    private static void GeneralLogicSetHistory(String formatStringPattern) {
        String currentNumberClear = GeneralLogicClear(String.format(Locale.getDefault(), "%.10f", currentNumber));
        if (views.getTvHistory().getText().length() == 0) {
            views.getTvHistory().setText(String.format(Locale.getDefault(), formatStringPattern, currentNumberClear));
        } else {
            String history = views.getTvHistory().getText().toString();
            System.out.println(history);
            System.out.println(currentNumberClear);

            String result = String.format(Locale.getDefault(), formatStringPattern, views.getTvHistory().getText());
            views.getTvHistory().setText(result);
        }
    }

    //endregion

    public static void GeneralToaster(String message) {
        Toast.makeText(CalcActivity.context, message, Toast.LENGTH_SHORT).show();

        long[] vibrationPattern = {0, 200, 100, 200};
        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) CalcActivity.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            vibrator = (Vibrator) CalcActivity.context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1));
        } else {
            //vibrator.vibrate(300);
            vibrator.vibrate(vibrationPattern, -1);
        }

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
        while (result.endsWith("0") || result.endsWith(Buttons.signComa)) {
            result = result.substring(0, result.length() - 1);
            if (result.length() == 1) break;
        }

        return result;
    }
}
