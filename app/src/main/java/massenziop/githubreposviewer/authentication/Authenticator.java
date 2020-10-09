package massenziop.githubreposviewer.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.IOException;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.BuildConfig;
import massenziop.githubreposviewer.data.models.GitHubUserModel;
import massenziop.githubreposviewer.data.networking.NetworkService;
import massenziop.githubreposviewer.ui.authentication.AuthenticationActivity;

public class Authenticator extends AbstractAccountAuthenticator {
    public static final String ACCOUNT_TYPE = BuildConfig.APPLICATION_ID + ".account";
    public static final String ACCOUNT_TOKEN_TYPE = BuildConfig.APPLICATION_ID + ".account.bearer_token";
    private Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse,
                             String accountType, String authTokenType, String[] strings, Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(mContext, AuthenticationActivity.class);
        intent.putExtra(
                AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                accountAuthenticatorResponse);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse,
                               Account account, String authTokenType, Bundle bundle) throws NetworkErrorException {
        final AccountManager am = AccountManager.get(mContext.getApplicationContext());
        String authToken = am.peekAuthToken(account, authTokenType);
        if (TextUtils.isEmpty(authToken)) {
            return createFailResult(accountAuthenticatorResponse, authTokenType);
        } else {
            try {
                GitHubUserModel user = NetworkService.getInstance().getUserCardSynch(authToken);
                if (user != null) {
                    ApplicationController.getInstance().createOrUpdateAccount(
                            user,
                            authToken,
                            null);
                    return createSuccessResult(account, authToken);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return createFailResult(accountAuthenticatorResponse, authTokenType);
        }
    }

    private Bundle createSuccessResult(Account account, String token) {
        final Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, token);
        return result;
    }

    private Bundle createFailResult(
            AccountAuthenticatorResponse response,
            String tokenType) {
        final Bundle result = new Bundle();
        final Intent intent = new Intent(mContext, AuthenticationActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, tokenType);
        result.putParcelable(AccountManager.KEY_INTENT, intent);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }

}
