package massenziop.githubreposviewer.ui.start_loading;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.Timer;
import java.util.TimerTask;

import massenziop.githubreposviewer.NavigationControllerGetter;
import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.ui.MainActivity;
import massenziop.githubreposviewer.ui.authentication.AuthenticationActivity;

public class StartLoadingActivity extends AppCompatActivity implements NavigationControllerGetter {
    private static final int HOLD_DELAY_MILLIS = 2000;
    private StartLoadingFragmentViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.start_loading_fragment);


        viewModel = new StartLoadingFragmentViewModel(new StartLoadingFragmentViewModel.LoadingAccountCallback() {
            @Override
            public void showAccountChooser(Account[] accounts) {
                // TODO: show existing accounts
            }

            @Override
            public void directToAuthActivity() {
                launchAuthenticationActivity();
            }

            @Override
            public void directToReposList() {
                launchMainActivity();
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkAuth();
            }
        }, HOLD_DELAY_MILLIS);
    }

    private void checkAuth() {
        viewModel.checkLastAuthentication(this);
    }



    private void launchAuthenticationActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
