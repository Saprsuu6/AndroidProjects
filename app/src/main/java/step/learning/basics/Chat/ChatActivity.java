package step.learning.basics.Chat;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import step.learning.basics.R;

public class ChatActivity extends AppCompatActivity {
    private UUID uuid = UUID.randomUUID();
    private LinearLayout chatContainer;
    private Services services;
    private String content = null;
    private ChatDAO chatDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatContainer = findViewById(R.id.chat_container);
        services = new Services(this);

        // TODO: save and read from file

        System.out.println(uuid.toString());

        new Thread(() -> {
            content = services.LoadUrl();
            ParseContent();
        }).start();
    }

    private void ParseContent() {
        // TODO: parse JSON
        try {
            chatDAO = new ChatDAO(new JSONObject(content));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        runOnUiThread(this::ShowContent);
    }

    private void ShowContent() {
        Resources resources = getResources();
        @SuppressLint("UseCompatLoadingForDrawables") android.graphics.drawable.Drawable
                ratesBgLeft = resources.getDrawable(R.drawable.currency_rate_cell_left, this.getTheme());
        @SuppressLint("UseCompatLoadingForDrawables") android.graphics.drawable.Drawable
                ratesBgRight = resources.getDrawable(R.drawable.currency_rate_cell_right, this.getTheme());

        LinearLayout.LayoutParams layoutParamsMessageWithDate = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParamsMessageWithDate.setMargins(20, 30, 20, 30);

        LinearLayout.LayoutParams layoutParamsDate = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        layoutParamsDate.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams layoutParamsMessage = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        List<UserDAO> userDAOList = chatDAO.getData();
        for (UserDAO user : userDAOList) {
            LinearLayout messageWithDate = new LinearLayout(this);

            messageWithDate.setLayoutParams(layoutParamsMessageWithDate);
            messageWithDate.setPadding(20, 5, 20, 5);
            messageWithDate.setOrientation(LinearLayout.HORIZONTAL);

            // region date
            TextView textViewDate = new TextView(this);

            textViewDate.setText(user.getMoment());
            textViewDate.setLayoutParams(layoutParamsDate);
            textViewDate.setTextSize(15);
            textViewDate.setPadding(20, 0, 0, 0);
            // endregion

            // region message
            LinearLayout message = new LinearLayout(this);

            message.setLayoutParams(layoutParamsMessage);
            message.setPadding(20, 5, 20, 5);
            message.setOrientation(LinearLayout.VERTICAL);

            if (uuid.toString() == user.getId()) {
                message.setBackground(ratesBgRight);
            } else {
                message.setBackground(ratesBgLeft);
            }

            // region author
            TextView textViewAuthor = new TextView(this);

            textViewAuthor.setText(user.getAuthor());
            textViewAuthor.setLayoutParams(layoutParams);
            // endregion

            // region txt
            TextView textViewTxt = new TextView(this);

            textViewTxt.setText(user.getTxt());
            textViewTxt.setTextColor(resources.getColorStateList(R.color.black, this.getTheme()));
            textViewTxt.setLayoutParams(layoutParams);
            // endregion

            message.addView(textViewAuthor);
            message.addView(textViewTxt);
            // endregion

            messageWithDate.addView(message);
            messageWithDate.addView(textViewDate);
            chatContainer.addView(messageWithDate);
        }
    }
}