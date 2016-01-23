package com.technopark.bulat.advandroidhomework3.network;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by bulat on 22.01.16.
 */
public class SocketResponseMessage implements Serializable {
    private int connectionError = -1;
    private String stringResponse;

    public SocketResponseMessage(int connectionError) {
        this.connectionError = connectionError;
    }

    public SocketResponseMessage(String stringResponse) {
        this.stringResponse = stringResponse;
    }

    public int getConnectionError() {
        return connectionError;
    }

    public String getStringResponse() {
        return stringResponse;
    }
}
