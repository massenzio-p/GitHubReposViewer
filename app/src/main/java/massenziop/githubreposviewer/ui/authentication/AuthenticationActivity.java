package massenziop.githubreposviewer.ui.authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.databinding.DataBindingUtil;

import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.app_helpers.Utils;
import massenziop.githubreposviewer.data.networking.NetworkService;
import massenziop.githubreposviewer.databinding.AuthenticationLayoutBinding;
import massenziop.githubreposviewer.ui.MainActivity;

import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_ACCESS_TOKEN;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_ALLOW_SIGN_UP;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_CLIENT_ID;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_CODE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_LOGIN;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_SCOPE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_STATE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_REDIRECT_URI;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_TOKEN_TYPE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_SCOPE;

public class AuthenticationActivity extends AppCompatActivity {
    private static final String CODE_CALLBACK_PATH = "code_callback";
    public static final String AUTHENTICATED_CALLBACK_PATH = "authenticated";
    public static final String BASE_ACTIVITY_URI = "massenziop://github.repos.viewer.auth.callback";

    private final String generatedState;

    private AuthenticationLayoutBinding binding;
    private AuthenticationViewModel viewModel;

    public AuthenticationActivity() {
        generatedState = Utils.getRandomHash();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new AuthenticationViewModel();
        if (getIntent().getData() != null) {
            onNewIntent(getIntent());
        }
        binding = DataBindingUtil.setContentView(this, R.layout.authentication_layout);

        setListeners();
    }

    private void setListeners() {
        binding.authGithubSignInBtn.setOnClickListener(v -> launchChromeTab(null));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                if (path.equals("/" + CODE_CALLBACK_PATH)) {
                    String code = uri.getQueryParameter(REQUEST_PARAM_NAME_CODE);
                    if (!TextUtils.isEmpty(code)) {
                        String returnedState = uri.getQueryParameter(REQUEST_PARAM_NAME_STATE);
                        if (returnedState != null) {
                            if (!returnedState.equals(generatedState)) {
                                showSnackMessage(R.string.
                                        message_compromised_authentication);
                            }
                            String state = Utils.getRandomHash();
                            viewModel.getAccessToken(code, BASE_ACTIVITY_URI + "/" + AUTHENTICATED_CALLBACK_PATH, state);
                            return;
                        }
                    }
                } else if (path.equals("/" + AUTHENTICATED_CALLBACK_PATH)) {
                    launchMainActivity();
                }
            }
        }
        showSnackMessage(R.string.message_unexpected_failure);

    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showSnackMessage(int message_compromised_authentication) {
        // TODO: SnackBarShowing
    }

    private void launchChromeTab(String login) {
        String url = createURL(login);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    private String createURL(String login) {
        StringBuilder sb = new StringBuilder()
                .append(NetworkService.getCodeRequestURL())
                .append("?")
                .append(REQUEST_PARAM_NAME_CLIENT_ID)
                .append("=")
                .append(NetworkService.getInstance().getClientID())

                .append("&")
                .append(REQUEST_PARAM_NAME_SCOPE)
                .append("=")
                .append(REQUEST_SCOPE)

                .append("&")
                .append(REQUEST_PARAM_NAME_STATE)
                .append("=")
                .append(generatedState)

                .append("&")
                .append(REQUEST_PARAM_ALLOW_SIGN_UP)
                .append("=")
                .append(true)

                .append("&")
                .append(REQUEST_PARAM_REDIRECT_URI)
                .append("=")
                .append(BASE_ACTIVITY_URI)
                .append("/")
                .append(CODE_CALLBACK_PATH);

        if (!TextUtils.isEmpty(login)) {
            sb.append("&")
                    .append(REQUEST_PARAM_NAME_LOGIN)
                    .append("=")
                    .append(login);
        }
        return sb.toString();
    }

}
