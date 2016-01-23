package com.technopark.bulat.advandroidhomework3.network.response.messages;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.network.response.messages.base.ResponseMessage;

import org.json.JSONObject;

/**
 * Created by bulat on 15.11.15.
 */
public class MessageResponse extends ResponseMessage {
    private static final String LOG_TAG = "MyMessageResponse";

    public MessageResponse(JSONObject jsonData) {
        super(jsonData);
        Log.d(LOG_TAG, jsonData.toString());
    }
}
