package massenziop.githubreposviewer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import massenziop.githubreposviewer.app_helpers.AppPreferences;
import massenziop.githubreposviewer.authentication.Authenticator;
import massenziop.githubreposviewer.data.AppRepository;
import massenziop.githubreposviewer.data.database.Database;
import massenziop.githubreposviewer.data.models.GitHubUserModel;
import massenziop.githubreposviewer.data.networking.NetworkService;

public class ApplicationController extends Application {
    private static final int NETWORK_THREADS_AMOUNT = 2;
    private static ApplicationController instance;
    private AuthMode mode;
    private Account currentAccount;
    private ExecutorService backgroundTasksExecutor;
    private ExecutorService networkExecutor;
    private ExecutorService dbExecutor;
    private Database db;
    private GitHubUserModel currentUser;

    public void onTokenFailure(Account account) {
        AccountManager.get(this).getAuthToken(
                account,
                Authenticator.ACCOUNT_TOKEN_TYPE,
                Bundle.EMPTY,
                false,
                null,
                null
        );
    }

    public enum AuthMode {
        AUTHENTICATED_MODE,
        SIMPLE_MODE
    }

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

    public Account[] getAppAccounts() {
        return AccountManager.get(this).getAccountsByType(Authenticator.ACCOUNT_TYPE);
    }

    public Account getCurrentAccount() {
        if (currentAccount == null) {
            Account account = getLastAccount();
            if (account == null) {
                return null;
            }
            setCurrentAccount(account, null);
        }
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

    private Account getLastAccount() {
        Account[] accounts = getAppAccounts();
        String lastAccountName = AppPreferences.getLastAccountName(this);
        return findAccountFromArray(lastAccountName, accounts);
    }


    public void checkToken(String uri, String token) {
        getBackgroundTasksExecutor().execute(() -> {
            try {
                GitHubUserModel user = NetworkService.getInstance().getUserCardSynch(token);
                if (user != null) {
                    createOrUpdateAccount(user, token, uri);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void createOrUpdateAccount(GitHubUserModel user, String token, String uri) {
        AccountManager am = AccountManager.get(this);
        Account account = new Account(user.getLogin(), Authenticator.ACCOUNT_TYPE);
        boolean isNewAccount = am.addAccountExplicitly(
                account,
                null,
                user.convertToBundle()
        );
        am.setAuthToken(account, Authenticator.ACCOUNT_TOKEN_TYPE, token);
        if (!isNewAccount) {
            updateUserdata(am, account, user);
        }
        if (!TextUtils.isEmpty(uri)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        setCurrentAccount(account, user);
        AppPreferences.setLastAccountName(this, account.name);
    }

    public void setCurrentAccount(Account currentAccount, GitHubUserModel user) {
        if (currentAccount == null) {
            AccountManager am = AccountManager.get(this);
            String token = am.peekAuthToken(
                    this.currentAccount,
                    Authenticator.ACCOUNT_TOKEN_TYPE
            );
            if (!TextUtils.isEmpty(token)) {
                networkExecutor.execute(() -> {
                    try {
                        NetworkService.getInstance().revokeToken(token);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        this.currentAccount = currentAccount;
        this.currentUser = user;

        if (currentAccount != null) {
            setMode(AuthMode.AUTHENTICATED_MODE);
            initDataBase();
            dbExecutor.execute(() -> {
                AppRepository.getInstance().addAppUser(user);
            });
        } else {
            setMode(AuthMode.SIMPLE_MODE);
            AppPreferences.setLastAccountName(
                    this,
                    currentAccount == null ? null : currentAccount.name);
        }
    }

    private void initDataBase() {
        db = Room.databaseBuilder(this,
                Database.class, "github_repos_db")
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .build();
    }

    public Database getDb() {
        if (db == null) {
            initDataBase();
        }
        return db;
    }

    public AuthMode getMode() {
        return mode;
    }

    private void setMode(AuthMode mode) {
        this.mode = mode;
    }

    private void updateUserdata(AccountManager am, Account account, GitHubUserModel user) {
        am.setUserData(account, "login", user.getLogin());
        am.setUserData(account, "id", user.getId() + "");
        am.setUserData(account, "node_id", user.getNode_id());
        am.setUserData(account, "avatar_url", user.getAvatar_url());
        am.setUserData(account, "gravatar_id", user.getGravatar_id());
        am.setUserData(account, "url", user.getUrl());
        am.setUserData(account, "email", user.getEmail());
        am.setUserData(account, "name", user.getName());
        am.setUserData(account, "company", user.getCompany());
    }

    public GitHubUserModel getCurrentUser() {
        return currentUser;
    }

}
