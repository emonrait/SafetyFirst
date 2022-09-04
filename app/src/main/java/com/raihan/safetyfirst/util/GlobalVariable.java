package com.raihan.safetyfirst.util;

import android.app.Application;

import java.util.ArrayList;

public class GlobalVariable extends Application {
    private String latitude = "";
    private String longitude = "";
    private String name = "";
    private String address = "";
    private String phone = "";
    private String email = "";
    private String helpTeam = "";
    private String policeMobile = "";
    private String policeEmail = "";
    private String emName = "";
    private String emMobile = "";
    private String emEmail = "";
    private String mapurl = "https://maps.google.com?q=";
    private ArrayList<String> emailList;
    private ArrayList<String> mobileList;

    public String getMapurl() {
        return mapurl;
    }

    public void setMapurl(String mapurl) {
        this.mapurl = mapurl;
    }

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

    public String getPoliceEmail() {
        return policeEmail;
    }

    public void setPoliceEmail(String policeEmail) {
        this.policeEmail = policeEmail;
    }

    public String getEmName() {
        return emName;
    }

    public void setEmName(String emName) {
        this.emName = emName;
    }

    public String getEmMobile() {
        return emMobile;
    }

    public void setEmMobile(String emMobile) {
        this.emMobile = emMobile;
    }

    public String getEmEmail() {
        return emEmail;
    }

    public void setEmEmail(String emEmail) {
        this.emEmail = emEmail;
    }

    public ArrayList<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(ArrayList<String> emailList) {
        this.emailList = emailList;
    }

    public ArrayList<String> getMobileList() {
        return mobileList;
    }

    public void setMobileList(ArrayList<String> mobileList) {
        this.mobileList = mobileList;
    }
}
