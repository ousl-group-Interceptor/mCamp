package com.interceptor.mcamp;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedVariable {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_USER_ID = "UserID";
    private static final String KEY_Email = "Email";
    private static final String KEY_SIGNIN_Email = "SignInEmail";
    private static final String KEY_SIGNIN_PASSWORD = "SignInPassword";
    private static final String KEY_Google = "Google";
    private static final String KEY_Facebook = "FacebookAuthentication";
    private static final String KEY_Name = "Name";
    private static final String KEY_Version = "Version";
    private final Context mContext;

    public SharedVariable(ActivityMain mainActivity) {
        mContext = mainActivity;
    }

    public SharedVariable(ActivitySplash activitySplash) {
        mContext = activitySplash;
    }

    public SharedVariable(ActivitySignUp activitySignUp) {
        mContext = activitySignUp;
    }

    public SharedVariable(ActivitySignIn activitySignIn) {
        mContext = activitySignIn;
    }

    public SharedVariable(ActivityHome activityHome) {
        mContext = activityHome;
    }

    public SharedVariable(ActivityAddLocation activityAddLocation) {
        mContext = activityAddLocation;
    }


    public void setWhileLogin(String userID, String name, String email, boolean google, boolean facebook) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, userID);
        editor.putBoolean(KEY_Google, google);
        editor.putBoolean(KEY_Facebook, facebook);
        editor.putString(KEY_Email, email);
        editor.putString(KEY_Name, name);
        editor.apply();
    }

    public String getUserID() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_ID, "unknown");
    }

    public boolean getGoogle() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_Google, false);
    }

    public boolean getFacebook() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_Facebook, false);
    }

    public String getEmail() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_Email, "unknown");
    }

    public String getName() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_Name, "unknown");
    }

    public void setRememberMe(String email, String password) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SIGNIN_Email, email);
        editor.putString(KEY_SIGNIN_PASSWORD, password);
        editor.apply();
    }

    public String getRememberEmail() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SIGNIN_Email, "unknown");
    }

    public String getRememberPassword() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SIGNIN_PASSWORD, "unknown");
    }

    public double getVersion() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return Double.parseDouble(prefs.getString(KEY_Version, "1.0"));
    }

}

//    SharedVariable sharedVariable = new SharedVariable(this);
//    sharedVariable.setIsParent(true); // Set the boolean value to true
//
//        SharedVariable sharedVariable = new SharedVariable(this);
//        boolean isParent = sharedVariable.isParent(); // Get the boolean value
