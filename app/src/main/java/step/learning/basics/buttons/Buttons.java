package step.learning.basics.buttons;

import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

import step.learning.basics.R;
import step.learning.basics.views.Views;

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
     * Change logic +-
     */
    private void pmClick(View v) {
        positiveNumber = !positiveNumber;

        String result = !positiveNumber
                ? "-" + views.getTvResult().getText().toString()
                : views.getTvResult().getText().toString().replace("-", "");

        views.getTvResult().setText(result);
    }

    /**
     * Click logic
     */
    private void digitClick(View view, String operation) {
        if (operation == "number") {
            numbersInResult++;
        }

        if (numbersInResult <= 10 || operation == "digit") {
            if (!views.getTvResult().getText().toString().contains(",") && operation == "digit" || operation == "number") {
                String digit = ((Button) view).getText().toString();
                String result = views.getTvResult().getText().toString();
                if (result.equals("0") && operation != "digit") {
                    result = digit;
                } else {
                    result += digit;
                }
                views.getTvResult().setText(result);
            }
        }
    }
}
