package com.myandroid.bigdiga;

import android.content.Context;
import android.preference.PreferenceManager;

import com.myandroid.bigdiga.fragment.HistoryFragment;

public class AppPreferences {
    private static final String PREFERENCES_SORT_METHOD = "sortMethod";
    private static final String PREFERENCES_SORT_POSITION = "sortPosition";

    public static String getSortMethod(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFERENCES_SORT_METHOD, HistoryFragment.COLUMN_TIME);
    }

    public static void setSortMethod(Context context, String sort) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREFERENCES_SORT_METHOD, sort)
                .apply();
    }

    public static int getSortPosition(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREFERENCES_SORT_POSITION, 0);
    }

    public static void setSortPosition(Context context, int sortPosition) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREFERENCES_SORT_POSITION, sortPosition)
                .apply();
    }
}
