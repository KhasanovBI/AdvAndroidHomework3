package com.technopark.bulat.advandroidhomework3.network.response.messages;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.response.messages.base.ResponseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class AddContactResponse extends ResponseMessage {
    private static final String LOG_TAG = "MyAddContactResponse";

    public AddContactResponse(JSONObject jsonData) {
        super(jsonData);
        Log.d(LOG_TAG, jsonData.toString());
    }
}