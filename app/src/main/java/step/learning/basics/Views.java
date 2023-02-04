package step.learning.basics;

import android.view.View;
import android.widget.TextView;

import java.util.List;

public class Views {
    public TextView getTvHistory() {
        return tvHistory;
    }

    public TextView getTvResult() {
        return tvResult;
    }

    private TextView tvHistory;
    private TextView tvResult;

    public Views(TextView tvHistory, TextView tvResult) {
        this.tvHistory = tvHistory;
        this.tvResult = tvResult;
    }
}
