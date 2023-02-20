package step.learning.basics.Chat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class UserDAO {
    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTxt() {
        return txt;
    }

    public String getMoment() {
        return moment;
    }

    public String getReplyPreview() {
        return replyPreview;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public void setMoment(String moment) {
        this.moment = moment;
    }

    public void setReplyPreview(String replyPreview) {
        this.replyPreview = replyPreview;
    }

    public UserDAO(JSONObject obj) throws JSONException {
        this.id = obj.getString("id");
        this.author = obj.getString("author");
        this.txt = obj.getString("txt");
        this.moment = obj.getString("moment");
        if (!obj.isNull("replyPreview")) {
            this.replyPreview = obj.getString("id");
        }
    }

    private String id;
    private String author;
    private String txt;
    private String moment;
    private String replyPreview = null;
}
