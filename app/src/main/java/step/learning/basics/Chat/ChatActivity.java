package step.learning.basics.Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import step.learning.basics.R;

public class ChatActivity extends AppCompatActivity {
    private LinearLayout chatContainer;
    private Services services;
    private String content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatContainer = findViewById(R.id.chat_container);
        services = new Services(this);

        new Thread(() -> {
            content = services.LoadUrl();
            ParseContent();
        }).start();
    }

    private void ParseContent() {
        // TODO: parse JSON
        runOnUiThread(this::ShowContent);
    }

    private void ShowContent() {
        TextView textView = new TextView(this);
        textView.setText(content);
        chatContainer.addView(textView);
    }
}