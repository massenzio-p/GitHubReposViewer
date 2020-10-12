package massenziop.githubreposviewer.data.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.Objects;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "github_repos",
            foreignKeys = @ForeignKey(entity = GitHubUserModel.class, parentColumns = "id", childColumns = "favor_owner_id", onDelete = CASCADE))
public class GitHubRepoModel implements Parcelable {
    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("node_id")
    private String node_id;

    @SerializedName("name")
    private String name;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("private")
    private boolean is_private;

    @Embedded(prefix = "owner")
    @SerializedName("owner")
    private GitHubUserModel owner;

    @SerializedName("description")
    private String description;

    @SerializedName("forks_count")
    private int forks_count;

    @SerializedName("forks")
    private int forks;

    @SerializedName("stargazers_count")
    private int stargazers_count;

    @SerializedName("default_branch")
    private String default_branch;

    @TypeConverters(DateConverter.class)
    @SerializedName("created_at")
    private Date created_at;

    @TypeConverters(DateConverter.class)
    @SerializedName("updated_at")
    private Date updated_at;

    @TypeConverters(DateConverter.class)
    @SerializedName("pushed_at")
    private Date pushed_at;

    @Ignore
    private Bitmap avatarBitMap;

    private int favor_owner_id;

    public int getFavor_owner_id() {
        return favor_owner_id;
    }

    public void setFavor_owner_id(int favor_owner_id) {
        this.favor_owner_id = favor_owner_id;
    }

    public static class DateConverter  {
        @TypeConverter
        public long fromDate(Date date) {
            if (date == null) {
                return -1;
            }
            return date.getTime();
        }

        @TypeConverter
        public Date toDate(long data) {
            if (data == -1) {
                return null;
            }
            return new Date(data);
        }
    }

    public GitHubRepoModel() {
    }


    public GitHubRepoModel(Parcel in) {
        id = in.readInt();
        node_id = in.readString();
        name = in.readString();
        full_name = in.readString();
        is_private = in.readInt() > 0 ? true : false;
        description = in.readString();
        forks_count = in.readInt();
        forks = in.readInt();
        stargazers_count = in.readInt();
        default_branch = in.readString();
        created_at = (Date) in.readSerializable();
        updated_at = (Date) in.readSerializable();
        pushed_at = (Date) in.readSerializable();
        owner = in.readParcelable(getClass().getClassLoader());
        avatarBitMap = in.readParcelable(getClass().getClassLoader());
    }

    public GitHubRepoModel(int id, String node_id, String name, String full_name, boolean is_private, GitHubUserModel owner, String description, int forks_count, int forks, int stargazers_count, String default_branch, Date created_at, Date updated_at, Date pushed_at, Bitmap avatarBitMap) {
        this.id = id;
        this.node_id = node_id;
        this.name = name;
        this.full_name = full_name;
        this.is_private = is_private;
        this.owner = owner;
        this.description = description;
        this.forks_count = forks_count;
        this.forks = forks;
        this.stargazers_count = stargazers_count;
        this.default_branch = default_branch;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.pushed_at = pushed_at;
        this.avatarBitMap = avatarBitMap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public boolean isIs_private() {
        return is_private;
    }

    public void setIs_private(boolean is_private) {
        this.is_private = is_private;
    }

    public GitHubUserModel getOwner() {
        return owner;
    }

    public void setOwner(GitHubUserModel owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getForks_count() {
        return forks_count;
    }

    public void setForks_count(int forks_count) {
        this.forks_count = forks_count;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getStargazers_count() {
        return stargazers_count;
    }

    public void setStargazers_count(int stargazers_count) {
        this.stargazers_count = stargazers_count;
    }

    public String getDefault_branch() {
        return default_branch;
    }

    public void setDefault_branch(String default_branch) {
        this.default_branch = default_branch;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getPushed_at() {
        return pushed_at;
    }

    public void setPushed_at(Date pushed_at) {
        this.pushed_at = pushed_at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GitHubRepoModel)) return false;
        GitHubRepoModel that = (GitHubRepoModel) o;
        return getId() == that.getId() &&
                isIs_private() == that.isIs_private() &&
                getForks_count() == that.getForks_count() &&
                getForks() == that.getForks() &&
                getStargazers_count() == that.getStargazers_count() &&
                getNode_id().equals(that.getNode_id()) &&
                getName().equals(that.getName()) &&
                getFull_name().equals(that.getFull_name()) &&
                getOwner().equals(that.getOwner()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getDefault_branch(), that.getDefault_branch()) &&
                getCreated_at().equals(that.getCreated_at()) &&
                Objects.equals(getUpdated_at(), that.getUpdated_at()) &&
                Objects.equals(getPushed_at(), that.getPushed_at());
    }

    public void setAvatarBitMap(Bitmap bitmap) {
        this.avatarBitMap = bitmap;
    }

    public Bitmap getAvatarBitMap() {
        return avatarBitMap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(node_id);
        dest.writeString(name);
        dest.writeString(full_name);
        dest.writeInt(is_private ? 1 : 0);
        dest.writeString(description);
        dest.writeInt(forks_count);
        dest.writeInt(forks);
        dest.writeInt(stargazers_count);
        dest.writeString(default_branch);
        dest.writeSerializable(created_at);
        dest.writeSerializable(updated_at);
        dest.writeSerializable(pushed_at);
        dest.writeParcelable(owner, 0);
        dest.writeParcelable(avatarBitMap, 0);
    }
    public static final Parcelable.Creator<GitHubRepoModel> CREATOR = new Parcelable.Creator<GitHubRepoModel>() {

        @Override
        public GitHubRepoModel createFromParcel(Parcel source) {
            return new GitHubRepoModel(source);
        }

        @Override
        public GitHubRepoModel[] newArray(int size) {
            return new GitHubRepoModel[size];
        }
    };
}
