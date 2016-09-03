package com.krux.activity.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.brent.helloworld.R;
import com.krux.session.ActiveSession;

public class SetTagsFilterActivity extends Activity  implements View.OnClickListener{
    EditText text;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_tags_filter);

        button = (Button) findViewById(R.id.set_tags_filter_button);
        button.setOnClickListener(this);

        text = (EditText) findViewById(R.id.tags_text_box);
        text.setText(ActiveSession.getFilterTags());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.set_tags_filter_button:
                new OfflineThread().execute(text.getText().toString());
                break;
        }
    }

    private class OfflineThread extends AsyncTask<String, Void, Boolean> {
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
            ActiveSession.setFilterTags(str[0]);
            return true;
        }

        protected void onPostExecute(Boolean b) {
            if (progDailog.isShowing())
                progDailog.dismiss();
            LimbsFilterFragment.setStrings();
            finish();

        }
    }
}
