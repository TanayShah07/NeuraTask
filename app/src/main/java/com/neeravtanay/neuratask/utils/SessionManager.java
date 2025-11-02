package com.neeravtanay.neuratask.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "NeuraTaskPrefs";
    private static final String KEY_IS_FIRST_LAUNCH = "isFirstLaunch";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_HAS_SET_PIN = "hasSetPin";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean first) {
        editor.putBoolean(KEY_IS_FIRST_LAUNCH, first);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, loggedIn);
        editor.apply();
    }

    public boolean hasSetPin() {
        return prefs.getBoolean(KEY_HAS_SET_PIN, false);
    }

    public void setHasSetPin(boolean hasPin) {
        editor.putBoolean(KEY_HAS_SET_PIN, hasPin);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
