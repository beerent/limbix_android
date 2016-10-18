package com.krux.activity.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brent.helloworld.R;
import com.krux.json.JSONBuilder;
import com.krux.net.Client;
import com.krux.session.ActiveSession;

import org.json.simple.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class LimbsFilterFragment extends Fragment implements View.OnClickListener{
    private static TextView tag_filter_message;
    private static TextView created_date_filter_message;
    private static TextView due_date_filter_message;
    private static TextView completed_filter_message;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_filter, container, false);
        tag_filter_message = (TextView) view.findViewById(R.id.tag_filter_message);
        created_date_filter_message = (TextView) view.findViewById(R.id.created_date_filter_message);
        due_date_filter_message = (TextView) view.findViewById(R.id.due_date_filter_message);
        completed_filter_message = (TextView) view.findViewById(R.id.completed_filter_message);

        /*****************/
        /*  Set Strings  */
        /*****************/
        setStrings();

        view.findViewById(R.id.tag_filter_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_tag_filter_button).setOnClickListener(this);
        view.findViewById(R.id.created_date_filter_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_created_date_filter_button).setOnClickListener(this);
        view.findViewById(R.id.due_date_filter_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_due_date_filter_button).setOnClickListener(this);
        view.findViewById(R.id.completed_filter_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_completed_filter_button).setOnClickListener(this);
        view.findViewById(R.id.save_filter_button).setOnClickListener(this);
        view.findViewById(R.id.load_saved_filter).setOnClickListener(this);
        view.findViewById(R.id.clear_all_filters).setOnClickListener(this);

        return view;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setStrings();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tag_filter_button:
                applyTagFilterClick();
                break;

            case R.id.cancel_tag_filter_button:
                clearTagFiltersClick();
                break;

            case R.id.created_date_filter_button:
                applyCreatedDateFilterClick();
                break;

            case R.id.cancel_created_date_filter_button:
                long rx = TrafficStats.getTotalRxBytes() - ActiveSession.start_rx;
                long tx = TrafficStats.getTotalTxBytes() - ActiveSession.start_tx;
                Toast.makeText(getActivity(),"" + rx + " " + tx,
                        Toast.LENGTH_SHORT).show();
                clearCreatedDateFilterClick();
                break;

            case R.id.due_date_filter_button:
                applyDueDateFilterClick();
                break;

            case R.id.cancel_due_date_filter_button:
                clearDueDateFilterClick();
                break;

            case R.id.completed_filter_button:
                applyCompletedFilterClick();
                break;

            case R.id.cancel_completed_filter_button:
                clearCompletedFilterClick();
                break;

            case R.id.save_filter_button:
                saveNewFilterClick();
                break;

            case R.id.load_saved_filter:
                loadSavedFilterClick();
                break;

            case R.id.clear_all_filters:
                clearAllFiltersClick();
                break;
        }
    }

    public static void setStrings(){
        String tag_string = ActiveSession.getFilterTagsAsString();

        String on_created_date = ActiveSession.getOnCreatedDate();
        String before_created_date = ActiveSession.getBeforeCreatedDate();
        String after_created_date = ActiveSession.getAfterCreatedDate();

        String on_due_date = ActiveSession.getOnDueDate();
        String before_due_date = ActiveSession.getBeforeDueDate();
        String after_due_date = ActiveSession.getAfterDueDate();

        Boolean completed = ActiveSession.getCompleted();

        //TAG FILTER
        if(tag_string != null && tag_string.length() > 0){
            tag_filter_message.setText("Tags: " + tag_string);
        }else if(tag_string == null){
            tag_filter_message.setText("Tags: Not Set");
        }

        //CREATED FILTER
        if(on_created_date != null){
            created_date_filter_message.setText("Created On: " + on_created_date);
        }else if(before_created_date != null || after_created_date != null){
            //check if one or both of the other two fields exist
            //if so, set field(s)
            if(before_created_date != null && after_created_date != null){
                created_date_filter_message.setText(("Created Before: " + before_created_date +
                                                    " & Created After: " + after_created_date));
            }else if(before_created_date != null){
                created_date_filter_message.setText("Created Before: " + before_created_date);
            }else{
                created_date_filter_message.setText(("Created After: " + after_created_date));
            }
        }else{
            created_date_filter_message.setText("Created Date: Not Set");
        }

        //DUE DATE FILTER
        if(on_due_date != null){
            due_date_filter_message.setText("Due On: " + on_due_date);
        }else if(before_due_date != null || after_due_date != null){
            //check if one or both of the other two fields exist
            //if so, set field(s)
            if(before_due_date != null && after_due_date != null){
                due_date_filter_message.setText(("Due Before: " + before_due_date +
                        " & Due After: " + after_due_date));
            }else if(before_due_date != null){
                due_date_filter_message.setText("Due Before: " + before_due_date);
            }else{
                due_date_filter_message.setText(("Due After: " + after_due_date));
            }
        }else{
            due_date_filter_message.setText("Due Date: Not Set");
        }

        //COMPLETED FILTER
        if(completed != null){
            String completed_str = "true";
            if(!completed)
                completed_str = "false";

            completed_filter_message.setText("Completed: " + completed_str);
        }else{
            completed_filter_message.setText("Completed: Not Set");
        }
    }

    private void applyTagFilterClick(){
        Intent intent=new Intent(getActivity().getApplicationContext(), SetTagsFilterActivity.class);
        startActivity(intent);
    }

    private void clearTagFiltersClick(){
        ActiveSession.setFilterTags(new ArrayList<String>());
        LimbsFilterFragment.setStrings();
    }

    private void applyCreatedDateFilterClick(){
        Intent intent=new Intent(getActivity().getApplicationContext(), SetCreatedDateFilterActivity.class);
        startActivity(intent);
    }

    private void clearCreatedDateFilterClick(){
        ActiveSession.setBeforeCreatedDate(null);
        ActiveSession.setOnCreatedDate(null);
        ActiveSession.setAfterCreatedDate(null);

        setStrings();
    }

    private void applyDueDateFilterClick(){
        Intent intent=new Intent(getActivity().getApplicationContext(), SetDueDateFilterActivity.class);
        startActivity(intent);
    }

    private void clearDueDateFilterClick(){
        ActiveSession.setBeforeDueDate(null);
        ActiveSession.setOnDueDate(null);
        ActiveSession.setAfterDueDate(null);

        setStrings();
    }

    private void applyCompletedFilterClick(){
        Boolean completed = ActiveSession.getCompleted();

        if(completed == null)
            ActiveSession.setCompleted(true);
        else if(completed == true)
            ActiveSession.setCompleted(false);
        else
            ActiveSession.setCompleted(null);

        setStrings();
    }

    private void clearCompletedFilterClick(){
        ActiveSession.setCompleted(null);
        setStrings();
    }

    private void saveNewFilterClick(){
        TextView name = (TextView) view.findViewById(R.id.save_filter_text);
        new AddNewFilterThread().execute(name.getText().toString());
        name.setText("");
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }


    private void loadSavedFilterClick(){
        Intent intent=new Intent(getActivity().getApplicationContext(), SetFilterActivity.class);
        startActivity(intent);
    }

    private void clearAllFiltersClick(){
        ActiveSession.clearAllFilters();
        setStrings();
    }

    private class AddNewFilterThread extends AsyncTask<String, Void, Boolean> {
        private Client client;
        private ProgressDialog progDailog;
        private JSONObject response_json;

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
            String json = getRequestJSON(str[0]);
            this.client = new Client();
            String response = client.handleTransaction(json);
            response_json = json_builder.getJSONObject(response);
            response_json = (JSONObject) response_json.get("response");
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
                Toast.makeText(getActivity(), (String) response_json.get("success"),
                        Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(getActivity(), (String) response_json.get("error"),
                        Toast.LENGTH_LONG).show();
            }
        }

        private String getRequestJSON(String filter_name){
            if(!ActiveSession.isLoggedIn()){
                System.out.println("user is not logged in");
                //send back to login screen
            }

            String username = ActiveSession.getUsername();
            String password = ActiveSession.getPassword();
            String tag_string = ActiveSession.getFilterTagsAsString();

            String on_created_date = ActiveSession.getOnCreatedDate();
            String before_created_date = ActiveSession.getBeforeCreatedDate();
            String after_created_date = ActiveSession.getAfterCreatedDate();

            String on_due_date = ActiveSession.getOnDueDate();
            String before_due_date = ActiveSession.getBeforeDueDate();
            String after_due_date = ActiveSession.getAfterDueDate();

            Boolean completed = ActiveSession.getCompleted();

            JSONObject return_json = new JSONObject();
            JSONObject request_json = new JSONObject();
            request_json.put("username", username);
            request_json.put("password", password);
            request_json.put("type", "add_filter");

            request_json.put("filter_name", filter_name);

            if(tag_string != null)
                request_json.put("tags", tag_string);

            if(before_created_date != null)
                request_json.put("created_before", before_created_date);
            if(on_created_date != null)
                request_json.put("created", on_created_date);
            if(after_created_date != null)
                request_json.put("created_after", after_created_date);

            if(before_due_date != null)
                request_json.put("due_before", before_due_date);
            if(on_due_date != null)
                request_json.put("due", on_due_date);
            if(after_due_date != null)
                request_json.put("due_after", after_due_date);


            if(completed != null) {
                String completed_str = "0";
                if(completed)
                    completed_str = "1";
                request_json.put("complete", completed_str);
            }
            request_json.put("deleted", "0");
            return_json.put("request", request_json);
            return return_json.toString();
        }
    }
}
