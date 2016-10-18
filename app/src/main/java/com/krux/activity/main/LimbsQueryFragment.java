package com.krux.activity.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brent.helloworld.R;
import com.krux.json.JSONBuilder;
import com.krux.limb.Limb;
import com.krux.net.Client;
import com.krux.session.ActiveSession;
import com.krux.util.ClientManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LimbsQueryFragment extends Fragment implements View.OnClickListener{

    private static ListView list;
    private static LimbListViewAdapter adapter;
    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_query, container, false);

        FloatingActionButton add_limb_button =
                (FloatingActionButton) view.findViewById(R.id.add_limb_button);
        add_limb_button.setOnClickListener(this);

        new ClientThread().execute();

        return view;
    }

    public static void refresh() {
        if(ActiveSession.filterUpdated()){
            new ClientThread().execute();
            ActiveSession.setFilterUpdated(false);
        }else if(ActiveSession.refreshLimbList()){
            new ClientThread().execute();
            ActiveSession.setRefreshLimbList(false);
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refresh();
        }
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.add_limb_button:
                addLimbClicked();
                break;
        }
    }

    private void addLimbClicked(){
        Intent intent=new Intent(getActivity().getApplicationContext(), AddLimbActivity.class);
        startActivity(intent);
    }



    private static class ClientThread extends AsyncTask<String, Void, Boolean> {
        private Client client;
        private Activity activity;
        //private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        private ProgressDialog progDailog;
        private ArrayList<Limb> limbs;


        @Override
        protected void onPreExecute() {
            this.activity = (Activity) view.getContext();
            progDailog = new ProgressDialog(activity);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        protected Boolean doInBackground(String... str) {
            String request_json = getRequestJSON(str);
            this.client = new Client();
            String response = client.handleTransaction(request_json);
            loadLimbs(response);
            return true;
        }

        protected void onPostExecute(Boolean b) {
            if(b) {
                if (progDailog.isShowing())
                    progDailog.dismiss();
            }

        }

        private String getRequestJSON(String [] filters){
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
            request_json.put("type", "get");
            //adding filters
            if(ActiveSession.getFilterTags() != null)
                request_json.put("tags", ActiveSession.getFilterTagsAsString());

            if(ActiveSession.getBeforeCreatedDate() != null)
                request_json.put("created_before", ActiveSession.getBeforeCreatedDate());
            if(ActiveSession.getOnCreatedDate() != null)
                request_json.put("created", ActiveSession.getOnCreatedDate());
            if(ActiveSession.getAfterCreatedDate() != null)
                request_json.put("created_after", ActiveSession.getAfterCreatedDate());

            if(ActiveSession.getBeforeDueDate() != null)
                request_json.put("due_date_before", ActiveSession.getBeforeDueDate());
            if(ActiveSession.getOnDueDate() != null)
                request_json.put("due_date", ActiveSession.getOnDueDate());
            if(ActiveSession.getAfterDueDate() != null)
                request_json.put("due_date_after", ActiveSession.getAfterDueDate());

            if(ActiveSession.getCompleted() !=  null){
                String completed = "1";
                if(ActiveSession.getCompleted() == false)
                    completed = "0";

                request_json.put("complete", completed);
            }

            request_json.put("deleted", "0");
            return_json.put("request", request_json);
            return return_json.toString();
        }


        private void loadLimbs(String limbs_string){
            JSONBuilder jb = new JSONBuilder();
            System.out.println(limbs_string);
            JSONObject limbs_json = jb.getJSONObject(limbs_string);

            limbs = getLimbsFromJSON(limbs_json);

            list=(ListView)view.findViewById(R.id.list);

            // Getting adapter by passing xml data ArrayList
            adapter = new LimbListViewAdapter(this.activity, limbs);
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list.setAdapter(adapter);
                }
            });

            // Click event for single list row
            list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(activity.getApplicationContext(), DetailedLimbActivity.class);
                    intent.putExtra("limb_text", limbs.get(position).getLimbMessage());
                    intent.putExtra("limb_created", limbs.get(position).getCreatedDate());
                    intent.putExtra("limb_due", limbs.get(position).getDueDate());
                    intent.putExtra("limb_id", limbs.get(position).getLimbID());
                    intent.putExtra("limb_position", position);
                    activity.startActivity(intent);
                }
            });
        }

        private ArrayList<Limb> getLimbsFromJSON(JSONObject limbs_json){
            ArrayList<Limb> limbs = new ArrayList<Limb>();
            JSONArray limbs_json_array = (JSONArray) limbs_json.get("reminders");
            if(limbs_json_array == null)
                return limbs;

            int limb_id;
            String limb_message, created, due, complete_str;
            boolean complete;
            JSONObject limb_json;
            Limb limb;
            for(int i = 0; i < limbs_json_array.size(); i++){
                limb_json = (JSONObject) limbs_json_array.get(i);
                limb_id = Integer.parseInt((String) limb_json.get("reminder_id"));
                limb_message = (String) limb_json.get("reminder");
                created = (String) limb_json.get("created_date");
                due = (String) limb_json.get("due_date");
                complete_str = (String) limb_json.get("complete");
                complete = complete_str != null && complete_str.equals("true");
                limb = new Limb(limb_id, limb_message, created, due, complete);
                limb.cleanDateStrings();
                limbs.add(limb);
            }

            return limbs;
        }
    }

    public static class DetailedLimbActivity extends Activity implements View.OnClickListener{

        private int limb_id;
        private String limb_text, limb_created, limb_due;
        private int limb_position;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_detailed_limb);

            this.limb_text = getIntent().getExtras().getString("limb_text");
            this.limb_created = getIntent().getExtras().getString("limb_created");
            this.limb_due = getIntent().getExtras().getString("limb_due");

            this.limb_id = getIntent().getExtras().getInt("limb_id");
            this.limb_position = getIntent().getExtras().getInt("limb_position");

            TextView limb_text_view = (TextView) findViewById(R.id.detailed_limb_text_box);
            TextView limb_created_date_view = (TextView) findViewById(R.id.detailed_view_created_date);
            TextView limb_due_date_view = (TextView) findViewById(R.id.detailed_view_due_date);

            View rel_layout = (View) findViewById(R.id.detailed_text_top_layout);
            rel_layout.setOnClickListener(this);


            limb_text_view.setText(limb_text);
            limb_created_date_view.setText("created: " + limb_created);
            if(limb_due != null && !limb_due.equals("null"))
                limb_due_date_view.setText("due: " + limb_due);
            else
                limb_due_date_view.setText("no due date");

            Button delete_limb = (Button) findViewById(R.id.delete_limb);
            Button edit_limb   = (Button) findViewById(R.id.edit_limb);

            delete_limb.setOnClickListener(this);
            edit_limb.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.delete_limb:
                    deleteLimbClick();
                    break;
                case R.id.edit_limb:
                    editLimbClick();
                    break;
                case R.id.detailed_text_top_layout:
                    System.out.println("layout clicked !");
                    break;
            }
        }

        private void deleteLimbClick(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder
                    .setMessage("Delete this Limb?")
                    .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            new DetailedLimbClientThread().execute("delete");
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                            finish();
                        }
                    })
                    .show();
        }

        private void editLimbClick(){
            Intent intent = new Intent(this, EditLimbActivity.class);
            intent.putExtra("limb_text", this.limb_text);
            intent.putExtra("limb_due", this.limb_due);
            intent.putExtra("limb_id", this.limb_id);
            intent.putExtra("limb_position", this.limb_position);
            startActivity(intent);
            finish();
        }

        private class DetailedLimbClientThread extends AsyncTask<String, Void, Boolean> {
            private ProgressDialog progDailog;

            protected void onPreExecute() {
                progDailog = new ProgressDialog(DetailedLimbActivity.this);
                progDailog.setMessage("Deleting...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(true);
                progDailog.show();

            }

            protected Boolean doInBackground(String... str) {
                ClientManager cm = new ClientManager();

                JSONObject response_json = null;
                if(str[0].equals("delete")){
                    response_json = cm.updateLimb("update_reminder", ""+ limb_id, "deleted", "1");
                }
                Long op = (Long) response_json.get("op");
                return op == 0;
            }

            protected void onPostExecute(Boolean result) {
                if(result){
                    //list.removeViewAt(limb_position);
                    adapter.removeLimb(limb_position, limb_id);
                    if (progDailog.isShowing()) {
                        progDailog.dismiss();
                    }
                    finish();
                }
            }
        }
    }

    public static class EditLimbActivity extends Activity implements View.OnClickListener,
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
        private int limb_position;

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
            this.limb_position = getIntent().getExtras().getInt("limb_position");

            submit_button = (Button) findViewById(R.id.save_edit_limb_button);
            submit_button.setOnClickListener(this);

            clear_due_date = (Button) findViewById(R.id.clear_edit_due_date_button);
            clear_due_date.setOnClickListener(this);

            cancel_edit_limb_button = (Button) findViewById(R.id.cancel_edit_limb_button);
            cancel_edit_limb_button.setOnClickListener(this);

            edit_limb_text = (EditText) findViewById(R.id.edit_limb_text);
            edit_limb_text.setText(limb_text);

            due_date_text = (EditText) findViewById(R.id.edit_due_date_text);
            if(limb_due.equals("null"))
                due_date_text.setText("no due date");
            else
                due_date_text.setText("due: " + limb_due);

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
                    .append("due: ")
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
            private Long present;

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
                present = (Long) response_json.get("present");
                if(op == 0){
                    return true;
                }
                return false;
            }

            protected void onPostExecute(Boolean b) {
                if (progDailog.isShowing())
                    progDailog.dismiss();

                if(b) {
                    if(present == 0)
                        adapter.removeLimb(limb_position, limb_id);

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

}
