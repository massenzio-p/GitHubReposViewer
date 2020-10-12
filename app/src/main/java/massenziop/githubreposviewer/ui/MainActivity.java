package massenziop.githubreposviewer.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.databinding.ActivityMainBinding;
import massenziop.githubreposviewer.ui.authentication.AuthenticationActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(getNavController().getGraph()).build();

        Toolbar toolbar = binding.appToolbar;
        NavigationView navView = binding.navView;
        DrawerLayout drawerLayout = binding.drawerLayout;

        NavigationUI.setupWithNavController(
                toolbar, getNavController(), appBarConfiguration);
        NavigationUI.setupWithNavController(navView, getNavController());
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        navView.setNavigationItemSelectedListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private NavController getNavController() {
        NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController controller = hostFragment.getNavController();
        return controller;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_sign_out) {
            ApplicationController.getInstance().setCurrentAccount(null, null);
            launchAuthActivity();
        }
        return false;
    }

    private void launchAuthActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}