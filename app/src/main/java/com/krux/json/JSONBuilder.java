package com.krux.json;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.StringReader;

/**
 * Created by brent on 8/24/16.
 */
public class JSONBuilder {
    public JSONBuilder(){}

    public String buildLoginRequest(String username, String password){
        String return_str = null;
            JSONObject request = new JSONObject();
            JSONObject inner_json = new JSONObject();
            inner_json.put("type", "login");
            inner_json.put("username", username);
            inner_json.put("password", password);
            request.put("request", inner_json);
            return_str = request.toString();
        return return_str;
    }

    public String buildUpdateGCMTokenRequest(String token, String username, String password) {
        String return_str = null;
        JSONObject request = new JSONObject();
        JSONObject inner_json = new JSONObject();
        inner_json.put("type", "update_gcm_token");
        inner_json.put("token", token);
        inner_json.put("username", username);
        inner_json.put("password", password);
        request.put("request", inner_json);
        return_str = request.toString();
        return return_str;
    }

    public String buildRegisterRequest(String first_name,
                                       String last_name,
                                       String username,
                                       String password1,
                                       String password2,
                                       String email) {
        String return_str = null;
        JSONObject request = new JSONObject();
        JSONObject inner_json = new JSONObject();
        inner_json.put("type", "register_user");
        inner_json.put("first_name", first_name);
        inner_json.put("last_name", last_name);
        inner_json.put("username", username);
        inner_json.put("password1", password1);
        inner_json.put("password2", password2);
        inner_json.put("email", email);
        request.put("request", inner_json);
        return_str = request.toString();
        return return_str;

    }

    public JSONObject getJSONObject(String s){
        JSONParser parser = new JSONParser();
        try {
            JSONObject json_obj = (JSONObject) parser.parse(new StringReader(s));
            return json_obj;
        } catch (Exception e) {
            return null;
        }
    }
}