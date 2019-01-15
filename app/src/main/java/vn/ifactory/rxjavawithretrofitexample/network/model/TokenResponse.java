package vn.ifactory.rxjavawithretrofitexample.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SonLV on 01/14/2019.
 */


public class TokenResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private long expired;

    @SerializedName("refresh_token")
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
