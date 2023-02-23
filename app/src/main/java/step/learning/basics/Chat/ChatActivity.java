package step.learning.basics.Chat;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import step.learning.basics.R;

public class ChatActivity extends AppCompatActivity {
    private String replayedTxt = null;
    private String replayedID = null;
    private String content = null;
    private Resources resources;
    private InputMethodManager imm;
    private Timer timer;
    private UUID uuid;
    private Services services;
    private ChatDAO chatDAO;
    private MessageDAO messageDAO;
    private LinearLayout chatContainer;
    private ImageButton send;
    private TextView author;
    private EditText sendMessage;
    private ScrollView scrollView;
    private List<MessageDAO> messageDAOList;
    private final android.graphics.drawable.Drawable[] drawables = new Drawable[4];
    private final LinearLayout.LayoutParams[] params = new LinearLayout.LayoutParams[4];
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
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        timer = new Timer();
        services = new Services(this);

        addResources();
        FindViews();
        SetListeners();

        LoadStory();
        startAlarm();
    }

    private void startAlarm() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LoadStory();
                runOnUiThread(() -> chatContainer.removeAllViews());
            }
        }, 5L, 5000L);
    }

    private void LoadStory() {
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
        drawables[0] = resources.getDrawable(R.drawable.message_cell_left, this.getTheme());
        drawables[1] = resources.getDrawable(R.drawable.message_cell_right, this.getTheme());
        drawables[2] = resources.getDrawable(R.drawable.message_cell_left_reply, this.getTheme());
        drawables[3] = resources.getDrawable(R.drawable.message_cell_right_reply, this.getTheme());

        params[0] = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params[1] = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params[2] = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params[2].gravity = Gravity.CENTER;

        params[3] = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params[3].setMargins(10, 20, 10, 20);
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    private void SetListeners() {
        send.setOnClickListener(this::SendButtonClick);
        sendMessage.setOnKeyListener((v, keyCode, event) -> {
            if (sendMessage.getText().toString().trim().length() > 0) {
                send.setEnabled(true);
                send.setBackground(resources.getDrawable(R.drawable.can_send, getTheme()));
            } else {
                send.setEnabled(false);
                send.setBackground(resources.getDrawable(R.drawable.cannot_send, getTheme()));
            }
            return true;
        });

        sendMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (replayedID != null) replayedID = null;
                if (replayedTxt != null) replayedTxt = null;
            }
        });

        findViewById(R.id.chat_layout).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendMessage.clearFocus();
                imm.hideSoftInputFromWindow(sendMessage.getWindowToken(), 0);
            }
            return true;
        });
    }

    private void SendButtonClick(View view) {
        if (sendMessage.getText().toString().trim().length() != 0) {
            messageDAO = new MessageDAO();

            messageDAO.setAuthor(author.getText().toString());
            messageDAO.setTxt(sendMessage.getText().toString().trim());

            if (replayedID != null) {
                messageDAO.setIdReply(UUID.fromString(replayedID));
                messageDAO.setReplyPreview(replayedTxt + "...");
            }

            new Thread(this::postChatMessage).start();
        } else {
            Toast.makeText(this, "Empty message", Toast.LENGTH_SHORT).show();
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
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded", "ClickableViewAccessibility"})
    private void AddContainerWithMsg(MessageDAO user, List<MessageDAO> messageDAOList) {
        LinearLayout messageContainer = new LinearLayout(this);

        messageContainer.setLayoutParams(params[0]);
        messageContainer.setOrientation(LinearLayout.HORIZONTAL);

        // region message with date
        LinearLayout messageWithDate = new LinearLayout(this);
        messageWithDate.setLayoutParams(params[3]);
        messageWithDate.setPadding(20, 5, 20, 5);
        messageWithDate.setOrientation(LinearLayout.HORIZONTAL);

        // region date
        TextView textViewDate = new TextView(this);
        textViewDate.setText(dateFormat.format(user.getMoment()));
        textViewDate.setLayoutParams(params[2]);
        textViewDate.setPadding(30, 0, 30, 0);
        // endregion

        // region time
        TextView textViewTime = new TextView(this);
        textViewTime.setText(timeFormat.format(user.getMoment()));
        textViewDate.setLayoutParams(params[2]);
        // endregion

        // region message
        LinearLayout message = new LinearLayout(this);
        message.setLayoutParams(params[2]);
        message.setPadding(20, 5, 20, 5);
        message.setOrientation(LinearLayout.VERTICAL);

        // region replaced id
        TextView textViewReplacedId = new TextView(this);
        textViewReplacedId.setText(user.getId().toString());
        textViewReplacedId.setVisibility(View.GONE);
        // endregion

        // region replaced txt
        TextView textViewReplacedTxt = new TextView(this);
        textViewReplacedTxt.setText(user.getTxt().toString());
        textViewReplacedTxt.setVisibility(View.GONE);
        // endregion

        // region author
        TextView textViewAuthor = new TextView(this);
        textViewAuthor.setText(user.getAuthor());
        textViewAuthor.setLayoutParams(params[1]);
        // endregion

        // region txt
        TextView textViewTxt = new TextView(this);
        textViewTxt.setText(user.getTxt());
        textViewTxt.setTextColor(resources.getColorStateList(R.color.black, this.getTheme()));
        textViewTxt.setLayoutParams(params[1]);
        // endregion

        message.addView(textViewAuthor);
        message.addView(textViewTxt);
        // endregion

        TextView reply = new TextView(this);
        reply.setText("Reply");
        reply.setLayoutParams(params[2]);
        reply.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    reply.setTextColor(resources.getColorStateList(R.color.reply, getTheme()));
                    PrepareToReply(reply);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    reply.setTextColor(resources.getColorStateList(R.color.gray, getTheme()));
                }
                return false;
            }
        });

        // region if reply
        LinearLayout messageReply = null;
        if (user.getIdReply() != null && user.getReplyPreview() != null) {
            MessageDAO replayedMsg = FindReplayedMsg(user.getIdReply(), messageDAOList);
            messageReply = new LinearLayout(this);

            messageReply.setLayoutParams(params[2]);
            messageReply.setPadding(20, 10, 20, 10);
            messageReply.setOrientation(LinearLayout.VERTICAL);

            // region message
            LinearLayout messageReplayed = new LinearLayout(this);
            messageReplayed.setPadding(20, 5, 20, 5);
            messageReplayed.setLayoutParams(params[3]);
            messageReplayed.setOrientation(LinearLayout.VERTICAL);

            // region author
            TextView textViewAuthorReplayed = new TextView(this);
            textViewAuthorReplayed.setLayoutParams(params[1]);
            // endregion

            // region replayedMsg
            TextView textViewTxtReplayed = new TextView(this);
            textViewTxtReplayed.setTextColor(resources.getColorStateList(R.color.black, this.getTheme()));
            textViewTxtReplayed.setLayoutParams(params[1]);
            // endregion

            messageReplayed.addView(textViewAuthorReplayed);
            messageReplayed.addView(textViewTxtReplayed);
            // endregion

            message.setLayoutParams(params[1]);
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
                message.setBackground(drawables[1]);
                messageReply.setBackground(drawables[3]);
                messageWithDate.addView(reply);
                messageWithDate.addView(textViewDate);
                messageWithDate.addView(messageReply);
                messageContainer.setGravity(Gravity.RIGHT);
                textViewTime.setGravity(Gravity.LEFT);
            } else {
                message.setBackground(drawables[0]);
                messageReply.setBackground(drawables[2]);
                messageWithDate.addView(messageReply);
                messageWithDate.addView(textViewDate);
                messageWithDate.addView(reply);
                messageContainer.setGravity(Gravity.LEFT);
                textViewTime.setGravity(Gravity.RIGHT);
            }
            // endregion
        } else {
            message.addView(textViewTime);

            // region setters
            if (Objects.equals(user.getAuthor(), author.getText())) {
                message.setBackground(drawables[1]);
                messageWithDate.addView(reply);
                messageWithDate.addView(textViewDate);
                messageWithDate.addView(message);
                messageContainer.setGravity(Gravity.RIGHT);
                textViewTime.setGravity(Gravity.LEFT);
            } else {
                message.setBackground(drawables[0]);
                messageWithDate.addView(message);
                messageWithDate.addView(textViewDate);
                messageWithDate.addView(reply);
                messageContainer.setGravity(Gravity.LEFT);
                textViewTime.setGravity(Gravity.RIGHT);
            }
            // endregion
        }
        // endregion

        // endregion

        messageContainer.addView(messageWithDate);
        messageContainer.addView(textViewReplacedId);
        messageContainer.addView(textViewReplacedTxt);

        runOnUiThread(() -> chatContainer.addView(messageContainer));
    }

    private void PrepareToReply(TextView reply) {
        ViewGroup group = ((ViewGroup) reply.getParent().getParent());
        TextView id = ((TextView) group.getChildAt(1));
        TextView txt = ((TextView) group.getChildAt(2));

        replayedID = id.getText().toString();
        replayedTxt = txt.getText().toString();

        sendMessage.requestFocus();
        imm.showSoftInput(sendMessage, InputMethodManager.SHOW_IMPLICIT);
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