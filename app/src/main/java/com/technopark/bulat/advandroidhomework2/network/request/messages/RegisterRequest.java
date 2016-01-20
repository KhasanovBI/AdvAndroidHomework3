package com.technopark.bulat.advandroidhomework2.network.request.messages;

import com.technopark.bulat.advandroidhomework2.network.request.RequestMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bulat on 16.11.15.
 */
public class RegisterRequest implements RequestMessage {
    private final String login;
    private final String pass;
    private final String nick;

    public RegisterRequest(String login, String pass, String nick) {
        this.login = login;
        this.pass = pass;
        this.nick = nick;
    }

    @Override
    public String getRequestString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("login", login);
            jsonData.put("pass", pass);
            jsonData.put("nick", nick);

            jsonObject.put("action", "register");
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
