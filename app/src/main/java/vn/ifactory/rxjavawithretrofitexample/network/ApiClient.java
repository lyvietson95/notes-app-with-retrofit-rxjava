package vn.ifactory.rxjavawithretrofitexample.network;

import android.content.Context;
import android.text.TextUtils;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
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
import vn.ifactory.rxjavawithretrofitexample.network.model.ToDo;
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
                // in other case
                /*Request request = chain.request();
                // ignore authorized with action Get TOKEN or Register User
                if (request.url().encodedPath().equalsIgnoreCase("/oauth/token")
                        || (request.url().encodedPath().equalsIgnoreCase("/api/v1/users") && request.method().equalsIgnoreCase("post"))) {
                    return  chain.proceed(request);
                }
                Request newRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer token-here")
                        .build();
                Response response = chain.proceed(newRequest);
                return response;*/

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


    public Observable<TokenResponse> getToken(String userName, String password, String grantType) {
        return apiService.getToken(userName, password, grantType);
    }

    public Single<ResponseHelper<User>> registerUser(String userName, String password, String fullName, String address){
        return apiService.register(userName, password, fullName, address);
    }

    public Single<ResponseHelper<List<ToDo>>> fetchAllNotes(int userId) {
        return apiService.fetchAllNotes(userId);
    }

    public Single<ResponseHelper<User>> getUser(String userName, String password) {
        return apiService.getUser(userName, password);
    }

    public Single<ResponseHelper<ToDo>> createNote(String noteTitle, String noteDes, int userId) {
        return apiService.createNote(noteTitle, noteDes, userId);
    }

    public Completable updateNote(int noteId, String noteTitle, String noteDes) {
        return apiService.updateNote(noteId, noteTitle, noteDes);
    }

    public Completable deleteNote(int noteId) {
        return apiService.deleteNote(noteId);
    }

}
