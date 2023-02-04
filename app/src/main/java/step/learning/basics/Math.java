package step.learning.basics;

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
     * Change +-
     */
    public static void pmLogic(boolean positiveNumber) {
        System.out.println(currentNumber);

        if (currentNumber != 0) {
            if (currentNumber > 0) {
                views.getTvResult().setText("-" + views.getTvResult().getText().toString());
            } else if (currentNumber < 0) {
                views.getTvResult().setText(
                        views.getTvResult().getText().toString().replace("-", ""));
            }

            currentNumber *= -1;
        }

        System.out.println(currentNumber);
    }
}
