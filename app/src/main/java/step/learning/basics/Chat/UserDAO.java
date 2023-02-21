package step.learning.basics.Chat;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class UserDAO {
    public void setId(UUID id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }

    public void setIdReply(UUID idReply) {
        this.idReply = idReply;
    }

    public void setReplyPreview(String replyPreview) {
        this.replyPreview = replyPreview;
    }

    public UUID getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTxt() {
        return txt;
    }

    public Date getMoment() {
        return moment;
    }

    public UUID getIdReply() {
        return idReply;
    }

    public String getReplyPreview() {
        return replyPreview;
    }

    public UserDAO(JSONObject obj) throws JSONException {
        setId(UUID.fromString(obj.getString("id")));
        setAuthor(obj.getString("author"));
        setTxt(obj.getString("txt"));

        try {
            setMoment(dateFormat.parse(obj.getString("moment")));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (!obj.isNull("idReply")) {
            setIdReply(UUID.fromString(obj.getString("idReply")));
        }
        if (!obj.isNull("replyPreview")) {
            setReplyPreview(obj.getString("replyPreview"));
        }
    }

    public UserDAO() {
        setId(UUID.randomUUID());
        setMoment(new Date());
    }

    private UUID id;
    private String author;
    private String txt;
    private Date moment;
    private UUID idReply;
    private String replyPreview = null;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy KK:mm:ss a");
}
