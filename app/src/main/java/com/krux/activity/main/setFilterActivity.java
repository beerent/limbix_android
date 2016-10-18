package com.krux.activity.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.brent.helloworld.R;
import com.krux.json.JSONBuilder;
import com.krux.limb.Filter;
import com.krux.net.Client;
import com.krux.session.ActiveSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class SetFilterActivity extends Activity implements View.OnClickListener{

    private ListView list;
    private FiltersListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_filter);

        new PopulateFilterListView().execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){

        }
    }

    private class PopulateSelectedFilter extends AsyncTask<String, Void, Boolean> {
        private Client client;
        private ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            progDailog = new ProgressDialog(SetFilterActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
//            progDailog.show();

        }

        protected Boolean doInBackground(String... str) {
            String request_json = getRequestJSON(str[0]);
            this.client = new Client();
            String response = client.handleTransaction(request_json);
            parseAndPopulate(response);
            return true;
        }

        private void parseAndPopulate(String response){
            JSONObject filter = new JSONBuilder().getJSONObject(response);
            JSONObject filter_json = (JSONObject) filter.get("response");
            if(filter_json == null)
                return;

            int filter_id;
            String filter_name;
            String tags;
            String created_before;
            String created;
            String created_after;
            String due_before;
            String due;
            String due_after;
            Boolean complete;
            Boolean deleted;

            filter_id = ((Long) filter_json.get("filter_id")).intValue();
            filter_name = (String) filter_json.get("filter_name");
            tags = (String) filter_json.get("tags");
            created_before = (String) filter_json.get("created_before");
            created = (String) filter_json.get("created");
            created_after = (String) filter_json.get("created_after");
            due_before = (String) filter_json.get("due_before");
            due = (String) filter_json.get("due");
            due_after = (String) filter_json.get("due_after");
            complete = (Boolean) filter_json.get("complete");
            deleted = (Boolean) filter_json.get("deleted");

            ActiveSession.clearAllFilters();

            if(tags !=null && !tags.equals("null")){
                ArrayList<String> tag_list = new ArrayList<String>(Arrays.asList(tags.split(",")));
                ActiveSession.setFilterTags(tag_list);
            }

            if(created_before !=null && !created_before.equals("null"))
                ActiveSession.setBeforeCreatedDate(created_before);
            if(created !=null && !created.equals("null"))
                ActiveSession.setOnCreatedDate(created);
            if(created_after !=null && !created_after.equals("null"))
                ActiveSession.setAfterCreatedDate(created_after);

            if(due_before !=null && !due_before.equals("null"))
                ActiveSession.setBeforeDueDate(due_before);
            if(due !=null && !due.equals("null"))
                ActiveSession.setBeforeDueDate(due);
            if(due_after !=null && !due_after.equals("null"))
                ActiveSession.setBeforeDueDate(due_after);

            if(complete != null)
                ActiveSession.setCompleted(complete);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LimbsFilterFragment.setStrings();
                }
            });
            //add filters to ActiveSession, then setStrings
        }

        protected void onPostExecute(Boolean b) {
            if(b) {
                if (progDailog.isShowing())
                    progDailog.dismiss();
            }
            finish();

        }

        private String getRequestJSON(String filter_id){
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
            request_json.put("type", "get_filter");
            request_json.put("filter_id", filter_id);
            return_json.put("request", request_json);
            return return_json.toString();
        }
    }

    private class PopulateFilterListView extends AsyncTask<String, Void, Boolean> {
        private Client client;
        private ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            progDailog = new ProgressDialog(SetFilterActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        protected Boolean doInBackground(String... str) {
            String request_json = getRequestJSON();
            this.client = new Client();
            String response = client.handleTransaction(request_json);
            parseAndPopulate(response);
            return true;
        }

        private void parseAndPopulate(String response){
            JSONObject filters = new JSONBuilder().getJSONObject(response);
            JSONArray filters_json_array = (JSONArray) filters.get("filters");
            if(filters_json_array == null)
                return;

            int filter_id;
            String filter_name;
            JSONObject filter_json;

            ArrayList<Filter> filters_array = new ArrayList<Filter>();
            for(int i = 0; i < filters_json_array.size(); i++){
                filter_json = (JSONObject) filters_json_array.get(i);
                filter_id = ((Long) filter_json.get("filter_id")).intValue();
                filter_name = (String) filter_json.get("filter_name");

                if(filter_name.equals("[custom]"))
                    continue;
                filters_array.add(new Filter(filter_name, filter_id));
            }

            if(filters_array.size() == 0){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SetFilterActivity.this, "You have no filters saved.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }

            ActiveSession.setFilters(filters_array);
            list = (ListView) findViewById(R.id.available_filter_list_view);

            // Getting adapter by passing xml data ArrayList
            adapter = new FiltersListViewAdapter(SetFilterActivity.this, filters_array);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list.setAdapter(adapter);
                }
            });

            // Click event for single list row
            list.setClickable(true);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Filter f = adapter.getItem(position);
                    new PopulateSelectedFilter().execute("" + f.getId());
                    finish();
                }
            });

        }



        protected void onPostExecute(Boolean b) {
            if(b) {
                if (progDailog.isShowing())
                    progDailog.dismiss();
            }


        }

        private String getRequestJSON(){
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
            request_json.put("type", "get_filters_meta");
            return_json.put("request", request_json);
            return return_json.toString();
        }
    }
}