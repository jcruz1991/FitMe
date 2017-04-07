package com.fitme.fitme.model;


public class UserLocation {
    int id;
    String email;
    Double latitude;
    Double longitude;
    String city;


    public UserLocation() {
    }

    public UserLocation(String email, Double latitude, Double longitude, String city) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
