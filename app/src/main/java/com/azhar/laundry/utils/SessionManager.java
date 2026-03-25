package com.azhar.laundry.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "laundry_session";
    private static final String KEY_TOKEN = "jwt_token";
    public static final String KEY_NAME = "user_name";
    private static final String KEY_ADDRESS = "user_address";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public void saveUserName(String name) {
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_NAME, "Pengguna");
    }

    public void saveUserAddress(String address) {
        editor.putString(KEY_ADDRESS, address);
        editor.apply();
    }

    public String getUserAddress() {
        return prefs.getString(KEY_ADDRESS, "-");
    }
}
