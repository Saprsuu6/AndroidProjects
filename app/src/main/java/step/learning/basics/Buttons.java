package step.learning.basics;

import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Buttons {
    private Views views;
    private List<View> numbers;
    private HashMap<View, String> operations;
    private View[] servicesButtons;
    int numbersInResult = 0;
    public static String signMinus = CalcActivity.context.getString(R.string.btnMinus);
    public static String signComa = CalcActivity.context.getString(R.string.btnComa);
    private Vibrator vibrator = new Vibrator();

    public Buttons(List<View> numbers, HashMap<View, String> operations, View[] servicesButtons, Views views) {
        Math.setViews(views);

        this.numbers = numbers;
        this.operations = operations;
        this.views = views;

        this.servicesButtons = servicesButtons;

        InitializeEvents();
    }

    /**
     * Initialize all buttons events
     */
    private void InitializeEvents() {
        // region operation buttons
        for (View numbers_view : numbers) {
            numbers_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    digitClick(view, "number");
                }
            });
        }

        for (View operations_view : operations.keySet()) {
            operations_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    operationClick(view, operations.get(operations_view));
                }
            });
        }
        //endregion

        // region services buttons
        this.servicesButtons[0].setOnClickListener(this::pmClick);

        this.servicesButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitClick(view, "digit");
            }
        });

        this.servicesButtons[2].setOnClickListener(this::BackSpace);
        // endregion
    }

    private void operationClick(View v, String operation) {
        System.out.println(operation);
        try {
            switch (operation) {
                case "inverse":
                    Math.Invert();
                    break;
                case "sqrt":
                    Math.Sqrt();
                    break;
                case "square":
                    Math.Sqr();
                    break;
                case "minus":
                    Math.OperationClick("-");
                    break;
                case "plus":
                    Math.OperationClick("+");
                    break;
                case "multiply":
                    Math.OperationClick("X");
                    break;
                case "divide":
                    Math.OperationClick("÷");
                    break;
                case "equal":
                    double result = Math.Equal();
                    Show(result);
                    break;
                case "clearE":
                    ClearE();
                    break;
                case "clearAll":
                    ClearAll();
                    break;
            }
        } catch (Exception e) {
            vibrator.run();
            System.out.println(e.getMessage());
        }
    }

    private void ClearAll() {
        if (views.getTvHistory().getText() != "") {
            if (CalcActivity.needClearAll) CalcActivity.needClearAll = false;
            views.getTvHistory().setText("");

            ClearE();
        }
    }

    private void ClearE() {
        if (views.getTvResult().getText() != "0" && Math.getCurrentNumber() != 0) {
            if (CalcActivity.needClearRes) CalcActivity.needClearRes = false;
            Math.setCurrentNumber(0);
            views.getTvResult().setText("0");
        }
    }

    private void Show(double result) {
        views.getTvResult().setText(Math.GeneralLogicClear(String.format(Locale.getDefault(), "%.10f", result)));
    }

    // Return values and show method

    /**
     * BackSpace Logic
     *
     * @param v
     */
    private void BackSpace(View v) {
        String result = views.getTvResult().getText().toString();
        int len = result.length();

        if (len <= 1) {
            views.getTvResult().setText("0");
            Math.setCurrentNumber(0);
            return;
        }
        result = result.substring(0, result.length() - 1);
        if (result.equals(signMinus)) {
            result = "0";
        }

        Math.setCurrentNumber(0);
        views.getTvResult().setText(result);
    }

    /**
     * Change +-. Logic event
     */
    private void pmClick(View v) {
        try {
            Math.pmLogic();
        } catch (Exception e) {
            vibrator.run();
            System.out.println(e.getMessage());
        }
    }

    /**
     * Click logic
     */
    private void digitClick(View v, String operation) {
        if (operation == "number") {
            numbersInResult++;
        }

        if (numbersInResult <= 10 || operation == "digit") {
            if (!views.getTvResult().getText().toString().contains(signComa) && operation == "digit" || operation == "number") {
                if (CalcActivity.needClearRes) {
                    CalcActivity.needClearRes = false;
                    views.getTvResult().setText("0");
                    Math.setCurrentNumber(0);
                }

                if (CalcActivity.needClearAll) {
                    CalcActivity.needClearAll = false;
                    views.getTvHistory().setText("");
                    views.getTvResult().setText("0");
                    Math.setCurrentNumber(0);
                }

                String digit = ((Button) v).getText().toString();
                String result = views.getTvResult().getText().toString();

                if (result.equals("0") && operation != "digit" || CalcActivity.error) {
                    if (CalcActivity.error) {
                        CalcActivity.error = false;
                        views.getTvHistory().setText("");
                    }

                    result = digit;
                } else {
                    result += digit;
                }
                views.getTvResult().setText(result);

                if (operation == "number") {
                    Math.setCurrentNumber(Double.parseDouble(result));
                }
            }
        }
    }
}
