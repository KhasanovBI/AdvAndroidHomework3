package com.technopark.bulat.advandroidhomework2.network.request.messages;

import com.technopark.bulat.advandroidhomework2.network.request.RequestMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bulat on 16.11.15.
 */
public class DelContactRequest implements RequestMessage {
    private final String uid;
    private final String cid;
    private final String sid;

    public DelContactRequest(String uid, String cid, String sid) {
        this.uid = uid;
        this.cid = cid;
        this.sid = sid;
    }

    @Override
    public String getRequestString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("uid", uid);
            jsonData.put("cid", cid);
            jsonData.put("sid", sid);

            jsonObject.put("action", "delcontact");
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
