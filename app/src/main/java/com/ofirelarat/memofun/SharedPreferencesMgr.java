package com.ofirelarat.memofun;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesMgr {
    private SharedPreferences sharedPreferences;
    private final String SHARED_PREFERENCES_FILE_NAME = "com.ofirelarat.memogame.shared_preferences";
    private final String SHARED_PREFERENCES_HIGH_SCORE_KEY = "high_scores";
    private final String SHARED_PREFERENCES_IS_SILENT = "is_silent";
    private final String SHARED_PREFERENCES_IS_FIRST_TIME_KEY = "is_first_time";

    public SharedPreferencesMgr(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public boolean writeNewScoerifIsHigher(int newScore){
        int oldHighScore = sharedPreferences.getInt(SHARED_PREFERENCES_HIGH_SCORE_KEY, 0);
        if(oldHighScore < newScore) {
            sharedPreferences.edit().putInt(SHARED_PREFERENCES_HIGH_SCORE_KEY, newScore).commit();

            return true;
        }

        return false;
    }

    public int getHighScore(){
        return sharedPreferences.getInt(SHARED_PREFERENCES_HIGH_SCORE_KEY, 0);
    }

    public boolean getIsSilent(){
        return sharedPreferences.getBoolean(SHARED_PREFERENCES_IS_SILENT, false);
    }

    public void setIsSilent(){
        boolean isSilent = sharedPreferences.getBoolean(SHARED_PREFERENCES_IS_SILENT, false);
        sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_IS_SILENT, !isSilent).commit();
    }

    public boolean isFirstTimeUse(){
        boolean isFirstTime = sharedPreferences.getBoolean(SHARED_PREFERENCES_IS_FIRST_TIME_KEY, true);
        if(isFirstTime){
            sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_IS_FIRST_TIME_KEY, false).commit();
        }

        return  isFirstTime;
    }
}
