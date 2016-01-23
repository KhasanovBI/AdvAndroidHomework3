package com.technopark.bulat.advandroidhomework3.network.response.messages;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.response.messages.base.ResponseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class AddContactResponse extends ResponseMessage {
    private static final String LOG_TAG = "MyAddContactResponse";
    private User user;

    public AddContactResponse(JSONObject jsonData) {
        super(jsonData);
        Log.d(LOG_TAG, jsonData.toString());
        if (status == 0) {
            try {
                user = new User();
                JSONObject jsonUser = jsonData.getJSONObject("user");
                user.setUid(jsonUser.getString("uid"));
                user.setNick(jsonUser.getString("nick"));
                user.setEmail(jsonUser.getString("email"));
                user.setPhone(jsonUser.getString("phone"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public User getUser() {
        return user;
    }
}