package step.learning.basics.Chat;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import step.learning.basics.R;

public class ChatActivity extends AppCompatActivity {
    private List<MessageDAO> messageDAOList;
    private Resources resources;
    private UUID uuid;
    private Services services;
    private String content = null;
    private ChatDAO chatDAO;
    private MessageDAO messageDAO;
    private LinearLayout chatContainer;
    private ImageButton send;
    private TextView author;
    private EditText sendMessage;
    private ScrollView scrollView;
    private android.graphics.drawable.Drawable ratesBgLeft;
    private android.graphics.drawable.Drawable ratesBgRight;
    private android.graphics.drawable.Drawable ratesBgLeftReply;
    private android.graphics.drawable.Drawable ratesBgRightReply;
    private LinearLayout.LayoutParams layoutParamsContainer;
    private LinearLayout.LayoutParams layoutParamsVerticalAdaptive;
    private LinearLayout.LayoutParams layoutParamsHorizontalAdaptive;
    private LinearLayout.LayoutParams layoutParamsWrapWithMargins;
    @SuppressLint("SimpleDateFormat")
    private final DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    @SuppressLint("SimpleDateFormat")
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        uuid = UUID.randomUUID();
        resources = getResources();

        addResources();

        FindViews();
        services = new Services(this);
        SetListeners();

