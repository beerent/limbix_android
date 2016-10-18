package com.krux.activity.preLogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brent.helloworld.R;
import com.krux.json.JSONBuilder;
import com.krux.net.Client;
import com.krux.util.IntentManager;

import org.json.simple.JSONObject;

public class RegisterActivity extends Activity {
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onClickSubmit(View view) {
        EditText first_name_text = (EditText) findViewById(R.id.first_name);
        EditText last_name_text = (EditText) findViewById(R.id.last_name);
        EditText username_text = (EditText) findViewById(R.id.username);
        EditText password1_text = (EditText) findViewById(R.id.password1);
        EditText password2_text = (EditText) findViewById(R.id.password2);
        EditText email_text = (EditText) findViewById(R.id.email);

        String first_name = first_name_text.getText().toString();
        String last_name  = last_name_text.getText().toString();
        String username   = username_text.getText().toString();
        String password1  = password1_text.getText().toString();
        String password2  = password2_text.getText().toString();
        String email      = email_text.getText().toString();

        new ClientThread().execute(first_name, last_name, username, password1, password2, email);
    }

    public void onClickCancel(View view) {
        finish();
    }











    private class ClientThread extends AsyncTask<String, Void, Boolean> {
        private Client client;
        private JSONObject response_json;
        private String username, password;
        private ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            progDailog = new ProgressDialog(RegisterActivity.this);
            progDailog.setMessage("Validating...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        protected Boolean doInBackground(String... str) {

            String first_name = str[0];
            String last_name = str[1];
            String username = str[2];
            String password1 = str[3];
            String password2 = str[4];
            String email = str[5];



            JSONBuilder json_builder = new JSONBuilder();
            String outgoing_msg = json_builder.buildRegisterRequest(first_name,
                    last_name,
                    username,
                    password1,
                    password2,
                    email);

            this.client = new Client();
            client.receive();                      // OK
            this.client.send(outgoing_msg);        // SEND LOGIN REQUEST
            String return_str = client.receive();  // RECEIVE RESPONSE

            JSONObject response_json = json_builder.getJSONObject(return_str);
            response_json = (JSONObject) response_json.get("response");
            this.response_json = response_json;
            Long op = (Long) response_json.get("op");
            return op == 0;
        }

        protected void onPostExecute(Boolean result) {

            System.out.println("result:" + result);
            JSONBuilder jb = new JSONBuilder();

            if (result){

                if (progDailog.isShowing()) {
                    progDailog.dismiss();
                }
                Toast.makeText(RegisterActivity.this, (String) response_json.get("success"),
                        Toast.LENGTH_LONG).show();
                finish();
            }else{
                if (progDailog.isShowing()) {
                    progDailog.dismiss();
                }
                //INVALID USERNAME/PASSWORD
                Toast.makeText(RegisterActivity.this, (String) response_json.get("error"),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
