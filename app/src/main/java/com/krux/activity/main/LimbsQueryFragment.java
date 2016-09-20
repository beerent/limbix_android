package com.krux.activity.main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
                request_json.put("tags", ActiveSession.getFilterTags());

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

            final ArrayList<Limb> limbs = getLimbsFromJSON(limbs_json);

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
}
