package com.krux.activity.main;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.brent.helloworld.R;
import com.krux.session.ActiveSession;

public class LimbsFilterFragment extends Fragment implements View.OnClickListener{
    private static TextView tag_filter_message;
    private static TextView created_date_filter_message;
    private static TextView due_date_filter_message;
    private static TextView completed_filter_message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        tag_filter_message = (TextView) view.findViewById(R.id.tag_filter_message);
        created_date_filter_message = (TextView) view.findViewById(R.id.created_date_filter_message);
        due_date_filter_message = (TextView) view.findViewById(R.id.due_date_filter_message);
        completed_filter_message = (TextView) view.findViewById(R.id.completed_filter_message);

        /*****************/
        /*  Set Strings  */
        /*****************/
        setStrings();

        view.findViewById(R.id.tag_filter_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_tag_filter_button).setOnClickListener(this);
        view.findViewById(R.id.created_date_filter_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_created_date_filter_button).setOnClickListener(this);
        view.findViewById(R.id.due_date_filter_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_due_date_filter_button).setOnClickListener(this);
        view.findViewById(R.id.completed_filter_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_completed_filter_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tag_filter_button:
                applyTagFilterClick();
                break;

            case R.id.cancel_tag_filter_button:
                clearTagFiltersClick();
                break;

            case R.id.created_date_filter_button:
                applyCreatedDateFilterClick();
                break;

            case R.id.cancel_created_date_filter_button:
                clearCreatedDateFilterClick();
                break;

            case R.id.due_date_filter_button:
                applyDueDateFilterClick();
                break;

            case R.id.cancel_due_date_filter_button:
                clearDueDateFilterClick();
                break;

            case R.id.completed_filter_button:
                applyCompletedFilterClick();
                break;

            case R.id.cancel_completed_filter_button:
                clearCompletedFilterClick();
                break;
        }
    }

    public static void setStrings(){
        String tag_string = ActiveSession.getFilterTags();

        String on_created_date = ActiveSession.getOnCreatedDate();
        String before_created_date = ActiveSession.getBeforeCreatedDate();
        String after_created_date = ActiveSession.getAfterCreatedDate();

        String on_due_date = ActiveSession.getOnDueDate();
        String before_due_date = ActiveSession.getBeforeDueDate();
        String after_due_date = ActiveSession.getAfterDueDate();

        Boolean completed = ActiveSession.getCompleted();

        //TAG FILTER
        if(tag_string != null && tag_string.length() > 0){
            tag_filter_message.setText("Tags: " + tag_string);
        }else if(tag_string == null){
            tag_filter_message.setText("Tags: Not Set");
        }

        //CREATED FILTER
        if(on_created_date != null){
            created_date_filter_message.setText("Created On: " + on_created_date);
        }else if(before_created_date != null || after_created_date != null){
            //check if one or both of the other two fields exist
            //if so, set field(s)
            if(before_created_date != null && after_created_date != null){
                created_date_filter_message.setText(("Created Before: " + before_created_date +
                                                    " & Created After: " + after_created_date));
            }else if(before_created_date != null){
                created_date_filter_message.setText("Created Before: " + before_created_date);
            }else{
                created_date_filter_message.setText(("Created After: " + after_created_date));
            }
        }else{
            created_date_filter_message.setText("Created Date: Not Set");
        }

        //DUE DATE FILTER
        if(on_due_date != null){
            due_date_filter_message.setText("Due On: " + on_due_date);
        }else if(before_due_date != null || after_due_date != null){
            //check if one or both of the other two fields exist
            //if so, set field(s)
            if(before_due_date != null && after_due_date != null){
                due_date_filter_message.setText(("Due Before: " + before_due_date +
                        " & Due After: " + after_due_date));
            }else if(before_due_date != null){
                due_date_filter_message.setText("Due Before: " + before_due_date);
            }else{
                due_date_filter_message.setText(("Due After: " + after_due_date));
            }
        }else{
            due_date_filter_message.setText("Due Date: Not Set");
        }

        //COMPLETED FILTER
        if(completed != null){
            String completed_str = "true";
            if(!completed)
                completed_str = "false";

            completed_filter_message.setText("Completed: " + completed_str);
        }else{
            completed_filter_message.setText("Completed: Not Set");
        }
    }

    private void applyTagFilterClick(){
        Intent intent=new Intent(getActivity().getApplicationContext(), SetTagsFilterActivity.class);
        startActivity(intent);
    }

    private void clearTagFiltersClick(){
        ActiveSession.setFilterTags(null);
        LimbsFilterFragment.setStrings();
    }

    private void applyCreatedDateFilterClick(){
        Intent intent=new Intent(getActivity().getApplicationContext(), SetCreatedDateFilterActivity.class);
        startActivity(intent);
    }

    private void clearCreatedDateFilterClick(){
        ActiveSession.setBeforeCreatedDate(null);
        ActiveSession.setOnCreatedDate(null);
        ActiveSession.setAfterCreatedDate(null);

        setStrings();
    }

    private void applyDueDateFilterClick(){
        Intent intent=new Intent(getActivity().getApplicationContext(), SetDueDateFilterActivity.class);
        startActivity(intent);
    }

    private void clearDueDateFilterClick(){
        ActiveSession.setBeforeDueDate(null);
        ActiveSession.setOnDueDate(null);
        ActiveSession.setAfterDueDate(null);

        setStrings();
    }

    private void applyCompletedFilterClick(){
        Boolean completed = ActiveSession.getCompleted();

        if(completed == null)
            ActiveSession.setCompleted(true);
        else if(completed == true)
            ActiveSession.setCompleted(false);
        else
            ActiveSession.setCompleted(null);

        setStrings();
    }

    private void clearCompletedFilterClick(){
        ActiveSession.setCompleted(null);
        setStrings();
    }
}
