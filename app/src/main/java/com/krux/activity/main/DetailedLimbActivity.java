package com.krux.activity.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brent.helloworld.R;
import com.krux.util.ClientManager;

import org.json.simple.JSONObject;

public class DetailedLimbActivity extends Activity implements View.OnClickListener{

    private int limb_id;
    private String limb_text;
    private int limb_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detailed_limb);

        this.limb_text = getIntent().getExtras().getString("limb_text");
        this.limb_id = getIntent().getExtras().getInt("limb_id");
        this.limb_position = getIntent().getExtras().getInt("limb_position");

        TextView text = (TextView) findViewById(R.id.limb_text_box);
        text.setText(limb_text);

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
        }
    }

    private void deleteLimbClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Delete this Limb?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new ClientThread().execute("delete");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void editLimbClick(){

    }

    private class ClientThread extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... str) {
            ClientManager cm = new ClientManager();

            JSONObject response_json = null;
            if(str[0].equals("delete")){
                response_json = cm.updateLimb("update_reminder", ""+ limb_id, "deleted", "1");
            }
            Long op = (Long) response_json.get("op");
            if(op == 0){

                finish();
            }

            return op == 0;
        }
    }
}
