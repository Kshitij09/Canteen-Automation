package com.fireaxe.canteenautomation.models;

/**
 * Created by negativezer0 on 15/3/18.
 */

public class LunchItem {
    int item_id;
    String item_name;
    int priority;
    boolean reject;

    public LunchItem(int item_id, String item_name, int priority) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.priority = priority;
    }


    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int increasePriority(){
        return priority++;
    }
    public int decrementPriority(){
        return priority>0? priority--:priority;
    }

    public boolean isReject() {
        return reject;
    }

    public void setReject(boolean reject) {
        this.reject = reject;
    }
}
