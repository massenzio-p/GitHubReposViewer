package massenziop.githubreposviewer.ui.repos_list.search;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.data.AppRepository;
import massenziop.githubreposviewer.data.data_sources.GitHubReposNetworkDataSource;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;

public class ReposSearchViewModel extends ViewModel {
    private LiveData<PagedList<GitHubRepoModel>> pagedList;
    private MutableLiveData<String> searchFilter;

    public ReposSearchViewModel() {
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

    private GitHubReposNetworkDataSource.AssetsItemsDataSourceFactory getDataSourceFactory(String switchSearch) {
        return AppRepository.getInstance().getGitHubReposNetworkDataSource(searchFilter.getValue());
    }

    public void setNewSearchQuery(String query) {
        searchFilter.setValue(query);
    }

    public LiveData<PagedList<GitHubRepoModel>> getPagedList() {
        return pagedList;
    }
}
