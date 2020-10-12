package massenziop.githubreposviewer.ui.authentication;

import android.accounts.Account;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.util.Timer;
import java.util.TimerTask;

import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.app_helpers.Utils;
import massenziop.githubreposviewer.ui.authentication.choose_account.AccountPickerDialog;
import massenziop.githubreposviewer.data.networking.NetworkService;
import massenziop.githubreposviewer.databinding.AuthenticationLayoutBinding;
import massenziop.githubreposviewer.ui.MainActivity;
import massenziop.githubreposviewer.ui.start_loading.StartLoadingFragment;

import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_ALLOW_SIGN_UP;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_CLIENT_ID;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_CODE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_LOGIN;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_SCOPE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_NAME_STATE;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_PARAM_REDIRECT_URI;
import static massenziop.githubreposviewer.data.networking.NetworkService.REQUEST_SCOPE;

public class AuthenticationActivity extends AppCompatActivity {
    private static final int LOADING_FRAGMENT_DELAY = 1500;
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 400;
    private static final String LOADING_FRAGMENT_TAG = "start_loading_fragment";
    private static final String ACCOUNT_CHOOSER_FRAGMENT_TAG = "account_chooser_fragment";
    private static final String CODE_CALLBACK_PATH = "code_callback";
    public static final String AUTHENTICATED_CALLBACK_PATH = "authenticated";
    public static final String BASE_ACTIVITY_URI = "massenziop://github.repos.viewer.auth.callback";


    private final String generatedState;

    private AuthenticationLayoutBinding binding;
    private AuthenticationViewModel viewModel;
    private Timer loadingDelayTimer;
    private GoogleSignInClient mGoogleSignInClient;


    public AuthenticationActivity() {
        generatedState = Utils.getRandomHash();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthenticationViewModel.Factory factory = new AuthenticationViewModel.Factory(new AuthenticationViewModel.AuthenticationCheckedCallback() {
            @Override
            public void onAccountsFound(Account[] accounts) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    binding.authChooseAccountBtn.setVisibility(View.VISIBLE);
                    binding.authChooseAccountBtn.setOnClickListener(v -> showAccountPicker(accounts));
                    closeStartLoadingFragment();
                });
            }

            @Override
            public void onAccountsNotFound() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    binding.authChooseAccountBtn.setVisibility(View.GONE);
                    closeStartLoadingFragment();
                });
            }

            @Override
            public void onAuthDone() {
                launchMainActivity();
            }

            @Override
            public void onAccountFailure(String name) {
                launchChromeTab(name);
                onAccountsFound(viewModel.getAllAppAccounts());
            }
        });
        viewModel = new ViewModelProvider(this, factory).get(AuthenticationViewModel.class);

        if (getIntent().getData() != null) {
            onNewIntent(getIntent());
            return;
        }

        binding = DataBindingUtil.setContentView(this, R.layout.authentication_layout);
        setListeners();
        loadingDelayTimer = new Timer();
        launchStartLoading();
        scheduleAuthChecking();
    }

    private void scheduleAuthChecking() {
        loadingDelayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkLastAuth();
            }
        }, LOADING_FRAGMENT_DELAY);
    }


    private void checkLastAuth() {
        viewModel.findLastAuthentication(this);
    }

    private void launchStartLoading() {
        StartLoadingFragment loadingFragment = new StartLoadingFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(binding.authRootLayout.getId(), loadingFragment, LOADING_FRAGMENT_TAG)
                .commit();
    }

    private void closeStartLoadingFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LOADING_FRAGMENT_TAG);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    private void setListeners() {
        binding.authGithubSignInBtn.setOnClickListener(v -> launchChromeTab(null));
        binding.authGoogleSignInBtn.setOnClickListener(v -> authWithGoogle());
        binding.authSimpleUse.setOnClickListener(v -> launchMainActivity());
    }

    private void authWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    private void showAccountPicker(Account[] accounts) {
        AccountPickerDialog picker = new AccountPickerDialog(
                accounts,
                account -> viewModel.checkToken(account, this)
        );
        picker.show(getSupportFragmentManager(), ACCOUNT_CHOOSER_FRAGMENT_TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result != null && result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    if (account != null) {
                        String email = account.getEmail();
                        launchChromeTab(email);
                    }
                }
            }
        }
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
                            viewModel.getAccessToken(
                                    code,
                                    BASE_ACTIVITY_URI + "/" + AUTHENTICATED_CALLBACK_PATH,
                                    state);
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

    private void showSnackMessage(int messageId) {
        // TODO: SnackBarShowing
    }

    private void launchChromeTab(String login) {
        String url = createURL(login);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_NO_HISTORY);
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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (loadingDelayTimer != null) {
            loadingDelayTimer.cancel();
            loadingDelayTimer.purge();
        }
    }
}
