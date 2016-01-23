package com.technopark.bulat.advandroidhomework3.network.response.messages.base;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bulat on 21.01.16.
 */
public abstract class ResponseMessage {
    protected int status;
    protected String error;

    public ResponseMessage(JSONObject jsonData) {
        setStatusAndError(jsonData);
    }

    private void setStatusAndError(JSONObject jsonData) {
        try {
            status = jsonData.getInt("status");
            if (status != 0) {
                error = jsonData.getString("error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
