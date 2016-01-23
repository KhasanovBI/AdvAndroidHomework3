package com.technopark.bulat.advandroidhomework3.network.response.messages;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.response.messages.base.ResponseMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bulat on 15.11.15.
 */
public class UserInfoResponse extends ResponseMessage {
    private static final String LOG_TAG = "MyUserInfoResponse";

    private User user;

    public UserInfoResponse(JSONObject jsonData) {
        super(jsonData);
        Log.d(LOG_TAG, jsonData.toString());
        if (status == 0) {
            try {
                user = new User();
                user.setNick(jsonData.getString("nick"));
                user.setEmail(jsonData.getString("email"));
                user.setPhone(jsonData.getString("phone"));
                user.setPicture(jsonData.getString("picture"));
                user.setStatus(jsonData.getString("user_status"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public User getUser() {
        return user;
    }
}
