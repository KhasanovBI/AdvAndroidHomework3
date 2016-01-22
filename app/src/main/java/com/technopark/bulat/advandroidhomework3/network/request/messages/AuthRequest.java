package com.technopark.bulat.advandroidhomework3.network.request.messages;

import com.technopark.bulat.advandroidhomework3.network.request.RequestMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bulat on 16.11.15.
 */
public class AuthRequest implements RequestMessage {
    private final String login;
    private final String pass;
    public AuthRequest(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    @Override
    public String getRequestString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("login", login);
            jsonData.put("pass", pass);

            jsonObject.put("action", "auth");
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
