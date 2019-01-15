package vn.ifactory.rxjavawithretrofitexample.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SonLV on 01/11/2019.
 */


public class User{
    @SerializedName("userId")
    private int userId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("password")
    private String password;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("address")
    private String address;

    @SerializedName("images")
    private String images;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
