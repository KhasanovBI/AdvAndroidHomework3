package com.technopark.bulat.advandroidhomework2.network.request.messages;

import com.technopark.bulat.advandroidhomework2.models.Attach;
import com.technopark.bulat.advandroidhomework2.network.request.RequestMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bulat on 16.11.15.
 */
public class MessageRequest implements RequestMessage {
    private final String cid;
    private final String sid;
    private final String uid;
    private final String body;
    private final Attach attach;

    public MessageRequest(String cid, String sid, String uid, String body, Attach attach) {
        this.cid = cid;
        this.sid = sid;
        this.uid = uid;
        this.body = body;
        this.attach = attach;
    }

    @Override
    public String getRequestString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonAttach = new JSONObject();
            jsonAttach.put("mime", attach.getMime());
            jsonAttach.put("data", attach.getData());

            JSONObject jsonData = new JSONObject();
            jsonData.put("cid", cid);
            jsonData.put("sid", sid);
            jsonData.put("uid", uid);
            jsonData.put("body", body);
            jsonData.put("attach", jsonAttach);

            jsonObject.put("action", "message");
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
