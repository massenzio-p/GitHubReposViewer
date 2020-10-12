package massenziop.githubreposviewer.ui.repos_list.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import massenziop.githubreposviewer.databinding.ReposFavoriteTabBinding;

public class ReposFavoritesFragment extends Fragment {
    private ReposFavoriteTabBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReposFavoriteTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
