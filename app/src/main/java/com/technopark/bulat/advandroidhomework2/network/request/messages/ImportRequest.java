package com.technopark.bulat.advandroidhomework2.network.request.messages;

import com.technopark.bulat.advandroidhomework2.models.Contact;
import com.technopark.bulat.advandroidhomework2.network.request.RequestMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bulat on 16.11.15.
 */
public class ImportRequest implements RequestMessage {
    private final List<Contact> contacts;

    public ImportRequest(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String getRequestString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonContactsArray = new JSONArray();
            for (Contact contact : contacts) {
                JSONObject jsonContact = new JSONObject();
                jsonContact.put("myid", contact.getMyid());
                jsonContact.put("name", contact.getName());
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
