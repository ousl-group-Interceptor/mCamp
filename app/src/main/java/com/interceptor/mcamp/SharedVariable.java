package com.interceptor.mcamp;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedVariable {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_USER_ID = "UserID";
    private final Context mContext;

    public SharedVariable(MainActivity mainActivity) {
        mContext = mainActivity;
    }

    public SharedVariable(ActivitySplash activitySplash) {
        mContext = activitySplash;
    }


    public String getUserID() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_ID, "unknown");
    }

    public void setWhileLogin(String userID) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, userID);
        editor.apply();
    }

//    public boolean getNewUser() {
//        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        return prefs.getBoolean(KEY_NEW_USER, true);
//    }

}

//    SharedVariable sharedVariable = new SharedVariable(this);
//    sharedVariable.setIsParent(true); // Set the boolean value to true
//
//        SharedVariable sharedVariable = new SharedVariable(this);
//        boolean isParent = sharedVariable.isParent(); // Get the boolean value
