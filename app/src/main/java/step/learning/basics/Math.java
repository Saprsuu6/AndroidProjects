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
    private static String signMinus = CalcActivity.context.getString(R.string.btnMinus);


    public static void Invert() {
//        Toast
//                .makeText(getApplicationContext.this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
//        return;

        if (currentNumber != 0) {
            currentNumber = 1 / currentNumber;

            String result = GeneralLogicClear(String.format("%.10f", currentNumber));
//            while (result.endsWith("0") || result.endsWith(".")) {
//                result = result.substring(0, result.length() - 1);
//            }

            views.getTvHistory().setText(String.format("1/(%s)=", currentNumber));
            views.getTvResult().setText(result);
        }
    }

    /**
     * Change +-
     */
    public static void pmLogic() {
        if (currentNumber != 0) {
            if (currentNumber > 0) {
                views.getTvResult().setText(signMinus + views.getTvResult().getText().toString());
            } else if (currentNumber < 0) {
                views.getTvResult().setText(
                        views.getTvResult().getText().toString().replace(signMinus, ""));
            }

            currentNumber *= -1;
        }
    }

    public static String GeneralLogicClear(String result) {
        String newResult = result;
        while (newResult.endsWith("0") || newResult.endsWith(".")) {
            newResult = newResult.substring(0, newResult.length() - 1);
        }

        return newResult;
    }
}
