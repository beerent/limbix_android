package com.krux.activity.main;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class LimbsAddFragment extends Fragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener{
    private Button submit_button;
    private Button set_due_date_button;

    private EditText due_date_text;
    private EditText add_limb_text;




    private int due_day;
    private int due_month;
    private int due_year;

    private JSONObject response_json;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        submit_button = (Button) view.findViewById(R.id.add_limb_button);
        submit_button.setOnClickListener(this);

        set_due_date_button = (Button) view.findViewById(R.id.set_due_date_button);
        set_due_date_button.setOnClickListener(this);

        due_date_text = (EditText) view.findViewById(R.id.due_date_text);
        due_date_text.setKeyListener(null);
        return view;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.add_limb_button:
                submitButtonOnClick();
                break;
            case R.id.set_due_date_button:
                setDueDateClick();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.due_year = year;
        this.due_month = monthOfYear;
        this.due_day = dayOfMonth;
        updateDisplay();
    }

    private void updateDisplay() {
        due_date_text.setText(new StringBuilder()
                .append("due date: ")
                .append(due_year).append("-").append(due_month + 1).append("-").append(due_day));
    }

    private void submitButtonOnClick(){
        add_limb_text = (EditText) getActivity().findViewById(R.id.add_limb_text);
        String due_date = "" + this.due_year + "-" + this.due_month + "-" + this.due_day;
        new ClientThread().execute(add_limb_text.getText().toString(), due_date);
    }

    private void setDueDateClick(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(getContext(),
                android.R.style.Theme_Holo_Dialog_MinWidth, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }











    private class ClientThread extends AsyncTask<String, Void, Boolean> {
        private Client client;
        private ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            progDailog = new ProgressDialog(getActivity());
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        protected Boolean doInBackground(String... str) {
            JSONBuilder json_builder = new JSONBuilder();
            String request_json = getRequestJSON(str[0], str[1]);
            this.client = new Client();
            String response = client.handleTransaction(request_json);
            response_json = json_builder.getJSONObject(response);
            response_json = (JSONObject) response_json.get("response");
            Long op = (Long) response_json.get("op");
            return op == 0;
        }

        protected void onPostExecute(Boolean b) {
            if (progDailog.isShowing())
                progDailog.dismiss();
            if(b) {
                Toast.makeText(getActivity(), (String) response_json.get("success"),
                        Toast.LENGTH_LONG).show();
                add_limb_text.setText("");
                due_date_text.setText("");
                due_year = -1;
                due_month = -1;
                due_day = -1;
            }else{
                Toast.makeText(getActivity(), (String) response_json.get("error"),
                        Toast.LENGTH_LONG).show();
            }
        }

        private String getRequestJSON(String reminder, String due_date){
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
            request_json.put("type", "add");
            request_json.put("reminder", reminder);
            request_json.put("due_date", due_date);

            return_json.put("request", request_json);
            return return_json.toString();
        }
    }
}
