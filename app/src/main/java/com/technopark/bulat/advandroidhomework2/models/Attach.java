package com.technopark.bulat.advandroidhomework2.models;

/**
 * Created by bulat on 21.01.16.
 */
public class Attach {
    private String mime;
    private String data;

    public Attach(String mime, String data) {
        this.mime = mime;
        this.data = data;
    }

    public Attach() {
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
