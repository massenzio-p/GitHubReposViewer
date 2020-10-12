package massenziop.githubreposviewer.ui.repos_list.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.NavigationControllerGetter;
import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.data.AppRepository;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.data.models.GitHubUserModel;
import massenziop.githubreposviewer.databinding.ReposFavoriteTabBinding;
import massenziop.githubreposviewer.ui.repos_list.PagedRecyclerAdapter;
import massenziop.githubreposviewer.ui.repos_list.ReposListPagerFragmentDirections;
import massenziop.githubreposviewer.ui.repos_list.SwipeRecyclerCallback;

public class ReposFavoritesFragment extends Fragment implements NavigationControllerGetter {
    private ReposFavoriteTabBinding binding;
    private ReposFavoritesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReposFavoriteTabBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(getActivity()).get(ReposFavoritesViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        setUpRecycler();
    }

    @Override
    public void onResume() {
        super.onResume();
        setSearchListener();
    }

    private void setUpRecycler() {
        binding.reposFavoriteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PagedRecyclerAdapter mAdapter = new PagedRecyclerAdapter(
                this::openCard,
                getContext()
        );
        viewModel.getPagedList().observe(getViewLifecycleOwner(), new Observer<PagedList<GitHubRepoModel>>() {
            @Override
            public void onChanged(PagedList<GitHubRepoModel> gitHubRepoModels) {
                mAdapter.submitList(gitHubRepoModels);
//                binding.reposSearchRecycler.smoothScrollToPosition(0);
            }
        });
        binding.reposFavoriteRecycler.setAdapter(mAdapter);
        enableSwipeToAddFavorite();
    }

    private void setSearchListener() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.app_toolbar);
            if (toolbar != null) {

                Menu menu = toolbar.getMenu();
                MenuItem searchItem = menu.findItem(R.id.app_toolbar_search_view).setVisible(true);
                SearchView sv = (SearchView) searchItem.getActionView();
                DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        viewModel.setSearchFilter(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        viewModel.setSearchFilter(newText);
                        return false;
                    }
                });
                ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                        activity, drawerLayout,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
                drawerLayout.addDrawerListener(mDrawerToggle);
                mDrawerToggle.syncState();
            }
        }
    }
    private void enableSwipeToAddFavorite() {
        SwipeRecyclerCallback swipeToDeleteCallback = new SwipeRecyclerCallback(true, getContext(), true) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final GitHubRepoModel item = viewModel.getPagedList().getValue().get(position);
                GitHubUserModel user = ApplicationController.getInstance().getCurrentUser();
                item.setFavor_owner_id(user.getId());
                binding.reposFavoriteRecycler.getAdapter().notifyDataSetChanged();
//                viewModel.getPagedList().getValue().remove(item);
                AppRepository.getInstance().removeFromFavorites(item);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);

        itemTouchhelper.attachToRecyclerView(binding.reposFavoriteRecycler);

    }

    private void openCard(GitHubRepoModel gitHubRepoModel) {
        ReposListPagerFragmentDirections.RepoCardAction action = ReposListPagerFragmentDirections.repoCardAction(gitHubRepoModel);
        getNavController().navigate(action);
    }
}
