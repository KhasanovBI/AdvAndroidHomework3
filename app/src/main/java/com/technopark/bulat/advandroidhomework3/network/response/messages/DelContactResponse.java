package com.technopark.bulat.advandroidhomework3.network.response.messages;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.network.response.messages.base.ResponseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class DelContactResponse extends ResponseMessage {
    private static final String LOG_TAG = "MyDelContactResponse";
    private String uid;

    public DelContactResponse(JSONObject jsonData) {
        super(jsonData);
        Log.d(LOG_TAG, jsonData.toString());
        if (status == 0) {
            try {
                uid = jsonData.getString("uid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUid() {
        return uid;
    }
}