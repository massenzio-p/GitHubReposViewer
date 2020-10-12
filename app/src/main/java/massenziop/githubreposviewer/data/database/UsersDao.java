package massenziop.githubreposviewer.data.database;

import androidx.room.Dao;
import androidx.room.Insert;

import massenziop.githubreposviewer.data.models.GitHubUserModel;

@Dao
public interface UsersDao {

    @Insert
    long insertUser(GitHubUserModel userModel);
}
