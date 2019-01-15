package vn.ifactory.rxjavawithretrofitexample.utils;

import android.content.Context;
import android.content.SharedPreferences;

import vn.ifactory.rxjavawithretrofitexample.AppConfig;

/**
 * Created by SonLV on 01/11/2019.
 */


public class PrefUtils {
    private static final String PREFS_NAME = "Prefs_Utils_Name";

    private static PrefUtils mInstance;
    private SharedPreferences mSharedPreferences;

    private PrefUtils() {
        mSharedPreferences = AppConfig.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // singleton with double checker, the goal suppress multi thread jump into here
    public static PrefUtils getInstance() {
        if (mInstance == null) {
            mInstance = new PrefUtils();
        }

       /* SharedPrefs sharedPrefs = mInstance;
        if (sharePrefs == null) {
            synchronized (SharedPrefs.class){
                sharedPrefs = mInstance;
                if (sharedPrefs == null) {
                    sharedPrefs = new SharedPrefs();
                }
            }
        }*/
        return mInstance;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> anonymousClass) {
        if (anonymousClass == String.class) {
            return (T) mSharedPreferences.getString(key, "");
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, false));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, 0));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, 0));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, 0));
        } else {
            // for primitive type object
            return (T) AppConfig.getInstance()
                    .getGson()
                    .fromJson(mSharedPreferences.getString(key, ""), anonymousClass);
        }
    }

    // for get default value preference
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> anonymousClass, T defaultValue) {
        if (anonymousClass == String.class) {
            return (T) mSharedPreferences.getString(key, (String) defaultValue);
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, (Boolean) defaultValue));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, (Float) defaultValue));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, (Integer) defaultValue));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, (Long) defaultValue));
        } else {
            return (T) AppConfig.getInstance()
                    .getGson()
                    .fromJson(mSharedPreferences.getString(key, ""), anonymousClass);
        }
    }

    public <T> void put(String key, T data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        } else {
            editor.putString(key, AppConfig.getInstance().getGson().toJson(data));
        }
        editor.apply();
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public void remove(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }
}
