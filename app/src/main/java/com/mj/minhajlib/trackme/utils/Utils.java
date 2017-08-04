package com.mj.minhajlib.trackme.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Minhaj lib on 8/3/2017.
 */

public class Utils {

    private final String PREF_FILE = "tracker_info";
    private final String PREF_KEY = "flag";
    private Context mContext;

    public Utils(Context context){
        mContext = context;
    }

    public void setPref(Boolean flag){
        boolean b = false;
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        b = editor.putBoolean(PREF_KEY,flag).commit();
        Log.d("tracker","pref "+b);
        //editor.apply();
    }

    public boolean getPref(){
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_KEY,false);
    }
}
