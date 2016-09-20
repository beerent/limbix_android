package com.krux.activity.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brent.helloworld.R;
import com.krux.json.JSONBuilder;
import com.krux.limb.Limb;
import com.krux.net.Client;
import com.krux.session.ActiveSession;
import com.krux.util.ClientManager;

import org.json.simple.JSONObject;

public class LimbListViewAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList <Limb> data;
    private LayoutInflater inflater = null;

    public LimbListViewAdapter(Activity activity, ArrayList<Limb> data) {
        this.activity = activity;
        this.data=data;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void removeLimb(int position, int limb_id){
        if(data.get(position).getLimbID() == limb_id) {
            data.remove(position);
            this.notifyDataSetChanged();
        }
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        final Limb limb = data.get(position);

        TextView limb_view = (TextView) vi.findViewById(R.id.limb);
        TextView created_date_view = (TextView) vi.findViewById(R.id.created_date);
        TextView due_date_view = (TextView) vi.findViewById(R.id.due_date);
        final CheckBox complete_checkbox = (CheckBox) vi.findViewById(R.id.row_checkbox);

        final View final_vi = vi;
        complete_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isPressed())
                    new ClientThread().execute("" + limb.getLimbID(), "" + b, "" + position);

                Boolean complete_filter_setting = ActiveSession.getCompleted();
                if(complete_filter_setting != null){
                    //if not null it must be true or false,
                    //since it can be clicked, the opposite is not in view, so remove
                    //data.remove(position);
                    //final_vi.invalidate();
                }
            }
        });

        // Setting all values in listview
        System.out.println(limb.getLimbMessage() + " " + limb.getDueDate());
        limb_view.setText(limb.getLimbMessage());
        created_date_view.setText("created: " + limb.getCreatedDate());
        if(limb.getDueDate() != null && !limb.getDueDate().equals("null"))
            due_date_view.setText("due: " + limb.getDueDate());
        complete_checkbox.setChecked(limb.isCompleted());

        return vi;
    }







    private class ClientThread extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... str) {
            boolean checked = str[1].equals("true");
            String check_op = "0";
            if(checked)
                check_op = "1";

            ClientManager cm = new ClientManager();
            JSONObject response_json = cm.updateLimb("update_reminder", str[0], "complete", check_op);
            Long op = (Long) response_json.get("op");
            if(op == 0){
                int position = Integer.parseInt(str[2]);
                data.get(position).setCompleted(checked);
            }

            return op == 0;
        }
    }
}