package com.krux.service;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.krux.json.JSONBuilder;
import com.krux.net.Client;
import com.krux.session.ActiveSession;

import org.json.simple.JSONObject;

import java.io.IOException;

public class GCMTokenRefreshListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        JSONBuilder json_builder = new JSONBuilder();
        try {
            InstanceID instance_id = InstanceID.getInstance(getApplicationContext());
            String token = instance_id.getToken("317557010111",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            String outgoing_msg = json_builder.buildUpdateGCMTokenRequest(
                    token,
                    ActiveSession.getUsername(),
                    ActiveSession.getPassword()
            );

            Client client = new Client();
            System.out.println("GCM connection made");
            String return_str = client.handleTransaction(outgoing_msg);
            System.out.println("response: " + return_str);
            JSONObject response_json = json_builder.getJSONObject(return_str);
            response_json = (JSONObject) response_json.get("response");
            Long op = (Long) response_json.get("op");
            if(op == 1){
                System.out.println("FAILURE STORING GCM TOKEN");
            }
        } catch (IOException e) {
            System.out.println("FAILURE STORING GCM TOKEN");
            e.printStackTrace();
        }
    }
}