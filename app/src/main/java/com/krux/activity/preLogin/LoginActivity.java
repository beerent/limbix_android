package com.krux.activity.preLogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brent.helloworld.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.krux.json.JSONBuilder;
import com.krux.net.Client;
import com.krux.session.ActiveSession;
import com.krux.util.IntentManager;

import org.json.simple.JSONObject;

import java.io.IOException;

public class LoginActivity extends Activity {
    private EditText username, password, developer_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button register_button = (Button) findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new IntentManager().loginToRegisterIntent(LoginActivity.this);
                startActivity(intent);
            }
        });


    }

    public void onClickLogin(View view) {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        developer_key = (EditText) findViewById(R.id.developer_server_key);

        String username_value = username.getText().toString();
        String password_value = password.getText().toString();

        String key = developer_key.getText().toString();
        if(key != null && !key.equals(""))
            ActiveSession.setDeveloperServerKey(key);

        new ClientThread().execute(username_value, password_value);

    }

    public void onClickRegister(View view) {
        Intent intent = new IntentManager().loginToRegisterIntent(LoginActivity.this);
        LoginActivity.this.startActivity(intent);
    }









    private class ClientThread extends AsyncTask<String, Void, Boolean> {
        private Client client;
        private JSONObject response_json;
        private String username, password;
        //private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        private ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            progDailog = new ProgressDialog(LoginActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        protected Boolean doInBackground(String... str) {
            JSONBuilder json_builder = new JSONBuilder();
            this.username = str[0];
            this.password = str[1];
            String outgoing_msg = json_builder.buildLoginRequest(this.username, this.password);

            this.client = new Client();
            this.client.receive();                      // OK
            this.client.send(outgoing_msg);        // SEND LOGIN REQUEST
            String return_str = client.receive();  // RECEIVE RESPONSE

            JSONObject response_json = json_builder.getJSONObject(return_str);
            response_json = (JSONObject) response_json.get("response");
            this.response_json = response_json;
            Long op = (Long) response_json.get("op");
            if(op == 0){
                try {
                    InstanceID instance_id = InstanceID.getInstance(LoginActivity.this);
                    String token = instance_id.getToken("317557010111",
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                    outgoing_msg = json_builder.buildUpdateGCMTokenRequest(
                            token,
                            this.username,
                            this.password
                    );

                    this.client = new Client();
                    System.out.println("GCM connection made");
                    return_str = this.client.handleTransaction(outgoing_msg);
                    System.out.println("response: " + return_str);
                    response_json = json_builder.getJSONObject(return_str);
                    response_json = (JSONObject) response_json.get("response");
                    op = (Long) response_json.get("op");
                    if(op == 1){
                        this.response_json = response_json;
                        System.out.println("FAILURE STORING GCM TOKEN");
                    }
                } catch (IOException e) {
                    System.out.println("FAILURE STORING GCM TOKEN");
                    e.printStackTrace();
                }
            }
            return op == 0;
        }

        protected void onPostExecute(Boolean result) {

            System.out.println("result:" + result);
            JSONBuilder jb = new JSONBuilder();

            if (result){
                //VALID USERNAME/PASSWORD
                ActiveSession.setLoggedIn(true);
                ActiveSession.setUsername(this.username);
                ActiveSession.setPassword(this.password);
                Intent intent = new IntentManager().loginToHomeIntent(LoginActivity.this);
                if (progDailog.isShowing()) {
                    progDailog.dismiss();
                }

                LoginActivity.this.startActivity(intent);
                finish();
            }else{
                if (progDailog.isShowing()) {
                    progDailog.dismiss();
                }
                //INVALID USERNAME/PASSWORD
                Toast.makeText(LoginActivity.this, (String) this.response_json.get("error"),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
