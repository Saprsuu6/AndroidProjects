package step.learning.basics;

import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;

public class Buttons {
    private Views views;
    private List<View> numbers;
    private HashMap<View, String> operations;
    private View plusMinus;
    private View coma;
    int numbersInResult = 0;
    public static String signMinus = CalcActivity.context.getString(R.string.btnMinus);
    public static String signComa = CalcActivity.context.getString(R.string.btnComa);

    public Buttons(List<View> numbers, HashMap<View, String> operations, View plusMinus, View coma, Views views) {
        Math.setViews(views);

        this.numbers = numbers;
        this.operations = operations;
        this.views = views;

        this.plusMinus = plusMinus;
        this.coma = coma;

        InitializeEvents();
    }

    /**
     * Initialize all buttons events
     */
    private void InitializeEvents() {
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

        this.coma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitClick(view, "digit");
            }
        });

        this.plusMinus.setOnClickListener(this::pmClick);
    }

    private void operationClick(View v, String operation) {
        System.out.println(operation);
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
        }
    }

    /**
     * Change +-. Logic event
     */
    private void pmClick(View v) {
        Math.pmLogic();
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
