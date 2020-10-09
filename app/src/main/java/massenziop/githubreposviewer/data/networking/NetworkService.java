package massenziop.githubreposviewer.data.networking;

import android.os.Looper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.data.models.GitHubUserModel;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService instance;
    public static final String REQUEST_PARAM_NAME_CLIENT_ID = "client_id";
    public static final String REQUEST_PARAM_NAME_SCOPE = "scope";
    public static final String REQUEST_PARAM_NAME_LOGIN = "login";
    public static final String REQUEST_PARAM_NAME_STATE = "state";
    public static final String REQUEST_PARAM_NAME_CODE = "code";
    public static final String REQUEST_PARAM_ACCESS_TOKEN = "access_token";
    public static final String REQUEST_PARAM_TOKEN_TYPE = "token_type";
    public static final String REQUEST_PARAM_ALLOW_SIGN_UP = "allow_signup";
    public static final String REQUEST_PARAM_REDIRECT_URI = "redirect_uri";
    public static final String REQUEST_SCOPE = "repo";

    private static final String codeRequestURL = "https://github.com/login/oauth/authorize";
    private static final String baseURL = "https://api.github.com";
    private static final String authBaseURL = "https://github.com";
    ;

    private Retrofit mRetrofit;

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(ApplicationController.getInstance().getBackgroundTasksExecutor())
                .client(getHttpClient())
                .build();

    }

    private OkHttpClient getHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
//                .addNetworkInterceptor(new RetryAndFollowUpInterceptor())
                .build();
        // TODO: create Interseptor
        return client;
    }

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }


    public static String getCodeRequestURL() {
        return codeRequestURL;
    }

    public String getClientID() {
        return ApplicationController.getInstance().getString(R.string.github_app_cliend_id);
    }

    private String getClientSecret() {
        return ApplicationController.getInstance().getString(R.string.github_app_cliend_secret);
    }


/*    public boolean checkTokenSYNC() throws IOException {
        Response<String> resp = mRetrofit
                .create(GitHubApi.class)
                .getUserCard(authHeader)
                .execute();
        // TODO: finish method
        return true;
    }*/

    public void getAccessToken(String code, String uri, String state) {
        getAuthRetrofit()
                .create(GitHubApi.class)
                .getAccessToken(
                        getClientID(),
                        getClientSecret(),
                        code,
                        uri,
                        state)
                .enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        if (response.code() == HttpURLConnection.HTTP_OK) {
                            ApplicationController.getInstance().checkToken(
                                    uri,
                                    response.body().get(REQUEST_PARAM_ACCESS_TOKEN));
                        }
                        //TODO: Handle errors;
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

                    }
                });
    }

    private Retrofit getAuthRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(authBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(ApplicationController.getInstance().getBackgroundTasksExecutor())
                .build();
    }

    public GitHubUserModel getUserCardSynch(String token) throws IOException {
        assert !(Thread.currentThread().equals(Looper.getMainLooper().getThread()));

        String authHeader = "token " + token;
        return getAuthRetrofit()
                .newBuilder()
                .baseUrl(baseURL)
                .build()
                .create(GitHubApi.class)
                .getUserCard(authHeader)
                .execute()
                .body();
    }
}
