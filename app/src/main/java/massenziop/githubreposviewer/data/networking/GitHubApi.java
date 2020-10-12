package massenziop.githubreposviewer.data.networking;

import java.util.HashMap;
import java.util.List;

import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.data.models.GitHubSearchDeserializedResponse;
import massenziop.githubreposviewer.data.models.GitHubUserModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

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

    @DELETE("settings/connections/applications/:{client_id}")
    Call<HashMap<String, String>> revokeToken(
            @Path("client_id") String client_id);

    @GET("search/repositories")
    Call<GitHubSearchDeserializedResponse> searchRepos(
            @Query("q") String query,
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("page") int page,
            @Query("per_page") int per_page
    );

    @GET("repositories")
    Call<List<GitHubRepoModel>> getAllReposes(
            @Query("since") int lastSeen
    );

    @GET
    Call<ResponseBody> getAvatar(@Url String CursorLoader);
}
