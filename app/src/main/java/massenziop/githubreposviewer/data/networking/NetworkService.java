package massenziop.githubreposviewer.data.networking;

import massenziop.githubreposviewer.ApplicationController;
import okhttp3.OkHttpClient;
import okhttp3.internal.http.BridgeInterceptor;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService instance;
    public static final String REQUEST_PARAM_NAME_CLIENT_ID = "client_id";
    public static final String REQUEST_PARAM_NAME_SCOPE = "scope";
    public static final String REQUEST_PARAM_NAME_LOGIN = "login";
    public static final String REQUEST_PARAM_NAME_STATE = "state";
    public static final String REQUEST_PARAM_NAME_CODE = "code";
    public static final String REQUEST_SCOPE = "repo";

    private static final String codeRequestURL = "https://github.com/login/oauth/authorize";
    private static final String apiClientId = "590d143d50273e13c2be";
    private static final String baseURL = "https://api.github.com";

    private String authCode;
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
                .addNetworkInterceptor(new RetryAndFollowUpInterceptor())
                .build();
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

    public static String getClientID() {
        return apiClientId;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public void checkTokenSYNC() {
        mRetrofit
                .create(GitHubApi.class)
                .;
    }
}
