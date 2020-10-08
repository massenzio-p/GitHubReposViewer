package massenziop.githubreposviewer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import massenziop.githubreposviewer.app_helpers.AppPreferences;
import massenziop.githubreposviewer.authentication.Authenticator;

public class ApplicationController extends Application {
    private static final int NETWORK_THREADS_AMOUNT = 2;
    private static ApplicationController instance;

    private Account currentAccount;
    private ExecutorService backgroundTasksExecutor;
    private ExecutorService networkExecutor;
    private ExecutorService dbExecutor;


    @Override
    public void onCreate() {
        super.onCreate();

        this.instance = this;
        this.backgroundTasksExecutor = Executors.newCachedThreadPool();
        this.networkExecutor = Executors.newFixedThreadPool(NETWORK_THREADS_AMOUNT);
        this.dbExecutor = Executors.newSingleThreadExecutor();
    }

    public static ApplicationController getInstance() {
        return instance;
    }


    public ExecutorService getBackgroundTasksExecutor() {
        return backgroundTasksExecutor;
    }

    public ExecutorService getNetworkExecutor() {
        return networkExecutor;
    }

    public ExecutorService getDbExecutor() {
        return dbExecutor;
    }

    public Account[] getAppAccounts(Context context) {
        return AccountManager.get(context).getAccountsByType(Authenticator.ACCOUNT_TYPE);
    }

    public Account getCurrentAccount(Context context) {
        if (getCurrentAccount() == null) {
            currentAccount = getLastAccount(context);
        }
        return currentAccount;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }


    private Account findAccountFromArray(String lastAccountName, Account[] accounts) {
        for (Account a : accounts) {
            if (a.name.equals(lastAccountName)) {
                return a;
            }
        }
        return null;
    }

    private Account getLastAccount(Context context) {
        Account[] accounts = getAppAccounts(context);
        String lastAccountName = AppPreferences.getLastAccountName(context);
        return findAccountFromArray(lastAccountName, accounts);
    }
}
