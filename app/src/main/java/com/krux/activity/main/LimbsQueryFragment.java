package com.krux.activity.main;

import java.util.ArrayList;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.brent.helloworld.R;
import com.krux.json.JSONBuilder;
import com.krux.limb.Limb;
import com.krux.net.Client;
import com.krux.session.ActiveSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LimbsQueryFragment extends Fragment{

    private ListView list;
    private LimbListViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_query, container, false);
        new ClientThread().execute();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(ActiveSession.filterUpdated()){
                new ClientThread().execute();
            }
        }
    }














    private class ClientThread extends AsyncTask<String, Void, Boolean> {
        private Client client;
        //private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
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
            String request_json = getRequestJSON(str);
            this.client = new Client();
            String response = client.handleTransaction(request_json);
            loadLimbs(response);
            return true;
        }

        protected void onPostExecute(Boolean b) {
            ActiveSession.setFilterUpdated(false);
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
            if(ActiveSession.getAfterCreatedDate() != null)
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

            list=(ListView)getActivity().findViewById(R.id.list);

            // Getting adapter by passing xml data ArrayList
            adapter = new LimbListViewAdapter(getActivity(), limbs);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list.setAdapter(adapter);
                }
            });

            // Click event for single list row
            list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), DetailedLimbActivity.class);
                    intent.putExtra("limb_text", limbs.get(position).getLimbMessage());
                    intent.putExtra("limb_id", limbs.get(position).getLimbID());
                    intent.putExtra("limb_position", position);
                    startActivityForResult(intent, 1);
                }
            });
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (resultCode != Activity.RESULT_OK)
                return;

            if (requestCode == PICK_FROM_FILE) {
                mImageCaptureUri = data.getData();
                // mPath = getRealPathFromURI(mImageCaptureUri); //from Gallery

                if (mPath == null)
                    mPath = mImageCaptureUri.getPath(); // from File Manager

                if (mPath != null)
                    bitmap = BitmapFactory.decodeFile(mPath);
            } else {
                mPath = mImageCaptureUri.getPath();
                bitmap = BitmapFactory.decodeFile(mPath);
            }
            mImageView.setImageBitmap(bitmap);
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
}
