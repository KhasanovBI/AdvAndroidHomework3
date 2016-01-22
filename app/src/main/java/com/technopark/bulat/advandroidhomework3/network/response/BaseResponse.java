package com.technopark.bulat.advandroidhomework3.network.response;


import org.json.JSONObject;

/**
 * Created by bulat on 13.11.15.
 */

public class BaseResponse {
    private String action;
    private JSONObject jsonData;

    public BaseResponse(String action, JSONObject jsonData) {
        this.action = action;
        this.jsonData = jsonData;
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
