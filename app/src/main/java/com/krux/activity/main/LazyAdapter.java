package com.krux.activity.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.brent.helloworld.R;
import com.krux.limb.Limb;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList <Limb> data;
    private static LayoutInflater inflater = null;

    public LazyAdapter(Activity activity, ArrayList<Limb> data) {
        this.activity = activity;
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

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView limb_view = (TextView) vi.findViewById(R.id.limb);
        TextView created_date_view = (TextView) vi.findViewById(R.id.created_date);
        TextView due_date_view = (TextView) vi.findViewById(R.id.due_date);
        CheckBox complete_checkbox = (CheckBox) vi.findViewById(R.id.row_checkbox);

        Limb limb = data.get(position);

        // Setting all values in listview
        limb_view.setText(limb.getLimbMessage());
        created_date_view.setText("created: " + limb.getCreatedDate());
        due_date_view.setText("due: " + limb.getDueDate());
        complete_checkbox.setChecked(limb.isCompleted());

        return vi;
    }
}