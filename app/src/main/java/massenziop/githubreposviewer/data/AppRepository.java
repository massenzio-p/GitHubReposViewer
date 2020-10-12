package massenziop.githubreposviewer.data;

import android.database.sqlite.SQLiteConstraintException;
import java.io.IOException;
import java.util.List;
import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.data.data_sources.GitHubReposNetworkDataSource;
import massenziop.githubreposviewer.data.database.Database;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.data.models.GitHubUserModel;
import massenziop.githubreposviewer.data.networking.NetworkService;

public class AppRepository {
    private static AppRepository instance;

    public static AppRepository getInstance() {
        if (instance == null) {
            instance = new AppRepository();
        }

        return instance;
    }


    public GitHubReposNetworkDataSource.AssetsItemsDataSourceFactory getGitHubReposNetworkDataSource(String value) {
        return new GitHubReposNetworkDataSource.AssetsItemsDataSourceFactory(value);
    }

    public void getAvatar(String url, NetworkService.OnImageGottenCallback callback) {
        NetworkService.getInstance().getAvatar(url, callback);
    }

    public List<GitHubRepoModel> getAllRepositories(int page) throws IOException {
        return NetworkService.getInstance().getAllRepositories(page);
    }
    public List<GitHubRepoModel> getRepositories(String search, int page, int requestedLoadSize) throws IOException {
        return NetworkService.getInstance().getRepositories(search, page, requestedLoadSize);
    }

    public void addToFavorites(GitHubRepoModel item) {
        ApplicationController.getInstance().getDbExecutor().execute(() -> {
            try {
                getDB().reposDao().insertRepo(item);
            } catch (SQLiteConstraintException e) {

            }
        });
    }
    private Database getDB() {
        return ApplicationController.getInstance().getDb();
    }

    public void addAppUser(GitHubUserModel user) {
        if (user != null) {
            ApplicationController.getInstance().getDbExecutor().execute(() -> {
                try {
                    ApplicationController.getInstance().getDb().usersDao().insertUser(user);
                } catch (SQLiteConstraintException exception) {
                }
            });
        }
    }
}
