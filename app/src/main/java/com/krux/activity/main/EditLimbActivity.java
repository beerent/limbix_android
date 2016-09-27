package com.krux.activity.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brent.helloworld.R;
import com.krux.json.JSONBuilder;
import com.krux.net.Client;
import com.krux.session.ActiveSession;
import org.json.simple.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

public class EditLimbActivity extends Activity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener{
    private boolean limb_updated;

    private Button submit_button;
    private Button clear_due_date;
    private Button cancel_edit_limb_button;

    private EditText due_date_text;
    private EditText edit_limb_text;

    private int due_day;
    private int due_month;
    private int due_year;

    private int limb_id;

    private JSONObject response_json;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_limb);
        this.setFinishOnTouchOutside(false);

        String limb_text = getIntent().getExtras().getString("limb_text");
        String limb_due = getIntent().getExtras().getString("limb_due");

        this.limb_id = getIntent().getExtras().getInt("limb_id");
//        this.limb_position = getIntent().getExtras().getInt("limb_position");

        submit_button = (Button) findViewById(R.id.save_edit_limb_button);
        submit_button.setOnClickListener(this);

        clear_due_date = (Button) findViewById(R.id.clear_edit_due_date_button);
        clear_due_date.setOnClickListener(this);

        cancel_edit_limb_button = (Button) findViewById(R.id.cancel_edit_limb_button);
        cancel_edit_limb_button.setOnClickListener(this);

        edit_limb_text = (EditText) findViewById(R.id.edit_limb_text);
        edit_limb_text.setText(limb_text);

        due_date_text = (EditText) findViewById(R.id.edit_due_date_text);
        if(due_date_text != null &&
                !due_date_text.getText().toString().equals("null") &&
                !due_date_text.getText().toString().equals("")) {
            System.out.println("due date text: " + due_date_text.getText().toString());
            due_date_text.setText(limb_due);
        }
        due_date_text.setKeyListener(null);
        due_date_text.setOnClickListener(this);

        this.limb_updated = false;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.save_edit_limb_button:
                submitButtonOnClick();
                break;
            case R.id.edit_due_date_text:
                setDueDateClick();
                break;
            case R.id.clear_edit_due_date_button:
                clearDueDateClick();
                break;
            case R.id.cancel_edit_limb_button:
                closeAddLimbWindow();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.due_year = year;
        this.due_month = monthOfYear + 1;
        this.due_day = dayOfMonth;
        updateDisplay();
    }

    private void updateDisplay() {
        String month = "" + due_month;
        if(month.length() == 1)
            month = "0" + month;

        String day = "" + due_day;
        if(day.length() == 1)
            day = "0" + day;
        due_date_text.setText(new StringBuilder()
                .append("due date: ")
                .append(due_year).append("-").append(month).append("-").append(day));
    }

    private void submitButtonOnClick(){
        String due_date = "" + this.due_year + "-" + this.due_month + "-" + this.due_day;
        System.out.println("sending due date: " + due_date);
        new ClientThread().execute("" + this.limb_id, edit_limb_text.getText().toString(), due_date);
        try  {

            //hide text window
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    private void setDueDateClick(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Dialog_MinWidth, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void clearDueDateClick(){
        due_date_text.setText("");
        due_year = -1;
        due_month = -1;
        due_day = -1;
    }

    private void closeAddLimbWindow(){
        if(this.limb_updated)
            LimbsQueryFragment.refresh();
        finish();
    }











    private class ClientThread extends AsyncTask<String, Void, Boolean> {
        private Client client;
        private ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            progDailog = new ProgressDialog(EditLimbActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        protected Boolean doInBackground(String... str) {
            JSONBuilder json_builder = new JSONBuilder();
            String request_json = getRequestJSON(str[0], str[1], str[2]);
            this.client = new Client();
            String response = client.handleTransaction(request_json);
            response_json = json_builder.getJSONObject(response);
            response_json = (JSONObject) response_json.get("response");
            System.out.println("response: " + response_json);
            Long op = (Long) response_json.get("op");
            if(op == 0){
                return true;
            }
            return false;
        }

        protected void onPostExecute(Boolean b) {
            if (progDailog.isShowing())
                progDailog.dismiss();

            if(b) {
                ActiveSession.setRefreshLimbList(true);
                Toast.makeText(EditLimbActivity.this, (String) response_json.get("success"),
                        Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(EditLimbActivity.this, (String) response_json.get("error"),
                        Toast.LENGTH_LONG).show();
            }

            limb_updated = true;
            closeAddLimbWindow();
        }

        private String getRequestJSON(String reminder_id, String reminder, String due_date){
            if(!ActiveSession.isLoggedIn()){
                System.out.println("user is not logged in");
                //send back to login screen
            }

            String username = ActiveSession.getUsername();
            String password = ActiveSession.getPassword();

            JSONObject return_json = new JSONObject();
            JSONObject request_json = new JSONObject();
            request_json.put("username", username);
            request_json.put("password", password);
            request_json.put("reminder_id", reminder_id);
            request_json.put("type", "update_reminder");
            request_json.put("reminder", reminder);
            request_json.put("due_date", due_date);

            return_json.put("request", request_json);
            return return_json.toString();
        }
    }
}
