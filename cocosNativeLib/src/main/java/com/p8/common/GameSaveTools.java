package com.p8.common;


import android.content.Context;
import android.content.SharedPreferences;

public class GameSaveTools {
    private static final String PREFS_NAME = "AppPreferences";
    private static SharedPreferences sharedPreferences;
    private static GameSaveTools instance;

    private GameSaveTools(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized GameSaveTools getInstance(Context context) {
        if (instance == null) {
            instance = new GameSaveTools(context);
        }
        return instance;
    }
    public void putArrString(String key, String value) {
        // 取出已有数据
        String stored = sharedPreferences.getString(key, "");
        StringBuilder sb = new StringBuilder();

        if (!stored.isEmpty()) {
            sb.append(stored).append(",");
        }
        sb.append(value); // 追加新值

        sharedPreferences.edit().putString(key, sb.toString()).apply();
    }
    public String getArrString(String key, String defaultValue) {
        String stored = sharedPreferences.getString(key, defaultValue);
        if (stored.isEmpty()) return ""; // 没有数据

        String[] arr = stored.split(",");

        // 取第一个值
        String firstValue = arr[0];

        // 重新保存删除后的数据
        if (arr.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < arr.length; i++) {
                sb.append(arr[i]);
                if (i < arr.length - 1) sb.append(",");
            }
            sharedPreferences.edit().putString(key, sb.toString()).apply();
        } else {
            // 只有一个值，删除这个key
            sharedPreferences.edit().remove(key).apply();
        }

        return firstValue;
    }
    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
