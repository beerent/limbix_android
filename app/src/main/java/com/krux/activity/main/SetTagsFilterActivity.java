package com.krux.activity.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;

import com.example.brent.helloworld.R;
import com.krux.json.JSONBuilder;
import com.krux.net.Client;
import com.krux.session.ActiveSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetTagsFilterActivity extends Activity implements View.OnClickListener{
    private ListView tags_list_view;
    private AddTagListViewAdapter tag_list_view_adapter;
    private SpinnerAdapter spinner_adapter;

    private Spinner tags_spinner;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_tags_filter);

        tags_list_view = (ListView) findViewById(R.id.add_tag_list_view);
        tag_list_view_adapter = new AddTagListViewAdapter(this, ActiveSession.getFilterTags());
        tags_list_view.setAdapter(tag_list_view_adapter);

        tags_spinner = (Spinner) findViewById(R.id.tags_spinner);

        tags_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tag_text = (String) parent.getItemAtPosition(position);
                if(tag_text.equals("[select a tag]..."))
                    return;

                ActiveSession.addFilterTag(tag_text);
                LimbsFilterFragment.setStrings();
                finish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new PopulateTagSpinner().execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
        }
    }

    private class PopulateTagSpinner extends AsyncTask<String, Void, Boolean> {
        private Client client;
        private ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            progDailog = new ProgressDialog(SetTagsFilterActivity.this);
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
            parseAndPopulate(response);
            return true;
        }

        private void parseAndPopulate(String response){
            JSONObject tags = new JSONBuilder().getJSONObject(response);
            final JSONArray tags_json_array = (JSONArray) tags.get("tags");
            if(tags_json_array == null)
                return;

            //int tag_id;
            String tag;
            JSONObject tag_json;

            List<String> tags_alist = new ArrayList<String>();
            for(int i = 0; i < tags_json_array.size(); i++){
                tag_json = (JSONObject) tags_json_array.get(i);
                //tag_id = Integer.parseInt((String) tag_json.get("tag_id"));
                tag = (String) tag_json.get("tag");

                if(!ActiveSession.filterTagsContainsTag(tag))
                    tags_alist.add(tag);
            }
            if(tags_alist.size() == 0)
                tags_alist.add("no tags available");
            else
                tags_alist.add("[select a tag]...");

            spinner_adapter = new SpinnerAdapter(
                    SetTagsFilterActivity.this,
                    tags_alist,
                    android.R.layout.simple_spinner_item);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tags_spinner.setPrompt("Select A Tag!");
                    tags_spinner.setAdapter(spinner_adapter);
                    tags_spinner.setSelection(spinner_adapter.getCount());

                    if (spinner_adapter.getCount() == 0)
                        tags_spinner.setEnabled(false);
                }
            });
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
            request_json.put("type", "get_tags");
            //adding filters

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
    }
}
