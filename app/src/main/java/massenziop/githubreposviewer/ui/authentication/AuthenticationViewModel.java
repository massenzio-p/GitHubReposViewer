package massenziop.githubreposviewer.ui.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.authentication.Authenticator;
import massenziop.githubreposviewer.data.networking.NetworkService;

public class AuthenticationViewModel extends ViewModel {
    private AuthenticationCheckedCallback callback;

    interface AuthenticationCheckedCallback {
        void onAccountsFound(Account[] accounts);

        void onAccountsNotFound();

        void onAuthDone();

        void onAccountFailure(String name);
    }

    public static class Factory implements ViewModelProvider.Factory {
        private AuthenticationCheckedCallback callback;

        public Factory(AuthenticationCheckedCallback callback) {
            this.callback = callback;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass != AuthenticationViewModel.class) {
                throw new IllegalArgumentException("Wrong ViewModel");
            }
            return (T) new AuthenticationViewModel(callback);
        }
    }

    AuthenticationViewModel(AuthenticationCheckedCallback callback) {
        this.callback = callback;
    }

    void getAccessToken(String code, String uri,  String state) {
        NetworkService.getInstance().getAccessToken(code, uri, state);
    }

    void findLastAuthentication(Context context) {
        ApplicationController.getInstance().getBackgroundTasksExecutor().execute(() -> {

            Account lastAccount = ApplicationController.getInstance().getCurrentAccount();

            if (lastAccount == null) {
                Account[] accounts = getAllAppAccounts();
                if (accounts != null && accounts.length > 0) {
                    callback.onAccountsFound(accounts);
                    return;
                } else {
                    callback.onAccountsNotFound();
                    return;
                }
            }
            checkToken(lastAccount, context);
        });
    }

    Account[] getAllAppAccounts() {
        return  ApplicationController.getInstance().getAppAccounts();
    }

    void checkToken(Account lastAccount, Context context) {
        ApplicationController.getInstance().getBackgroundTasksExecutor().execute(() -> {
/*            String token = AccountManager.get(context).peekAuthToken(lastAccount, Authenticator.ACCOUNT_TOKEN_TYPE);
            try {
                GitHubUserModel user = NetworkService.getInstance().getUserCardSynch(token);
            } catch (IOException e) {
                e.printStackTrace();
            }*/



            AccountManager.get(context).getAuthToken(
                    lastAccount,
                    Authenticator.ACCOUNT_TOKEN_TYPE,
                    Bundle.EMPTY,
                    true,
                    future -> {
                        if (future.isDone() && !future.isCancelled()) {
                                    try {
                                        String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                                        if (!TextUtils.isEmpty(token)) {
                                            callback.onAuthDone();
                                            return;
                                        }
                                    } catch (AuthenticatorException | IOException | OperationCanceledException e) {
                                        e.printStackTrace();
                                    }

                        }
                        callback.onAccountFailure(lastAccount.name);
                    },
                    null);
        });
    }
}
