package step.learning.basics;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.View;
import android.widget.Button;
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

    public static String getOperation() {
        return operation;
    }

    public static double getCurrentNumber() {
        return currentNumber;
    }

    private static double currentNumber;
    private static Views views;
    private static double argument1;
    private static String operation;

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
    public static void Sqrt() throws Exception {
        if (currentNumber == 0) {
            GeneralToaster("Can not square root from zero");
            throw new Exception("Can not square root from zero");
        } else if (currentNumber < 0) {
            GeneralToaster("Can not square root from negative number");
            throw new Exception("Can not square root from negative number");
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
    public static void Invert() throws Exception {
        if (currentNumber == 0) {
            GeneralToaster("Cannot divide by zero");
            throw new Exception("Cannot divide by zero");
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
    public static void pmLogic() throws Exception {
        if (currentNumber == 0) {
            GeneralToaster("Cannot be negative zero");
            throw new Exception("Cannot be negative zero");
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
            String history = views.getTvHistory().getText().toString().replaceAll("\\d+", currentNumberClear);
            String result = String.format(Locale.getDefault(), formatStringPattern, history);
            views.getTvHistory().setText(result);
        }
    }

    //endregion

    // region Operation with two parameters
    public static void OperationClick(String operation) {
        Math.operation = operation;
        String result = views.getTvResult().getText().toString();
        argument1 = GetArgument(result);
        views.getTvHistory().setText(result + " " + operation);
        CalcActivity.needClearRes = true;
    }
    // endregion

    private static double GetArgument(String resultText) {
        return Double.parseDouble(resultText.replace(Buttons.signMinus, "-"));
    }

    public static double Equal() throws Exception {
        if (!views.getTvHistory().getText().toString().contains("0 =")) {
            views.getTvHistory().append(" " + GeneralLogicClear(String.format(Locale.getDefault(), "%.10f", currentNumber)) + " =");
        }
        CalcActivity.needClearAll = true;

        switch (operation) {
            case "+":
                return argument1 + currentNumber;
            case "-":
                return argument1 - currentNumber;
            case "X":
                return argument1 * currentNumber;
            case "÷":
                if (currentNumber == 0) {
                    GeneralToaster("Can not square root from zero");
                    throw new Exception("Can not square root from zero");
                }
                return argument1 / currentNumber;
            default:
                return 0;
        }
    }

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
        while (result.endsWith("0") || result.endsWith(Buttons.signComa)) {
            result = result.substring(0, result.length() - 1);
            if (!result.contains(Buttons.signComa)) break;
            if (result.length() == 1) break;
        }

        return result;
    }
}
