package step.learning.basics.Chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatDAO {

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(List<UserDAO> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public List<UserDAO> getData() {
        return data;
    }

    public ChatDAO(JSONObject obj) throws JSONException {
        setStatus(obj.getString("status"));
        setData(new ArrayList<>());

        try {
            ParseData(obj.getJSONArray("data"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String status;
    private List<UserDAO> data;

    private void ParseData(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObj = data.getJSONObject(i);
            this.data.add(new UserDAO(jsonObj));
        }
    }
}
