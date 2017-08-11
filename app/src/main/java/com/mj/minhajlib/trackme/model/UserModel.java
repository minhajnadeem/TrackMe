package com.mj.minhajlib.trackme.model;

/**
 * Created by Minhaj lib on 8/4/2017.
 */

public class UserModel {

    private String name,profile;
    private double lat,lan;

    public UserModel(String name,String profile,double lat,double lan){
        setName(name);
        setProfile(profile);
        setLat(lat);
        setLan(lan);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLan() {
        return lan;
    }

    public void setLan(double lan) {
        this.lan = lan;
    }
}
