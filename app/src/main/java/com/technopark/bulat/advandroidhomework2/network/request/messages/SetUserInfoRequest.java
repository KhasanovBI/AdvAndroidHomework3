package com.technopark.bulat.advandroidhomework2.network.request.messages;

import com.technopark.bulat.advandroidhomework2.models.User;
import com.technopark.bulat.advandroidhomework2.network.request.RequestMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bulat on 20.11.15.
 */
public class SetUserInfoRequest implements RequestMessage {
    private final String cid;
    private final String sid;
    private final User user;

    public SetUserInfoRequest(String cid, String sid, User user) {
        this.cid = cid;
        this.sid = sid;
        this.user = user;
    }

    @Override
    public String getRequestString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("cid", cid);
            jsonData.put("sid", sid);
            jsonData.put("user_status", user.getStatus());
            jsonData.put("email", user.getEmail());
            jsonData.put("phone", user.getPhone());
            jsonData.put("picture", user.getPicture());

            jsonObject.put("action", "setuserinfo");
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
