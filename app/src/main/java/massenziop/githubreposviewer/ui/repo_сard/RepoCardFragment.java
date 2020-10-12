package massenziop.githubreposviewer.ui.repo_сard;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import massenziop.githubreposviewer.NavigationControllerGetter;
import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.databinding.RepoCardBinding;

public class RepoCardFragment extends Fragment implements NavigationControllerGetter {
    private static final String REPO_ARG_KEY = "repo";
    private RepoCardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RepoCardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GitHubRepoModel repo = (GitHubRepoModel) getArguments().get("repo");
        binding.setRepo(repo);
        binding.repoItemAvatar.setImageBitmap(repo.getAvatarBitMap());
        changeToolbar(repo);
    }

    private void changeToolbar(GitHubRepoModel repo) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.app_toolbar);
            Menu menu = toolbar.getMenu();
            menu.findItem(R.id.app_toolbar_search_view).setVisible(false);
            toolbar.setTitle(repo.getName());
            toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());
        }

    }
}
