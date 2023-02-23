package step.learning.basics.Chat;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import step.learning.basics.CurrencyRates.RatesActivity;

public class Services {
    private ChatActivity chatActivity;
    public static final String CHAT_URL = "https://diorama-chat.ew.r.appspot.com/story";

    public Services(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
    }

    public String LoadUrl() {
        StringBuilder sb = new StringBuilder();
        String content = "";

        try (InputStream inputStream = new URL(Services.CHAT_URL).openStream()) {
            int sym;
            while ((sym = inputStream.read()) != -1) {
                sb.append((char) sym);
            }

            content = new String(
                    sb.toString().getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8);
        } catch (NetworkOnMainThreadException ex) {
            Log.d("LoadUrl", "NetworkOnMainThreadException: " + ex.getMessage());
        } catch (MalformedURLException ex) {
            Log.d("LoadUrl", "MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            Log.d("LoadUrl", "IOException: " + ex.getMessage());
        } finally {
            return content;
        }
    }

    public void postChatMessage(MessageDAO messageDAO) {
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
            body.write(getJson(messageDAO).toString().getBytes());

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
            Log.d("IOException", e.getMessage());
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
    }

    private JSONObject getJson(MessageDAO messageDAO) throws JSONException {
        JSONObject object = new JSONObject()
                .put("author", messageDAO.getAuthor())
                .put("txt", messageDAO.getTxt());

        if (messageDAO.getIdReply() != null)
            object.put("idReply", messageDAO.getIdReply());
        if (messageDAO.getReplyPreview() != null)
            object.put("replyPreview", messageDAO.getReplyPreview());

        return object;
    }
}
