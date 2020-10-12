package massenziop.githubreposviewer.data.data_sources;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import massenziop.githubreposviewer.data.AppRepository;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.data.networking.NetworkService;

public class GitHubReposNetworkDataSource extends PageKeyedDataSource<Integer, GitHubRepoModel> {
    private String search;

    private  GitHubReposNetworkDataSource(String search) {
        this.search = search;
    }

    public static class AssetsItemsDataSourceFactory extends DataSource.Factory<Integer, GitHubRepoModel> {
        private final String search;

        public AssetsItemsDataSourceFactory(String search) {
            this.search = search.replace(" ", "+");
        }

        @Override
        public DataSource<Integer, GitHubRepoModel> create() {
            return new GitHubReposNetworkDataSource(search);
        }
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, GitHubRepoModel> callback) {
        List<GitHubRepoModel> initList = null;
        try {
            if (TextUtils.isEmpty(search)) {
                initList = AppRepository.getInstance().getAllRepositories(
                        0);
            } else {
                initList = AppRepository.getInstance().getRepositories(
                        search,
                        1,
                        params.requestedLoadSize);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        callback.onResult(initList, -1, TextUtils.isEmpty(search) ? 1 : 2);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, GitHubRepoModel> callback) {
        List<GitHubRepoModel> initList = new ArrayList<>();
        try {
            if (params.key == -1) {
                callback.onResult(initList, -1);
                return;
            }
            if (TextUtils.isEmpty(search)) {
                initList = AppRepository.getInstance().getAllRepositories(
                        params.key * params.requestedLoadSize);
            } else {
                initList = AppRepository.getInstance().getRepositories(
                        search,
                        params.key,
                        params.requestedLoadSize);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        callback.onResult(initList, params.key - 1);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, GitHubRepoModel> callback) {
        List<GitHubRepoModel> initList = null;
        try {
            if (TextUtils.isEmpty(search)) {
                initList = AppRepository.getInstance().getAllRepositories(
                        params.key * params.requestedLoadSize);
            } else {
                initList = AppRepository.getInstance().getRepositories(
                        search,
                        params.key,
                        params.requestedLoadSize);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        callback.onResult(initList, params.key + 1);
    }
}
