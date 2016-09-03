package com.krux.activity.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

import com.example.brent.helloworld.R;
import com.krux.session.ActiveSession;

import java.util.Calendar;
import java.util.TimeZone;

public class SetCreatedDateFilterActivity extends Activity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener{

    private int setting_date = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_created_date_filter);

        findViewById(R.id.created_before_button).setOnClickListener(this);
        findViewById(R.id.created_on_button).setOnClickListener(this);
        findViewById(R.id.created_after_button).setOnClickListener(this);


        //setDueDateClick
    }

    @Override
    public void onClick(View v) {
        this.setting_date = v.getId();
        setDateClick();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = "" + year + "-" + monthOfYear + "-" + dayOfMonth;

        if(this.setting_date == R.id.created_before_button){
            ActiveSession.setBeforeCreatedDate(date);
        }else if(this.setting_date == R.id.created_on_button){
            ActiveSession.setOnCreatedDate(date);
        }else if(this.setting_date == R.id.created_after_button){
            ActiveSession.setAfterCreatedDate(date);
        }

        LimbsFilterFragment.setStrings();
        this.setting_date = -1;
    }

    private void setDateClick(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Dialog_MinWidth, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}