        new Thread(() -> {
            content = services.LoadUrl();
            ParseContent();
        }).start();
    }

    private void FindViews() {
        scrollView = findViewById(R.id.scroll);
        author = findViewById(R.id.user_name);
        sendMessage = findViewById(R.id.edit_txt);
        chatContainer = findViewById(R.id.chat_container);
        send = findViewById(R.id.send);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addResources() {
        Resources resources = getResources();
        this.ratesBgLeft = resources.getDrawable(R.drawable.message_cell_left, this.getTheme());
        this.ratesBgRight = resources.getDrawable(R.drawable.message_cell_right, this.getTheme());
        this.ratesBgLeftReply = resources.getDrawable(R.drawable.message_cell_left_reply, this.getTheme());
        this.ratesBgRightReply = resources.getDrawable(R.drawable.message_cell_right_reply, this.getTheme());

        layoutParamsContainer = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParamsVerticalAdaptive = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParamsHorizontalAdaptive = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParamsHorizontalAdaptive.gravity = Gravity.CENTER;

        layoutParamsWrapWithMargins = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParamsWrapWithMargins.setMargins(10, 20, 10, 20);
    }

    private void SetListeners() {
        send.setOnClickListener(this::SendButtonClick);
        sendMessage.setOnKeyListener(new View.OnKeyListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (sendMessage.getText().toString().trim().length() > 0) {
                    send.setEnabled(true);
                    send.setBackground(resources.getDrawable(R.drawable.can_send, getTheme()));
                } else {
                    send.setEnabled(false);
                    send.setBackground(resources.getDrawable(R.drawable.cannot_send, getTheme()));
                }
                return true;
            }
        });
    }

    private void SendButtonClick(View view) {
        if (sendMessage.getText().toString().trim().length() != 0) {
            messageDAO = new MessageDAO();

            messageDAO.setAuthor(author.getText().toString());
            messageDAO.setTxt(sendMessage.getText().toString());
//            userDAO.setIdReply(UUID.fromString("13486278-b21f-11ed-96d1-f23c93f195e6"));
//            userDAO.setReplyPreview("шо вы...");

            new Thread(this::postChatMessage).start();
        }
    }

    private void postChatMessage() {
        services.postChatMessage(messageDAO);
        AddContainerWithMsg(messageDAO, messageDAOList);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        sendMessage.setText("");
    }

    private void ParseContent() {
        try {
            chatDAO = new ChatDAO(new JSONObject(content));
            messageDAOList = chatDAO.getData();
            Collections.reverse(messageDAOList);
        } catch (JSONException e) {
            Log.d("DataParse", e.getMessage());
        }
        runOnUiThread(this::ShowContent);
    }

    private void ShowContent() {
        for (MessageDAO user : messageDAOList) {
            AddContainerWithMsg(user, messageDAOList);
        }

        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    private void AddContainerWithMsg(MessageDAO user, List<MessageDAO> messageDAOList) {
        LinearLayout messageContainer = new LinearLayout(this);

        messageContainer.setLayoutParams(layoutParamsContainer);
        messageContainer.setOrientation(LinearLayout.HORIZONTAL);

        // region message with date
        LinearLayout messageWithDate = new LinearLayout(this);
        messageWithDate.setLayoutParams(layoutParamsWrapWithMargins);
        messageWithDate.setPadding(20, 5, 20, 5);
        messageWithDate.setOrientation(LinearLayout.HORIZONTAL);

        // region date
        TextView textViewDate = new TextView(this);
        textViewDate.setText(dateFormat.format(user.getMoment()));
        textViewDate.setLayoutParams(layoutParamsHorizontalAdaptive);
        textViewDate.setPadding(30, 0, 30, 0);
        // endregion

        // region time
        TextView textViewTime = new TextView(this);
        textViewTime.setText(timeFormat.format(user.getMoment()));
        textViewDate.setLayoutParams(layoutParamsHorizontalAdaptive);
        // endregion

        // region message
        LinearLayout message = new LinearLayout(this);
        message.setLayoutParams(layoutParamsHorizontalAdaptive);
        message.setPadding(20, 5, 20, 5);
        message.setOrientation(LinearLayout.VERTICAL);

        // region author
        TextView textViewAuthor = new TextView(this);
        textViewAuthor.setText(user.getAuthor());
        textViewAuthor.setLayoutParams(layoutParamsVerticalAdaptive);
        // endregion

        // region txt
        TextView textViewTxt = new TextView(this);
        textViewTxt.setText(user.getTxt());
        textViewTxt.setTextColor(resources.getColorStateList(R.color.black, this.getTheme()));
        textViewTxt.setLayoutParams(layoutParamsVerticalAdaptive);
        // endregion

        message.addView(textViewAuthor);
        message.addView(textViewTxt);
        // endregion

        // region if reply
        LinearLayout messageReply = null;
        if (user.getIdReply() != null && user.getReplyPreview() != null) {
            MessageDAO replayedMsg = FindReplayedMsg(user.getIdReply(), messageDAOList);
            messageReply = new LinearLayout(this);

            messageReply.setLayoutParams(layoutParamsHorizontalAdaptive);
            messageReply.setPadding(20, 10, 20, 10);
            messageReply.setOrientation(LinearLayout.VERTICAL);

            // region message
            LinearLayout messageReplayed = new LinearLayout(this);
            messageReplayed.setPadding(20, 5, 20, 5);
            messageReplayed.setLayoutParams(layoutParamsWrapWithMargins);
            messageReplayed.setOrientation(LinearLayout.VERTICAL);

            // region author
            TextView textViewAuthorReplayed = new TextView(this);
            textViewAuthorReplayed.setLayoutParams(layoutParamsVerticalAdaptive);
            // endregion

            // region replayedMsg
            TextView textViewTxtReplayed = new TextView(this);
            textViewTxtReplayed.setTextColor(resources.getColorStateList(R.color.black, this.getTheme()));
            textViewTxtReplayed.setLayoutParams(layoutParamsVerticalAdaptive);
            // endregion

            messageReplayed.addView(textViewAuthorReplayed);
            messageReplayed.addView(textViewTxtReplayed);
            // endregion

            message.setLayoutParams(layoutParamsVerticalAdaptive);
            messageReply.addView(messageReplayed);
            messageReply.addView(message);
            messageReply.addView(textViewTime);

            if (replayedMsg == null) {
                textViewAuthorReplayed.setText("some author");
                textViewTxtReplayed.setText("so old msg");
            } else {
                textViewTxtReplayed.setText(replayedMsg.getTxt());
                textViewAuthorReplayed.setText(replayedMsg.getTxt());
            }

            // region setters
            if (Objects.equals(user.getAuthor(), author.getText())) {
                message.setBackground(ratesBgRight);
                messageReply.setBackground(ratesBgRightReply);
                messageWithDate.addView(textViewDate);
                messageWithDate.addView(messageReply);
                messageContainer.setGravity(Gravity.RIGHT);
                textViewTime.setGravity(Gravity.LEFT);
            } else {
                message.setBackground(ratesBgLeft);
                messageReply.setBackground(ratesBgLeftReply);
                messageWithDate.addView(messageReply);
                messageWithDate.addView(textViewDate);
                messageContainer.setGravity(Gravity.LEFT);
                textViewTime.setGravity(Gravity.RIGHT);
            }
            // endregion
        } else {
            message.addView(textViewTime);

            // region setters
            if (Objects.equals(user.getAuthor(), author.getText())) {
                message.setBackground(ratesBgRight);
                messageWithDate.addView(textViewDate);
                messageWithDate.addView(message);
                messageContainer.setGravity(Gravity.RIGHT);
                textViewTime.setGravity(Gravity.LEFT);
            } else {
                message.setBackground(ratesBgLeft);
                messageWithDate.addView(message);
                messageWithDate.addView(textViewDate);
                messageContainer.setGravity(Gravity.LEFT);
                textViewTime.setGravity(Gravity.RIGHT);
            }
            // endregion
        }
        // endregion

        // endregion

        messageContainer.addView(messageWithDate);
        runOnUiThread(() -> chatContainer.addView(messageContainer));
    }

    private MessageDAO FindReplayedMsg(UUID uuid, List<MessageDAO> messageDAOList) {
        for (MessageDAO msg : messageDAOList) {
            if (msg.getId().equals(uuid)) {
                return msg;
            }
        }

        return null;
    }
}