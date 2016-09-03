package com.krux.limb;

/**
 * Created by brent on 8/28/16.
 */
public class Limb {
    private int limb_id;
    private String limb_message;
    private String created_date;
    private String due_date;
    private boolean completed;

    public Limb(int limb_id, String limb_message, String created_date, String due_date, boolean completed){
        this.limb_id = limb_id;
        this.limb_message = limb_message;
        this.created_date = created_date;
        this.due_date = due_date;
        this.completed = completed;
    }

    public int getLimbID(){
        return this.limb_id;
    }

    public void setLimbID(int limb_id){
        this.limb_id = limb_id;
    }

    public String getLimbMessage(){
        return this.limb_message;
    }

    public void setLimbMessage(String limb_message){
        this.limb_message = limb_message;
    }

    public String getCreatedDate(){
        return this.created_date;
    }

    public void setCreatedDate(String date){
        this.created_date = date;
    }

    public String getDueDate(){
        return this.due_date;
    }

    public void setDueDate(String date){
        this.due_date = date;
    }

    public boolean isCompleted(){
        return this.completed;
    }

    public void setCompleted(boolean b){
        this.completed = b;
    }

    public void cleanDateStrings(){
        if(this.due_date != null && !this.due_date.equals("null"))
            this.due_date = this.due_date.substring(0, this.due_date.indexOf('T'));

        this.created_date = this.created_date.substring(0, this.created_date.indexOf('T'));
    }
}
