package com.technopark.bulat.advandroidhomework3.network.request.messages;

import com.technopark.bulat.advandroidhomework3.network.request.RequestMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bulat on 16.11.15.
 */
public class UserInfoRequest implements RequestMessage {
    private final String userId;
    private final String cid;
    private final String sid;

    public UserInfoRequest(String userId, String cid, String sid) {
        this.userId = userId;
        this.cid = cid;
        this.sid = sid;
    }

    @Override
    public String getRequestString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("user", userId);
            jsonData.put("cid", cid);
            jsonData.put("sid", sid);

            jsonObject.put("action", "userinfo");
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
