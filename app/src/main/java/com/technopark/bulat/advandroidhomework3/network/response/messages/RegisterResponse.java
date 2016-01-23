package com.technopark.bulat.advandroidhomework3.network.response.messages;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.network.response.messages.base.ResponseMessage;

import org.json.JSONObject;

public class RegisterResponse extends ResponseMessage {
    private static final String LOG_TAG = "MyRegisterResponse";

    public RegisterResponse(JSONObject jsonData) {
        super(jsonData);
        Log.d(LOG_TAG, jsonData.toString());
    }
}