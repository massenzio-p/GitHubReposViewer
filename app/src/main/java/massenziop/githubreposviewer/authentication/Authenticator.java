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

import massenziop.githubreposviewer.ui.authentication.AuthenticationActivity;

public class Authenticator extends AbstractAccountAuthenticator {
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
        final Bundle result = new Bundle();
        final AccountManager am = AccountManager.get(mContext.getApplicationContext());
        String authToken = am.peekAuthToken(account, authTokenType);
        if (!TextUtils.isEmpty(authToken)) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        } else {
            final Intent intent = new Intent(mContext, AuthenticationActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, authTokenType);
            result.putParcelable(AccountManager.KEY_INTENT, intent);
        }
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
