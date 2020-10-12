package massenziop.githubreposviewer.data.database;

import androidx.room.RoomDatabase;

import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.data.models.GitHubUserModel;

@androidx.room.Database(
        entities = {
                GitHubUserModel.class,
                GitHubRepoModel.class},
        version = 1,
        exportSchema = false)
public abstract class Database extends RoomDatabase {
    public abstract UsersDao usersDao();
    public abstract ReposDao reposDao();
}
