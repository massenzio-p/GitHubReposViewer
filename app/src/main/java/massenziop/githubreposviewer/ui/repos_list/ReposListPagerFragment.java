package massenziop.githubreposviewer.ui.repos_list;

import android.os.Bundle;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.R;
import massenziop.githubreposviewer.databinding.ReposListPagerBinding;
import massenziop.githubreposviewer.ui.repos_list.favorites.ReposFavoritesFragment;
import massenziop.githubreposviewer.ui.repos_list.search.ReposSearchFragment;

public class ReposListPagerFragment extends Fragment {
    private ReposListPagerBinding binding;

    private static class TabsAdapter extends FragmentStatePagerAdapter {
        private final ApplicationController.AuthMode mode;
        private final static String[] titles = new String[]{"Search", "Favorites"};
        private final static int WITH_FAVORITES_COUNT = 2;
        private final static int WITHOUT_FAVORITES_COUNT = 1;

        public TabsAdapter(FragmentManager fm, ApplicationController.AuthMode mode) {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mode = mode;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return getSearchReposFragment();
            } else if (position == 1) {
                return getFavoriteReposFragment();
            }
            return null;
        }

        private Fragment getFavoriteReposFragment() {
            return new ReposFavoritesFragment();
        }

        private Fragment getSearchReposFragment() {
            return new ReposSearchFragment();
        }

        @Override
        public int getCount() {
            if (mode == ApplicationController.AuthMode.AUTHENTICATED_MODE) {
                return WITH_FAVORITES_COUNT;
            }
            return WITHOUT_FAVORITES_COUNT;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return titles[position];
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReposListPagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragment();
    }

    private void initFragment() {
        toggleSearchVisibility();
        setupPager();
        // TODO: Setup view pager with tab layout, create tabs adapter
    }

    private void setupPager() {
        ApplicationController.AuthMode mode = ApplicationController.getInstance().getMode();
        TabsAdapter mTabAdapter = new TabsAdapter(getChildFragmentManager(), mode);
        binding.reposPagerViewPager.setAdapter(mTabAdapter);
        binding.reposPagerTabLayout.setupWithViewPager(binding.reposPagerViewPager);
    }

    private void toggleSearchVisibility() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.app_toolbar);
            if (toolbar != null) {
                Menu menu = toolbar.getMenu();
                MenuItem searchItem = menu.findItem(R.id.app_toolbar_search_view);
                searchItem.setVisible(!searchItem.isVisible());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toggleSearchVisibility();
    }
}
