package step.learning.basics.Chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatDAO {
    public ChatDAO(JSONObject obj) throws JSONException {
        this.status = obj.getString("status");
        this.data = new ArrayList<>();

        try {
            ParseData(obj.getJSONArray("data"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String status;
    private List<UserDAO> data;

    public List<UserDAO> getData() {
        return data;
    }

    public void setStatus(String status) throws JSONException {
        this.status = status;
    }

    public void setData(List<UserDAO> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    private void ParseData(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObj = data.getJSONObject(i);
            this.data.add(new UserDAO(jsonObj));
        }
    }
}
