package com.technopark.bulat.advandroidhomework3.network.response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bulat on 13.11.15.
 */

public class GeneralResponse {
    private String action;
    private JSONObject jsonData;

    public GeneralResponse(String action, String stringResponse) {
        this.action = action;
        try {
            JSONObject jsonResponse = new JSONObject(stringResponse);
            if (action.equals("welcome")) {
                jsonData = jsonResponse;
            } else {
                jsonData = jsonResponse.getJSONObject("data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public JSONObject getJsonData() {
        return jsonData;
    }

    public void setJsonData(JSONObject jsonData) {
        this.jsonData = jsonData;
    }
}
