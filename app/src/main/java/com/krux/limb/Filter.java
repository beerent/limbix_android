package com.krux.limb;

/**
 * Created by beerent on 10/3/16.
 */
public class Filter {
    private String name;
    private int id;
    private String created_before;
    private String created;
    private String created_after;
    private String due_before;
    private String due;
    private String due_after;
    private boolean completed;
    private boolean deleted;

    public Filter(String name, int id){
        this.name = name;
        this.id = id;
    }

    public Filter(String name, int id,
                  String created_before, String created, String created_after,
                  String due_before, String due, String due_after,
                  boolean completed, boolean deleted)
    {
        this.name = name;
        this.id = id;
        this.created_before = created_before;
        this.created = created;
        this.created_after = created_after;
        this.due_before = due_before;
        this.due = due;
        this.due_after = due_after;
        this.completed = completed;
        this.deleted = deleted;

    }



    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCreatedBefore() {
        return created_before;
    }
    public void setCreatedBefore(String created_before) {
        this.created_before = created_before;
    }
    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }
    public String getCreatedAfter() {
        return created_after;
    }
    public void setCreatedAfter(String created_after) {
        this.created_after = created_after;
    }
    public String getDueBefore() {
        return due_before;
    }
    public void setDueBefore(String due_before) {
        this.due_before = due_before;
    }
    public String getDue() {
        return due;
    }
    public void setDue(String due) {
        this.due = due;
    }
    public String getDueAfter() {
        return due_after;
    }
    public void setDueAfter(String due_after) {
        this.due_after = due_after;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}

