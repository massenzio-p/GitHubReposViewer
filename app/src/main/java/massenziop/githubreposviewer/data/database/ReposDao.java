package massenziop.githubreposviewer.data.database;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import retrofit2.http.DELETE;

@Dao
public interface ReposDao {

    @Insert
    long insertRepo(GitHubRepoModel repo);

    @Delete
    void deleteRepo(GitHubRepoModel repo);

    @Query("DELETE FROM github_repos;")
    void deleteAll();

    @RawQuery(observedEntities = GitHubRepoModel.class)
    DataSource.Factory<Integer, GitHubRepoModel> getReposDataSource(SupportSQLiteQuery query);

}
