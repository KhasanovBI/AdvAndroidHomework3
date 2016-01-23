package com.technopark.bulat.advandroidhomework3.models;

import java.util.Date;

/**
 * Created by bulat on 08.11.15.
 */
public class Message {
    private String userId;
    private String userNick;
    private String text;
    private Date time;
    private Attach attach;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Attach getAttach() {
        return attach;
    }

    public void setAttach(Attach attach) {
        this.attach = attach;
    }
}
