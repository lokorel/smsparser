package com.pavlov.smsparser;

import android.content.Context;
import android.content.SharedPreferences;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SessionManager {

    private Context context;
    //private String param = "regexp_pattern";
    private SharedPreferences loginpreferences;
    private SharedPreferences.Editor logineditor;

    public String getParam(String param) {
        return loginpreferences.getString(param, ".+");
    }
    public void setParam(String param, String val) {
        logineditor.putString(param, val);
        logineditor.commit();
    }
    public SessionManager(Context ctx){
        context=ctx;
        loginpreferences = getDefaultSharedPreferences(context);
        logineditor = loginpreferences.edit();
    }

}
