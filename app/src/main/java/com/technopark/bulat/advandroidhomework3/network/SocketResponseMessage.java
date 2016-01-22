package com.technopark.bulat.advandroidhomework3.network;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by bulat on 22.01.16.
 */
public class SocketResponseMessage implements Serializable {
    private int connectionError = -1;
    private JSONObject jsonResponse;

    public SocketResponseMessage(int connectionError) {
        this.connectionError = connectionError;
    }

    public SocketResponseMessage(JSONObject jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public int getConnectionError() {
        return connectionError;
    }

    public JSONObject getJsonResponse() {
        return jsonResponse;
    }
}
