package massenziop.githubreposviewer.ui.repo_сard;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.NavigationControllerGetter;
import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.data.AppRepository;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.databinding.RepoCardBinding;

public class RepoCardFragment extends Fragment implements NavigationControllerGetter {
    private static final String REPO_ARG_KEY = "repo";
    private RepoCardBinding binding;
    private GitHubRepoModel repo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RepoCardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repo = (GitHubRepoModel) getArguments().get("repo");
        binding.setRepo(repo);
        binding.repoItemAvatar.setImageBitmap(repo.getAvatarBitMap());
    }

    @Override
    public void onResume() {
        super.onResume();
        changeToolbar(repo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.app_toolbar);
            Menu menu = toolbar.getMenu();
            menu.findItem(R.id.app_toolbar_add_to_favorites).setVisible(false);
            menu.findItem(R.id.app_toolbar_remove_from_favorites).setVisible(false);
        }
    }

    private void changeToolbar(GitHubRepoModel repo) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.app_toolbar);
            Menu menu = toolbar.getMenu();
            menu.findItem(R.id.app_toolbar_search_view).setVisible(false);
            menu.findItem(R.id.app_toolbar_add_to_favorites).setVisible(false);
            menu.findItem(R.id.app_toolbar_remove_from_favorites).setVisible(false);
            menu.findItem(R.id.app_toolbar_remove_all).setVisible(false);
            toolbar.setTitle(repo.getName());
            toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());
            AppRepository.getInstance().getRepoById(repo.getId(), new AppRepository.RepoGottenCallback() {
                @Override
                public void onRepoGotten(GitHubRepoModel repo1) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (repo1 == null) {
                            MenuItem item = toolbar.getMenu().findItem(R.id.app_toolbar_add_to_favorites);
                            item.setVisible(true);
                            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    repo.setFavor_owner_id(ApplicationController.getInstance().getCurrentUser().getId());
                                    AppRepository.getInstance().addToFavorites(repo);
                                    menu.findItem(R.id.app_toolbar_add_to_favorites).setVisible(false);
                                    menu.findItem(R.id.app_toolbar_remove_from_favorites).setVisible(true);
                                    return false;
                                }
                            });
                        } else {
                            MenuItem item = toolbar.getMenu().findItem(R.id.app_toolbar_remove_from_favorites);
                            item.setVisible(true);
                            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    AppRepository.getInstance().removeFromFavorites(repo);
                                    menu.findItem(R.id.app_toolbar_add_to_favorites).setVisible(true);
                                    menu.findItem(R.id.app_toolbar_remove_from_favorites).setVisible(false);
                                    return false;
                                }
                            });
                        }
                    });
                }
            });
        }

    }
}
