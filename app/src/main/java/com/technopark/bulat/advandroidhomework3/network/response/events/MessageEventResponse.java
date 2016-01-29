package com.technopark.bulat.advandroidhomework3.network.response.events;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.models.Attach;
import com.technopark.bulat.advandroidhomework3.models.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by bulat on 15.11.15.
 */
public class MessageEventResponse {
    private static final String LOG_TAG = "MyMessageEventResponse";
    private Message message;

    public MessageEventResponse(JSONObject jsonData) {
        Log.d(LOG_TAG, jsonData.toString());
        try {
            JSONObject jsonAttach = jsonData.getJSONObject("attach");
            Attach attach = new Attach();
            attach.setMime(jsonAttach.getString("mime"));
            attach.setData(jsonAttach.getString("data"));

            message = new Message();
            message.setUserId(jsonData.getString("from"));
            message.setUserNick(jsonData.getString("nick"));
            message.setText(jsonData.getString("body"));
            message.setTime(new Date(jsonData.getLong("time") * 1000));
            message.setAttach(attach);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Message getMessage() {
        return message;
    }
}
