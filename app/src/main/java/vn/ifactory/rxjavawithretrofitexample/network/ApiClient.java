package vn.ifactory.rxjavawithretrofitexample.network;

import android.content.Context;
import android.text.TextUtils;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.ifactory.rxjavawithretrofitexample.AppConfig;
import vn.ifactory.rxjavawithretrofitexample.app.Const;
import vn.ifactory.rxjavawithretrofitexample.network.model.ResponseHelper;
import vn.ifactory.rxjavawithretrofitexample.network.model.TokenResponse;
import vn.ifactory.rxjavawithretrofitexample.network.model.User;
import vn.ifactory.rxjavawithretrofitexample.utils.PrefUtils;

/**
 * Created by SonLV on 01/14/2019.
 */


public class ApiClient {
    private static ApiClient sApiClientInstance;

    private ApiService apiService;
    private static int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;

    private ApiClient() {
        if (okHttpClient == null) {
            initOkhttp();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static ApiClient getApiClient() {
        if (sApiClientInstance == null){
            sApiClientInstance = new ApiClient();
        }
        return sApiClientInstance;
    }


    private static void initOkhttp() {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        // add header interceptor
        Interceptor headerAuthorizationInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json");

                // Adding Authorization token (API Key)
                // Requests will be denied without API key
                if (original.url().toString().contains("api")) {
                    requestBuilder.addHeader("Authorization", AppConfig.getToken());
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
        httpClient.addInterceptor(headerAuthorizationInterceptor);

        // for logging api
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(httpLoggingInterceptor);

        okHttpClient = httpClient.build();
    }


    public Single<TokenResponse> getToken(String userName, String password, String grantType) {
        return apiService.getToken(userName, password, grantType);
    }

    public Single<ResponseHelper<User>> registerUser(String userName, String password, String fullName, String address){
        return apiService.register(userName, password, fullName, address);
    }


}
