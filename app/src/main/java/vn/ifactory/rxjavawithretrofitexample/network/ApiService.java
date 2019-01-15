package vn.ifactory.rxjavawithretrofitexample.network;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.ifactory.rxjavawithretrofitexample.network.model.ToDo;
import vn.ifactory.rxjavawithretrofitexample.network.model.TokenResponse;
import vn.ifactory.rxjavawithretrofitexample.network.model.User;

/**
 * Created by SonLV on 01/15/2019.
 */


public interface ApiService {
    // Get token
    @FormUrlEncoded
    @POST("api/token")
    Single<TokenResponse> getToken(@Field("username") String userName,
                                   @Field("password") String password,
                                   @Field("grant_type") String grantType);

    // Register new User
    @FormUrlEncoded
    @POST("api/users")
    Single<User> register(@Field("user_name") String userName,
                          @Field("password") String password,
                          @Field("full_name") String fullName,
                          @Field("address") String address);

    // Create Note
    @FormUrlEncoded
    @POST("api/todo")
    Single<ToDo> createNote(@Field("name") String todoName,
                            @Field("description") String description,
                            @Field("userId") int userId);

    // Fetch all notes by user
    @GET("api/todoes/{id}")
    Single<List<ToDo>> fetchAllNotes(@Path("id") int userId);

    // Update Note
    @FormUrlEncoded
    @PUT("api/todo")
    Completable updateNote(@Field("todoId") int todoId,
                           @Field("name") String name,
                           @Field("description") String description);

    // Delete Note
    @FormUrlEncoded
    @DELETE("api/todo")
    Completable deleteNote(@Field("todoId") int todoId);


}