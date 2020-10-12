package massenziop.githubreposviewer.data.networking;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.authentication.Authenticator;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.data.models.GitHubSearchDeserializedResponse;
import massenziop.githubreposviewer.data.models.GitHubUserModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static final String DEFAULT_QUERY_SORT = "stars";
    private static final String DEFAULT_QUERY_ORDER = "desc";
    private static final int RESULTS_PER_PAGE = 20;
    private static final String ITEMS_JSON_NAME = "items";
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
    public static final String AUTH_HEADER_NAME = "Authorization";
    public static final String REQUEST_SCOPE = "repo";

    private static final String codeRequestURL = "https://github.com/login/oauth/authorize";
    private static final String baseURL = "https://api.github.com";
    private static final String authBaseURL = "https://github.com";
    ;

    private Retrofit mRetrofit;

    public interface OnImageGottenCallback {
        void onImageGotten(Bitmap bitmap);
    }


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
                .addNetworkInterceptor(chain -> {
                    ApplicationController ac = ApplicationController.getInstance();
                    Account account = ac.getCurrentAccount();
                    Request req = chain.request();
                    okhttp3.Response response = null;
                    if (account != null) {
                        String token = AccountManager.get(ac).peekAuthToken(account, Authenticator.ACCOUNT_TOKEN_TYPE);
                        req = req
                                .newBuilder()
                                .addHeader(AUTH_HEADER_NAME, "token " + token)
                                .build();
                        response = chain.proceed(req);
                        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            ac.onTokenFailure(account);
                            // TODO: message about error;
                            return null;
                        }
                    } else {
                        response = chain.proceed(req);
                    }
                    // TODO: Handle not OK responses;
                    return response;
                })
                .build();
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



    public List<GitHubRepoModel> getRepositories(String search, int page, int requestedLoadSize) throws IOException {
        Response<GitHubSearchDeserializedResponse> response = mRetrofit.create(GitHubApi.class)
                .searchRepos(
                        search,
                        DEFAULT_QUERY_SORT,
                        DEFAULT_QUERY_ORDER,
                        page,
                        RESULTS_PER_PAGE)
                .execute();
        List<GitHubRepoModel> list = response.body().getItems();
        return list;
    }

    public List<GitHubRepoModel> getAllRepositories(int page) throws IOException {
        Response<List<GitHubRepoModel>> response = mRetrofit.create(GitHubApi.class)
                .getAllReposes(page)
                .execute();
        List<GitHubRepoModel> list = (List<GitHubRepoModel>) response.body();
        return list;
    }

    public void getAvatar(String url, OnImageGottenCallback callback) {
        mRetrofit.create(GitHubApi.class)
                .getAvatar(url)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == HttpURLConnection.HTTP_OK) {
                            if (response.body() != null) {
                                // display the image data in a ImageView or save it
                                Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                                callback.onImageGotten(bmp);
                            }
                            // TODO: handle errors;
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // TODO: handle;
                    }
                });
    }
}
