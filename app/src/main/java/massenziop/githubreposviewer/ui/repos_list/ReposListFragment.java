package massenziop.githubreposviewer.ui.repos_list;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import massenziop.githubreposviewer.databinding.ReposListPagerBinding;

public class ReposListFragment extends Fragment {
    ReposListPagerBinding binding;
    ReposListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReposListPagerBinding.inflate(inflater, container, false);
        viewModel = new ReposListViewModel();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragment();
    }

    private void initFragment() {
        setUpToolbar();
        setUpSearchTextView();
        // TODO: Setup view pager with tab layout, create tabs adapter
    }

    private void setUpToolbar() {
        getActivity().setActionBar(binding.reposPagerToolbar);
        ActionBar ab = getActivity().getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");
    }

    private void setUpSearchTextView() {
        /* TODO:
            1. Create Filter
            2. Create Adapter
        */
    }
}
