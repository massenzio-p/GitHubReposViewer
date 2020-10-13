package massenziop.githubreposviewer.data;

import android.database.sqlite.SQLiteConstraintException;

import androidx.paging.DataSource;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;

import java.io.IOException;
import java.util.List;
import massenziop.githubreposviewer.ApplicationController;
import massenziop.githubreposviewer.data.data_sources.GitHubReposNetworkDataSource;
import massenziop.githubreposviewer.data.database.Database;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.data.models.GitHubUserModel;
import massenziop.githubreposviewer.data.networking.NetworkService;

public class AppRepository {
    private static final String REPOS_TABLE_NAME = "github_repos";
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

    public DataSource.Factory<Integer, GitHubRepoModel> getGitHubReposDBDataSource(String switchSearch) {
        int currentUserID = ApplicationController.getInstance().getCurrentUser().getId();
        SupportSQLiteQuery query = SupportSQLiteQueryBuilder
                .builder(REPOS_TABLE_NAME)
                .columns(new String[]{"*"})
                .selection(
                        "( name LIKE ? OR " +
                                "full_name LIKE ? OR " +
                                "description LIKE ? OR " +
                                "ownername LIKE ? ) AND "  +
                                "favor_owner_id = " + currentUserID,
                        new String[]{
                                "%" + switchSearch + "%",
                                "%" + switchSearch + "%",
                                "%" + switchSearch + "%",
                                "%" + switchSearch + "%"
                        })
                .orderBy(
                        "id")
                .create();

        return ApplicationController.getInstance().getDb().reposDao().getReposDataSource(query);
    }


    public void removeFromFavorites(GitHubRepoModel item) {
        ApplicationController.getInstance().getDbExecutor().execute((() -> {
            ApplicationController.getInstance().getDb().reposDao().deleteRepo(item);
        }));

    }

    public void removeAllFavorites() {
        ApplicationController.getInstance().getDbExecutor().execute(() -> {
            getDB().reposDao().deleteAll();
        });
    }

    public interface RepoGottenCallback {
        void onRepoGotten(GitHubRepoModel repo);
    }

    public void getRepoById(int id, RepoGottenCallback callback) {
        ApplicationController.getInstance().getDbExecutor().execute((() -> {
            GitHubRepoModel repo = ApplicationController
                    .getInstance()
                    .getDb()
                    .reposDao()
                    .getRepoById(
                            id,
                            ApplicationController.getInstance().getCurrentUser().getId());
            callback.onRepoGotten(repo);
        }));
    }
}
