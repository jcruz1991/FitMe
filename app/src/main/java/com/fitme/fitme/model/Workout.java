package com.fitme.fitme.model;

public class Workout {

    private String w_name;    //might change to workout name
    private String w_category;
    private String user_name;
    private String e_name;
    private String e_desc;
    private String w_date;
    private int id;



    public Workout() {
    }

    public Workout(String w_name, String w_category, String user_name, String e_name, String e_desc, String w_date) {
        this.w_name = w_name;
        this.w_category = w_category;
        this.user_name = user_name;
        this.e_name = e_name;
        this.e_desc = e_desc;
        this.w_date = w_date;
    }

    public String getW_name() {
        return w_name;
    }

    public void setW_name(String w_name) {
        this.w_name = w_name;
    }

    public String getW_category() {
        return w_category;
    }

    public void setW_category(String w_category) {
        this.w_category = w_category;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getE_name() {
        return e_name;
    }

    public void setE_name(String e_name) {
        this.e_name = e_name;
    }

    public String getE_desc() {
        return e_desc;
    }

    public void setE_desc(String e_desc) {
        this.e_desc = e_desc;
    }

    public String getW_date() {
        return w_date;
    }

    public void setW_date(String w_date) {
        this.w_date = w_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}