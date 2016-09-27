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

public class AddTagListViewAdapter extends BaseAdapter {

    private ArrayList <String> data;
    private LayoutInflater inflater = null;

    public AddTagListViewAdapter(Activity activity, ArrayList<String> data) {
        this.data=data;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.detailed_tag_item, null);

        final String tag = data.get(position);

        TextView tag_view = (TextView) vi.findViewById(R.id.detailed_new_tag_text);

        // Setting all values in listview
        tag_view.setText(tag);
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
            }

            return op == 0;
        }
    }
}