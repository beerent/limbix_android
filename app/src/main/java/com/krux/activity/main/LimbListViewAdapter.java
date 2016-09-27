package com.krux.activity.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.brent.helloworld.R;
import com.krux.limb.Limb;
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
            vi = inflater.inflate(R.layout.query_list_row, null);

        Limb limb = data.get(position);
        TextView limb_view = (TextView) vi.findViewById(R.id.limb);
        TextView created_date_view = (TextView) vi.findViewById(R.id.created_date);
        TextView due_date_view = (TextView) vi.findViewById(R.id.due_date);
        CheckBox complete_checkbox = (CheckBox) vi.findViewById(R.id.row_checkbox);

        complete_checkbox.setTag(position);
        complete_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    int position = (Integer) compoundButton.getTag();
                    new updateCompleteThread().execute("" + data.get(position).getLimbID(), "" + b, "" + position);

                }
            }
        });

        // Setting all values in listview

        limb_view.setText(limb.getLimbMessage());
        created_date_view.setText("created: " + limb.getCreatedDate());

        if(limb.getDueDate() != null && !limb.getDueDate().equals("null"))
            due_date_view.setText("due: " + limb.getDueDate());
        else
            due_date_view.setText("");

        complete_checkbox.setChecked(limb.isCompleted());

        return vi;
    }







    private class updateCompleteThread extends AsyncTask<String, Void, Boolean> {
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