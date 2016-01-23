package com.technopark.bulat.advandroidhomework3.network.response.messages;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.network.response.messages.base.ResponseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthResponse extends ResponseMessage {
    private static final String LOG_TAG = "MyAuthResponse";
    private String sid;
    private String cid;

    public AuthResponse(JSONObject jsonData) {
        super(jsonData);
        Log.d(LOG_TAG, jsonData.toString());
        if (status == 0) {
            try {
                sid = jsonData.getString("sid");
                cid = jsonData.getString("cid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getSid() {
        return sid;
    }

    public String getCid() {
        return cid;
    }
}