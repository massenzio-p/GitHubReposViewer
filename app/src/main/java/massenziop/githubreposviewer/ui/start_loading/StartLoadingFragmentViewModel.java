package massenziop.githubreposviewer.ui.start_loading;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import java.io.IOException;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.authentication.Authenticator;

public class StartLoadingFragmentViewModel  extends ViewModel {

    void checkLastAuthentication(Context context) {
        ApplicationController.getInstance().getBackgroundTasksExecutor().execute(() -> {

            Account lastAccount = ApplicationController.getInstance().getCurrentAccount(context);

            if (lastAccount == null) {
                Account[] accounts = ApplicationController.getInstance().getAppAccounts(context);
                if (accounts != null && accounts.length > 0) {
                    // TODO: return choosing accounts;
                    return;
                } else {
                    // TODO: return to show login activity;
                    return;
                }
            }
            StartLoadingFragmentViewModel.this.checkToken(lastAccount, context);
        });
    }

    void checkToken(Account lastAccount, Context context) {
        ApplicationController.getInstance().getBackgroundTasksExecutor().execute(() -> {
            // TODO: May be remove this variable, but retain action?
            AccountManagerFuture<Bundle> future = AccountManager.get(context).getAuthToken(
                    lastAccount,
                    Authenticator.ACCOUNT_TYPE,
                    Bundle.EMPTY,
                    true,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                                String token = null;

                                try {
                                    token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN) ;
                                } catch (AuthenticatorException | IOException | OperationCanceledException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (TextUtils.isEmpty(token)) {
                                        // TODO: return to show login activity;
                                        return;
                                    }
                                    // TODO: return success
                                }
                        }
                    },
                    new Handler());
        });
    }
}
