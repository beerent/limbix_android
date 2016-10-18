package com.krux.activity.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.brent.helloworld.R;
import com.krux.limb.Filter;
import com.krux.limb.Limb;
import com.krux.net.Client;
import com.krux.session.ActiveSession;
import com.krux.util.ClientManager;

import org.json.simple.JSONObject;

public class FiltersListViewAdapter extends BaseAdapter {

    private ArrayList<Filter> data;
    private LayoutInflater inflater = null;

    public FiltersListViewAdapter(Activity activity, ArrayList<Filter> data) {
        this.data = data;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Filter getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_set_filter_list_row, null);

        Filter filter = data.get(position);
        TextView filter_name_view = (TextView) vi.findViewById(R.id.available_filter_text);
        Button delete_button = (Button) vi.findViewById(R.id.delete_available_filter);


        filter_name_view.setText(filter.getName());
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteFilter().execute("" + getItem(position).getId());
            }
        });
        return vi;
    }

    private class DeleteFilter extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... str) {
            ClientManager cm = new ClientManager();
            JSONObject resquest_json = cm.deleteFilter(Integer.parseInt(str[0]));
            Client client = new Client();
            client.handleTransaction(resquest_json.toJSONString());
            return true;
        }
    }
}