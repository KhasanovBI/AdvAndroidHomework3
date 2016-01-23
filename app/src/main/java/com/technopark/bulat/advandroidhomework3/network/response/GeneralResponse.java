package com.technopark.bulat.advandroidhomework3.network.response;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bulat on 13.11.15.
 */

public class GeneralResponse {
    private String action;
    private JSONObject jsonData;

    public GeneralResponse(String stringResponse) {
        try {
            JSONObject jsonResponse = new JSONObject(stringResponse);
            action = jsonResponse.getString("action");
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
