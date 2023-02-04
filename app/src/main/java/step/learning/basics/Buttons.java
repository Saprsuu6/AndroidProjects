package step.learning.basics;

import android.view.View;
import android.widget.Button;

import java.util.List;

public class Buttons {
    private Views views;
    private List<View> numbers;
    private List<View> operations;
    private View plusMinus;
    private View coma;
    int numbersInResult = 0;
    boolean positiveNumber = true;

    public Buttons(List<View> numbers, List<View> operations,
                   View plusMinus, View coma, Views views) {
        Math.setViews(views);

        this.numbers = numbers;
        this.operations = operations;
        this.views = views;

        this.plusMinus = plusMinus;
        this.plusMinus.setOnClickListener(this::pmClick);

        this.coma = coma;
        this.coma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitClick(view, "digit");
            }
        });

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

        // operation TO DO
    }

    /**
     * Change +-. Logic event
     */
    private void pmClick(View v) {
        Math.pmLogic(positiveNumber = !positiveNumber);
    }

    /**
     * Click logic
     */
    private void digitClick(View view, String operation) {
        if (operation == "number") {
            numbersInResult++;
        }

        if (numbersInResult <= 10 || operation == "digit") {
            if (!views.getTvResult().getText().toString().contains(".") && operation == "digit" || operation == "number") {
                String digit = ((Button) view).getText().toString();
                String result = views.getTvResult().getText().toString();

                if (result.equals("0") && operation != "digit") {
                    result = digit;
                } else {
                    result += digit;
                }
                views.getTvResult().setText(result);

                if (operation == "number") {
                    Math.setCurrentNumber(Double.parseDouble(result));
                    System.out.println(Math.getCurrentNumber());
                }
            }
        }
    }
}
