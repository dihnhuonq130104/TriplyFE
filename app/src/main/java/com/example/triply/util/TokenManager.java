package com.example.triply.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREFS_NAME = "TriplyPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private static final String KEY_FULL_NAME = "full_name";

    private final SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveAccessToken(String token) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public void saveUserInfo(String fullName, String userName, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (fullName != null) {
            editor.putString(KEY_FULL_NAME, fullName);
        }
        if (userName != null) {
            editor.putString(KEY_USER_NAME, userName);
        }
        if (email != null) {
            editor.putString(KEY_USER_EMAIL, email);
        }
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, null);
    }
}


