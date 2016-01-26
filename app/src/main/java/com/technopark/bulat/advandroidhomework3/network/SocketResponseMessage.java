package com.technopark.bulat.advandroidhomework3.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by bulat on 22.01.16.
 */
public class SocketResponseMessage implements Serializable {
    private int connectionError = -1;
    private String action;
    private String stringResponse;

    public SocketResponseMessage(int connectionError) {
        this.connectionError = connectionError;
    }

    public SocketResponseMessage(String stringResponse) {
        this.stringResponse = stringResponse;
        try {
            JSONObject jsonResponse = new JSONObject(stringResponse);
            action = jsonResponse.getString("action");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getConnectionError() {
        return connectionError;
    }

    public String getStringResponse() {
        return stringResponse;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
