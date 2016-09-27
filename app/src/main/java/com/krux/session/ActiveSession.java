package com.krux.session;

import android.net.TrafficStats;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by brent on 8/26/16.
 */
public class ActiveSession {
    private static boolean logged_in = false;
    private static String username = null;
    private static String password = null;

    private static boolean updated = false;
    private static Boolean refresh_limb_list = false;

    private static ArrayList<String> tags = new ArrayList<String>();

    private static String before_created_date = null;
    private static String on_created_date = null;
    private static String after_created_date = null;

    private static String before_due_date = null;
    private static String on_due_date = null;
    private static String after_due_date = null;

    public static long start_rx = TrafficStats.getTotalRxBytes();
    public static long start_tx = TrafficStats.getTotalTxBytes();

    private static Boolean completed = null;

    public static boolean isLoggedIn(){
        return ActiveSession.logged_in;
    }
    public static void setLoggedIn(boolean b){ ActiveSession.logged_in = b; }

    public static String getUsername() { return ActiveSession.username; }
    public static void setUsername(String username) { ActiveSession.username = username; }

    public static String getPassword() { return ActiveSession.password; }
    public static void setPassword(String password) { ActiveSession.password = password; }

    public static void addFilterTag(String tag) {
        if(!ActiveSession.tags.contains(tag)) {
            ActiveSession.tags.add(tag);
            ActiveSession.updated = true;
        }
    }

    public static boolean filterTagsContainsTag(String tag){
        return ActiveSession.tags.contains(tag);
    }

    public static void setFilterTags(ArrayList<String> tags){
        ActiveSession.tags = tags;
        ActiveSession.updated = true;
    }

    public static ArrayList<String> getFilterTags(){
        return ActiveSession.tags;
    }

    public static String getFilterTagsAsString() {
        ArrayList<String> tags = ActiveSession.tags;

        String tags_string = null;

        if(tags.size() == 1)
            tags_string = tags.get(0);

        else if(tags.size() > 1){
            tags_string = tags.get(0);
            for (int i = 1; i < tags.size(); i++)

                tags_string += ", " + tags.get(i);
        }
        return tags_string;
    }

    public static void setFilterUpdated(boolean updated){
        ActiveSession.updated = updated;
    }

    public static boolean filterUpdated(){
        return ActiveSession.updated;
    }

    public static String getBeforeCreatedDate() { return ActiveSession.before_created_date; }
    public static void setBeforeCreatedDate(String date) {
        ActiveSession.before_created_date = date;
        ActiveSession.updated = true;
    }

    public static String getOnCreatedDate() {
        return ActiveSession.on_created_date;
    }
    public static void setOnCreatedDate(String date) {
        ActiveSession.on_created_date = date;
        ActiveSession.updated = true;
    }

    public static String getAfterCreatedDate() {
        return ActiveSession.after_created_date;
    }
    public static void setAfterCreatedDate(String date) {
        ActiveSession.after_created_date = date;
        ActiveSession.updated = true;
    }

    public static String getBeforeDueDate() {
        return ActiveSession.before_due_date;
    }
    public static void setBeforeDueDate(String date) {
        ActiveSession.before_due_date = date;
        ActiveSession.updated = true;
    }

    public static String getOnDueDate() {return ActiveSession.on_due_date;}
    public static void setOnDueDate(String date) {
        ActiveSession.on_due_date = date;
        ActiveSession.updated = true;
    }

    public static String getAfterDueDate() {
        return ActiveSession.after_due_date;
    }
    public static void setAfterDueDate(String date) {
        ActiveSession.after_due_date = date;
        ActiveSession.updated = true;
    }

    public static Boolean getCompleted(){
        return ActiveSession.completed;
    }

    public static void setCompleted(Boolean completed){
        ActiveSession.completed = completed;
        ActiveSession.updated = true;
    }

    public static boolean refreshLimbList(){
        return ActiveSession.refresh_limb_list;
    }

    public static void setRefreshLimbList(Boolean refresh_limb_list){
        ActiveSession.refresh_limb_list = refresh_limb_list;
    }
}

