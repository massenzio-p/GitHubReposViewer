package massenziop.githubreposviewer.ui.repos_list.favorites;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.data.AppRepository;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;

public class ReposFavoritesViewModel extends ViewModel {
    private LiveData<PagedList<GitHubRepoModel>> pagedList;
    private MutableLiveData<String> searchFilter;


    public ReposFavoritesViewModel() {
        this.searchFilter = new MutableLiveData<>();
        pagedList = Transformations.switchMap(searchFilter, (input) -> {
            String switchSearch = (TextUtils.isEmpty(input) ? "" : input);
            LiveData<PagedList<GitHubRepoModel>> pagedList;
            PagedList.Config config = new PagedList.Config.Builder()
                    .setEnablePlaceholders(true)
                    .setInitialLoadSizeHint(50)
                    .setPrefetchDistance(20)
                    .setPageSize(TextUtils.isEmpty(switchSearch) ? 100 : 30)
                    .setEnablePlaceholders(true)
                    .build();
            try {
                pagedList = new LivePagedListBuilder<>(
                        getDataSourceFactory(
                                switchSearch),
                        config)
                        .setFetchExecutor(ApplicationController.getInstance().getBackgroundTasksExecutor())
                        .setInitialLoadKey(1)
                        .build();
                return pagedList;
            } catch (Exception e) {
                return null;
            }
        });
        searchFilter.setValue("");
    }

    private DataSource.Factory<Integer, GitHubRepoModel> getDataSourceFactory(String switchSearch) {
        return AppRepository.getInstance().getGitHubReposDBDataSource(switchSearch);
    }

    public LiveData<PagedList<GitHubRepoModel>> getPagedList() {
        return pagedList;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter.setValue(searchFilter);
    }
}
