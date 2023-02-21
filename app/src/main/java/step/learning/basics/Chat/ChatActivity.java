package step.learning.basics.Chat;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.media.VolumeShaper;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import step.learning.basics.R;

public class ChatActivity extends AppCompatActivity {
    private Resources resources;
    private UUID uuid;
    private Services services;
    private String content = null;
    private ChatDAO chatDAO;
    private UserDAO userDAO;
    private LinearLayout chatContainer;
    private ImageButton send;
    private TextView author;
    private EditText sendMessage;
    @SuppressLint("SimpleDateFormat")
    private DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    @SuppressLint("SimpleDateFormat")
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        uuid = UUID.randomUUID();
        resources = getResources();

        author = findViewById(R.id.user_name);
        sendMessage = findViewById(R.id.edit_txt);
        chatContainer = findViewById(R.id.chat_container);
        send = findViewById(R.id.send);
        services = new Services(this);
        SetListeners();

        new Thread(() -> {
            content = services.LoadUrl();
            ParseContent();
        }).start();
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
            userDAO = new UserDAO();

            userDAO.setAuthor(author.getText().toString());
            userDAO.setTxt(sendMessage.getText().toString());
            userDAO.setIdReply(UUID.fromString("acd98fa4-b1cf-11ed-ac39-f23c93f195e6"));
            userDAO.setReplyPreview("i");

            new Thread(this::postChatMessage).start();
        }
    }

    private void postChatMessage() {
        try {
            // region Send to server
            URL url = new URL(Services.CHAT_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true); // connection will be send data (send body)
            urlConnection.setDoInput(true); // connection will get data (get body)
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setChunkedStreamingMode(0); // not fragment thread

            OutputStream body = urlConnection.getOutputStream();
//            body.write(String.format("{\"author\":\"%s\", \"txt\":\"%s\"}",
//                    userDAO.getAuthor(), userDAO.getTxt()).getBytes());
            body.write(String.format("{\"author\":\"%s\", \"txt\":\"%s\", \"idReply\":\"%s\", \"replyPreview\":\"%s\"}",
                    userDAO.getAuthor(), userDAO.getTxt(), userDAO.getIdReply().toString(), userDAO.getReplyPreview()).getBytes());

            body.flush();
            body.close();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode != 200) {
                Log.d("postChatMessage", "Response code: " + responseCode);
                return;
            }
            // endregion

            // region Response
            InputStream reader = urlConnection.getInputStream();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int len;
            while ((len = reader.read(chunk)) != -1) {
                bytes.write(chunk, 0, len);
            }

            Log.d("postChatMessage", new String(bytes.toByteArray(), StandardCharsets.UTF_8));

            bytes.close();
            reader.close();
            urlConnection.disconnect();
            // endregion
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        content = services.LoadUrl();
        ParseContent();
    }

    private void ParseContent() {
        try {
            chatDAO = new ChatDAO(new JSONObject(content));
        } catch (JSONException e) {
            Log.d("DataParse", e.getMessage());
        }
        runOnUiThread(this::ShowContent);
    }

    @SuppressLint("RtlHardcoded")
    private void ShowContent() {
        Resources resources = getResources();
        @SuppressLint("UseCompatLoadingForDrawables") android.graphics.drawable.Drawable
                ratesBgLeft = resources.getDrawable(R.drawable.message_cell_left, this.getTheme());
        @SuppressLint("UseCompatLoadingForDrawables") android.graphics.drawable.Drawable
                ratesBgRight = resources.getDrawable(R.drawable.message_cell_right, this.getTheme());
        @SuppressLint("UseCompatLoadingForDrawables") android.graphics.drawable.Drawable
                ratesBgLeftReply = resources.getDrawable(R.drawable.message_cell_left_reply, this.getTheme());
        @SuppressLint("UseCompatLoadingForDrawables") android.graphics.drawable.Drawable
                ratesBgRightReply = resources.getDrawable(R.drawable.message_cell_right_reply, this.getTheme());

        // region Layout params
        LinearLayout.LayoutParams layoutParamsContainer = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams layoutParamsVerticalAdaptive = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParamsVerticalAdaptive.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams layoutParamsHorizontalAdaptive = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParamsHorizontalAdaptive.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams layoutParamsWrapWithMargins = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParamsWrapWithMargins.setMargins(10, 20, 10, 20);

        LinearLayout.LayoutParams layoutParamsWrap = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // endregion

        List<UserDAO> userDAOList = chatDAO.getData();
        for (UserDAO user : userDAOList) {
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
            textViewAuthor.setLayoutParams(layoutParamsWrap);
            // endregion

            // region txt
            TextView textViewTxt = new TextView(this);

            textViewTxt.setText(user.getTxt());
            textViewTxt.setTextColor(resources.getColorStateList(R.color.black, this.getTheme()));
            textViewTxt.setLayoutParams(layoutParamsWrap);
            // endregion

            message.addView(textViewAuthor);
            message.addView(textViewTxt);
            // endregion

            // region if reply
            LinearLayout messageReply = null;
            if (user.getIdReply() != null && user.getReplyPreview() != null) {
                UserDAO replayedMsg = FindReplayedMsg(user.getIdReply(), userDAOList);
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

                textViewAuthorReplayed.setText(replayedMsg.getAuthor());
                textViewAuthorReplayed.setTextColor(resources.getColorStateList(R.color.black, this.getTheme()));
                textViewAuthorReplayed.setLayoutParams(layoutParamsWrap);
                // endregion

                // region replayedMsg
                TextView textViewTxtReplayed = new TextView(this);

                textViewTxtReplayed.setText(replayedMsg.getTxt());
                textViewTxtReplayed.setTextColor(resources.getColorStateList(R.color.black, this.getTheme()));
                textViewTxtReplayed.setLayoutParams(layoutParamsWrap);
                // endregion

                messageReplayed.addView(textViewAuthorReplayed);
                messageReplayed.addView(textViewTxtReplayed);
                // endregion

                messageReply.addView(messageReplayed);
                message.setLayoutParams(layoutParamsVerticalAdaptive);
                messageReply.addView(message);
                messageReply.addView(textViewTime);

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
            } else {
                message.setBackground(ratesBgLeft);
                message.addView(textViewTime);

                if (Objects.equals(user.getAuthor(), author.getText())) {
                    messageWithDate.addView(textViewDate);
                    messageWithDate.addView(message);
                    messageContainer.setGravity(Gravity.RIGHT);
                    textViewTime.setGravity(Gravity.LEFT);
                } else {
                    messageWithDate.addView(message);
                    messageWithDate.addView(textViewDate);
                    messageContainer.setGravity(Gravity.LEFT);
                    textViewTime.setGravity(Gravity.RIGHT);
                }
            }
            // endregion

            // endregion

            messageContainer.addView(messageWithDate);
            chatContainer.addView(messageContainer);
        }
    }

    private UserDAO FindReplayedMsg(UUID uuid, List<UserDAO> userDAOList) {
        for (UserDAO msg : userDAOList) {
            if (msg.getId().equals(uuid)) {
                return msg;
            }
        }

        return new UserDAO();
    }
}