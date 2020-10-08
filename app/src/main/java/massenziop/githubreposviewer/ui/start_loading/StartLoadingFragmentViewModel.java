package massenziop.githubreposviewer.ui.start_loading;

import android.accounts.AccountManager;
import android.content.Context;

import androidx.lifecycle.ViewModel;

import massenziop.githubreposviewer.app_helpers.ApplicationController;

public class StartLoadingFragmentViewModel  extends ViewModel {

    void checkAuthentication(Context context) {
        ApplicationController.getInstance().getBackgroundTasksExecutor().execute(() -> {

        });
        AccountManager am = AccountManager.get(context);
        am.getau
    };

    private String getLastAccountName() {
        AppPreferences
    }
}
