package com.mj.minhajlib.trackme.model;

/**
 * Created by Minhaj lib on 8/4/2017.
 */

public class UserModel {

    private String name,profile,lat,lan;

    public UserModel(String name,String profile,String lat,String lan){
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }
}
