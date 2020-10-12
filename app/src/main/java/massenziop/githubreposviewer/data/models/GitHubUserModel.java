package massenziop.githubreposviewer.data.models;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import massenziop.githubreposviewer.ApplicationController;

@Entity(tableName = "github_users")
public class GitHubUserModel implements Parcelable {
    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("login")
    private String login;

    @SerializedName("node_id")
    private String node_id;

    @SerializedName("avatar_url")
    private String avatar_url;

    @SerializedName("gravatar_id")
    private String gravatar_id;

    @SerializedName("url")
    private String url;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("company")
    private String company;

    private String uuid;

    public GitHubUserModel() {

    }

    public GitHubUserModel(Parcel in) {
        login = in.readString();
        id = in.readInt();
        node_id = in.readString();
        avatar_url = in.readString();
        gravatar_id = in.readString();
        url = in.readString();
        email = in.readString();
        name = in.readString();
        company = in.readString();
        uuid = in.readString();
    }

    public GitHubUserModel(String login, int id, String node_id,
                           String avatar_url, String gravatar_id,
                           String url, String email, String name, String company, String uuid) {
        this.login = login;
        this.id = id;
        this.node_id = node_id;
        this.avatar_url = avatar_url;
        this.gravatar_id = gravatar_id;
        this.url = url;
        this.email = email;
        this.name = name;
        this.company = company;
        this.uuid = uuid;
    }

    public GitHubUserModel(Bundle userData) {
        this.login = userData.getString("login");
        this.id = userData.getInt("id");
        this.node_id = userData.getString("node_id");
        this.avatar_url = userData.getString("avatar_url");
        this.gravatar_id = userData.getString("gravatar_id");
        this.url = userData.getString("url");
        this.email = userData.getString("email");
        this.name = userData.getString("name");
        this.company = userData.getString("company");
        this.uuid = userData.getString("uuid");
    }

    public GitHubUserModel(Account currentAccount, Context context) {
        AccountManager am = AccountManager.get(context);
        this.login = am.getUserData(currentAccount, "login");
        this.id = Integer.parseInt(am.getUserData(currentAccount, "id"));
        this.node_id = am.getUserData(currentAccount, "node_id");
        this.avatar_url = am.getUserData(currentAccount, "avatar_url");
        this.gravatar_id = am.getUserData(currentAccount, "gravatar_id");
        this.url = am.getUserData(currentAccount, "url");
        this.email = am.getUserData(currentAccount, "email");
        this.name = am.getUserData(currentAccount, "name");
        this.company = am.getUserData(currentAccount, "company");
        this.uuid = am.getUserData(currentAccount, "uuid");
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getGravatar_id() {
        return gravatar_id;
    }

    public void setGravatar_id(String gravatar_id) {
        this.gravatar_id = gravatar_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Bundle convertToBundle() {
        Bundle userData = new Bundle();
        userData.putString("login", login);
        userData.putInt("id", id);
        userData.putString("node_id", node_id);
        userData.putString("avatar_url", avatar_url);
        userData.putString("gravatar_id", gravatar_id);
        userData.putString("url", url);
        userData.putString("email", email);
        userData.putString("name", name);
        userData.putString("company", company);
        userData.putString("uuid", uuid);
        return userData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(login);
        dest.writeInt(id);
        dest.writeString(node_id);
        dest.writeString(avatar_url);
        dest.writeString(gravatar_id);
        dest.writeString(url);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(company);
        dest.writeString(uuid);
    }

    public static final Parcelable.Creator<GitHubUserModel> CREATOR = new Parcelable.Creator<GitHubUserModel>() {

        @Override
        public GitHubUserModel createFromParcel(Parcel source) {
            return new GitHubUserModel(source);
        }

        @Override
        public GitHubUserModel[] newArray(int size) {
            return new GitHubUserModel[size];
        }
    };
}
