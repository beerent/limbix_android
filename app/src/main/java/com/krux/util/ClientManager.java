package com.krux.util;


import com.krux.json.JSONBuilder;
import com.krux.net.Client;
import com.krux.session.ActiveSession;

import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * Created by brent on 9/7/16.
 */
public class ClientManager {
    private JSONBuilder json_builder;
    private Client client;

    public ClientManager(){
        this.json_builder = new JSONBuilder();
    }

    public boolean updateLimb(String limb_id, HashMap<String, String> key_values){
        return false;
    }

    public JSONObject updateLimb(String type, String limb_id, String key, String value){
        this.client = new Client();
        JSONObject jo = getJSON(type, limb_id);
        addRequestKeyValue(jo, key, value);

        String response = client.handleTransaction(jo.toString());
        JSONObject response_json = this.json_builder.getJSONObject(response);
        response_json = (JSONObject) response_json.get("response");
        return response_json;
    }

    private JSONObject addRequestKeyValue(JSONObject json, String key, String value){
        JSONObject jo = (JSONObject) json.get("request");
        jo.put(key, value);
        return json;
    }

    private JSONObject getJSON(String type, String reminder_id){
        String username = ActiveSession.getUsername();
        String password = ActiveSession.getPassword();

        JSONObject return_json = new JSONObject();
        JSONObject request_json = new JSONObject();
        request_json.put("username", username);
        request_json.put("password", password);
        request_json.put("type", type);
        request_json.put("reminder_id", reminder_id);

        return_json.put("request", request_json);
        return return_json;
    }
}
