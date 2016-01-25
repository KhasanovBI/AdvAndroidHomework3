package com.technopark.bulat.advandroidhomework3.network.response.messages;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.response.messages.base.ResponseMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bulat on 15.11.15.
 */
public class ContactListResponse extends ResponseMessage {
    private static final String LOG_TAG = "MyContactListResponse";
    private List<User> users;

    public ContactListResponse(JSONObject jsonData) {
        super(jsonData);
        Log.d(LOG_TAG, jsonData.toString());
        if (status == 0) {
            try {
                users = new ArrayList<>();
                JSONArray jsonUsers = jsonData.getJSONArray("list");
                for (int i = 0; i < jsonUsers.length(); ++i) {
                    JSONObject jsonUser = (JSONObject) jsonUsers.get(i);
                    User user = new User();
                    user.setUid(jsonUser.getString("uid"));
                    user.setNick(jsonUser.getString("nick"));
                    user.setEmail(jsonUser.getString("email"));
                    user.setPhone(jsonUser.getString("phone"));
                    user.setPicture(jsonUser.getString("picture"));
                    users.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<User> getUsers() {
        return users;
    }
}
