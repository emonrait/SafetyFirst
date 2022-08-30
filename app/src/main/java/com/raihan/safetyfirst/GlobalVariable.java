package com.raihan.safetyfirst;

import android.app.Application;

public class GlobalVariable extends Application {
    private String latitude = "";
    private String longitude = "";
    private String name = "";
    private String address = "";
    private String policeMobile = "+8801816028491";
    private String phone = "+8801816028491";
    private String email = "emonrait@gmail.com";
    private String helpTeam = "Bangladesh Police";

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPoliceMobile() {
        return policeMobile;
    }

    public void setPoliceMobile(String policeMobile) {
        this.policeMobile = policeMobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHelpTeam() {
        return helpTeam;
    }

    public void setHelpTeam(String helpTeam) {
        this.helpTeam = helpTeam;
    }
}
