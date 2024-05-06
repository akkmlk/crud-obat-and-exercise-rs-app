package com.latihan.obat;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    String token;

    private static final String PREF_NAME = "obat";
    private static final String TOKEN_KEY = "token";

    public LocalStorage(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void clearToken() {
        editor.remove(TOKEN_KEY);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    public void setToken(String token) {
        editor.putString(TOKEN_KEY, token);
        editor.apply();
//        this.token = token;
    }
}
