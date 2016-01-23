package com.technopark.bulat.advandroidhomework3.network.request.messages;

import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.request.RequestMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by bulat on 16.11.15.
 */
public class ImportRequest implements RequestMessage {
    private final List<User> contacts;

    public ImportRequest(List<User> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String getRequestString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonContactsArray = new JSONArray();
            for (User contact : contacts) {
                JSONObject jsonContact = new JSONObject();
                jsonContact.put("myid", contact.getUid());
                jsonContact.put("name", contact.getNick());
                jsonContact.put("phone", contact.getPhone());
                jsonContact.put("email", contact.getEmail());
                jsonContactsArray.put(jsonContact);
            }

            JSONObject jsonData = new JSONObject();
            jsonData.put("contacts", jsonContactsArray);

            jsonObject.put("action", "import");
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
