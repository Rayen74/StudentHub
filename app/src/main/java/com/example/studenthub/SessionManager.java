package com.example.studenthub;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "StudentHubPrefs";
    private static final String KEY_IS_LOGGED_IN = "IsLoggedIn";
    private static final String KEY_USER_ID = "UserId";
    private static final String KEY_NOM = "UserNom";
    private static final String KEY_PRENOM = "UserPrenom";
    private static final String KEY_EMAIL = "UserEmail";
    private static final String KEY_DARK_MODE = "DarkMode";
    private static final String KEY_LANGUAGE = "Language";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int id, String nom, String prenom, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_NOM, nom);
        editor.putString(KEY_PRENOM, prenom);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void updateUserInfo(String nom, String prenom) {
        editor.putString(KEY_NOM, nom);
        editor.putString(KEY_PRENOM, prenom);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getNom() {
        return pref.getString(KEY_NOM, "");
    }

    public String getPrenom() {
        return pref.getString(KEY_PRENOM, "");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void setDarkMode(boolean isDark) {
        editor.putBoolean(KEY_DARK_MODE, isDark);
        editor.apply();
    }

    public boolean isDarkMode() {
        return pref.getBoolean(KEY_DARK_MODE, false);
    }

    public void setLanguage(String lang) {
        editor.putString(KEY_LANGUAGE, lang);
        editor.apply();
    }

    public String getLanguage() {
        return pref.getString(KEY_LANGUAGE, "fr");
    }

    public void logoutUser() {
        boolean isDark = isDarkMode();
        String lang = getLanguage();
        editor.clear();
        editor.putBoolean(KEY_DARK_MODE, isDark);
        editor.putString(KEY_LANGUAGE, lang);
        editor.apply();
    }
}
