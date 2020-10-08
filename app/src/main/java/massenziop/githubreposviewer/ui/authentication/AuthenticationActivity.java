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
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_CLIENT_ID;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_CODE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_LOGIN;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_SCOPE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_STATE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_SCOPE;

public class AuthenticationActivity extends AppCompatActivity {
    AuthenticationLayoutBinding binding;
    AuthenticationViewModel viewModel;
    private final String generatedState;

    public AuthenticationActivity() {
        generatedState = Utils.getRandomHash();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getData() != null) {
            onNewIntent(getIntent());
        }
        binding = DataBindingUtil.setContentView(this, R.layout.authentication_layout);
        viewModel = new AuthenticationViewModel();

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
            String code = uri.getQueryParameter(REQUEST_PARAM_NAME_CODE);
            if (!TextUtils.isEmpty(code)) {
                NetworkService.getInstance().setAuthCode(code);
                String returnedState = uri.getQueryParameter(REQUEST_PARAM_NAME_SCOPE);
                if (returnedState != null) {
                    if (!returnedState.equals(generatedState)) {
                        showSnackMessage(R.string.
                                message_compromised_authentication);
                    }
                    // TODO: Continue
                    return;
                }
            }
        }
        showSnackMessage(R.string.message_unexpected_failure);

    }

    private void showSnackMessage(int message_compromised_authentication) {
        // TODO: SnackBarShowing
    }

    private void launchChromeTab(String login) {
        String url = createURL(login);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    private String createURL(String login) {
        StringBuilder sb = new StringBuilder()
                .append(NetworkService.getCodeRequestURL())
                .append("?")
                .append(REQUEST_PARAM_NAME_CLIENT_ID)
                .append("=")
                .append(NetworkService.getClientID())

                .append("&")
                .append(REQUEST_PARAM_NAME_SCOPE)
                .append("=")
                .append(REQUEST_SCOPE)

                .append("&")
                .append(REQUEST_PARAM_NAME_STATE)
                .append("=")
                .append(generatedState);

        if (!TextUtils.isEmpty(login)) {
            sb.append("&")
                    .append(REQUEST_PARAM_NAME_LOGIN)
                    .append("=")
                    .append(login);
        }
        return sb.toString();
    }

}
