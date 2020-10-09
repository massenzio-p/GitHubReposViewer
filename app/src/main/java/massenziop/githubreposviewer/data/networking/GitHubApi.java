package massenziop.githubreposviewer.data.networking;

import java.util.HashMap;

import massenziop.githubreposviewer.data.models.GitHubUserModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GitHubApi {

    @GET("/user")
    Call<GitHubUserModel> getUserCard(
            @Header("Authorization") String authHeader);

    @POST("/login/oauth/access_token")
    @Headers("Accept: application/json")
    Call<HashMap<String, String>> getAccessToken(
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret,
            @Query("code") String code,
            @Query("redirect_uri") String redirect_uri,
            @Query("state") String state);
}
