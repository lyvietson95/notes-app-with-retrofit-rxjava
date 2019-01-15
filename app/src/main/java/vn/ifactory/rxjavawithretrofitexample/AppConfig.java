package vn.ifactory.rxjavawithretrofitexample;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import okhttp3.logging.HttpLoggingInterceptor;
import vn.ifactory.rxjavawithretrofitexample.network.ApiClient;
import vn.ifactory.rxjavawithretrofitexample.utils.AppUtils;
import vn.ifactory.rxjavawithretrofitexample.utils.PrefUtils;

/**
 * Created by SonLV on 01/15/2019.
 */


public class AppConfig extends Application {
    private static AppConfig sInstance;
    private static final String TAG = AppConfig.class.getSimpleName();
    private Gson mGson;

    private boolean isEmulator;

    private static ApiClient mApiClient;

    public static final String PRE_SESSION_TOKEN = "SESSION_TOKEN";
    public static final String PREFIX_TOKEN = "Bearer ";

    // singleton with double-checker
    public static AppConfig getInstance() {
        AppConfig instance = sInstance;
        if (instance == null) {
            synchronized (AppConfig.class) {
                instance = sInstance;
                if (instance == null) {
                    instance = sInstance = new AppConfig();
                }
            }

        }

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        isEmulator = AppUtils.isEmulator();
        sInstance = this;
        mGson = new Gson();
        mApiClient = ApiClient.getApiClient();
    }

    public static ApiClient getApiClient() {
        return mApiClient;
    }

    public Gson getGson() {
        return mGson;
    }


    public static String getToken() {
        return PREFIX_TOKEN + PrefUtils.getInstance().get(PRE_SESSION_TOKEN, String.class);
    }

    public void setToken(String token) {
        PrefUtils.getInstance().put(PRE_SESSION_TOKEN, token);
    }
}
