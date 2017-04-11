package com.fitme.fitme.model;


import java.io.Serializable;

public class Exercise implements Serializable{
    private String exercise_name;
    private String body_type;
    private String exercise_type;

    public Exercise() {
    }

    public Exercise(String exercise_name, String body_type, String exercise_type) {
        this.exercise_name = exercise_name;
        this.body_type = body_type;
        this.exercise_type = exercise_type;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public String getBody_type() {
        return body_type;
    }

    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    public String getExercise_type() {
        return exercise_type;
    }

    public void setExercise_type(String exercise_type) {
        this.exercise_type = exercise_type;
    }
}

