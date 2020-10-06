package massenziop.githubreposviewer.ui.authentication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.databinding.AuthenticationLayoutBinding;

public class AuthenticationActivity extends AppCompatActivity {
    AuthenticationLayoutBinding binding;
    AuthenticationViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.authentication_layout);
        viewModel = new AuthenticationViewModel();
    }

}
