package massenziop.githubreposviewer.ui.start_loading;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.authentication.Authenticator;

public class StartLoadingFragmentViewModel  extends ViewModel {
    private final LoadingAccountCallback callback;

    interface LoadingAccountCallback {
        void showAccountChooser(Account[] accounts);

        void directToAuthActivity();

        void directToReposList();
    }

    public StartLoadingFragmentViewModel(LoadingAccountCallback callback) {
        this.callback = callback;
    }

    void checkLastAuthentication(Context context) {
        ApplicationController.getInstance().getBackgroundTasksExecutor().execute(() -> {

            Account lastAccount = ApplicationController.getInstance().getCurrentAccount(context);

            if (lastAccount == null) {
                Account[] accounts = ApplicationController.getInstance().getAppAccounts(context);
                if (accounts != null && accounts.length > 0) {
                    callback.showAccountChooser(accounts);
                    return;
                } else {
                    callback.directToAuthActivity();
                    return;
                }
            }
            checkToken(lastAccount, context);
        });
    }

    private void checkToken(Account lastAccount, Context context) {
        ApplicationController.getInstance().getBackgroundTasksExecutor().execute(() -> {
            AccountManager.get(context).getAuthToken(
                    lastAccount,
                    Authenticator.ACCOUNT_TOKEN_TYPE,
                    Bundle.EMPTY,
                    true,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            if (future.isDone() && !future.isCancelled()) {
                                callback.directToReposList();
                            } else {
                                callback.directToAuthActivity();
                            }
                        }
                    },
                    null);
        });
    }
}
/*
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
        callback.directToAuthActivity();
        return;
        }
        ApplicationController.getInstance().checkToken(
        token,
        AuthenticationActivity.BASE_ACTIVITY_URI + "/" +
        AuthenticationActivity.AUTHENTICATED_CALLBACK_PATH
        );
        }
        }
        }*/
