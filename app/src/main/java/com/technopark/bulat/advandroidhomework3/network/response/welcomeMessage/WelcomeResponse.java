package com.technopark.bulat.advandroidhomework3.network.response.welcomeMessage;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by bulat on 13.11.15.
 */
public class WelcomeResponse {
    private static final String LOG_TAG = "MyWelcomeResponse";
    private String message;
    private long time;

    public WelcomeResponse(JSONObject jsonData) {
        try {
            message = jsonData.getString("message");
            time = jsonData.getLong("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Date serverTime = new Date(time * 1000);
        Log.d(LOG_TAG, message);
        Log.d(LOG_TAG, "Server time is: " + serverTime);
    }
}
