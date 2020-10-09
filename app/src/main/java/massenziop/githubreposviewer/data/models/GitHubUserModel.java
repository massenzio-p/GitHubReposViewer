package massenziop.githubreposviewer.data.models;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

public class GitHubUserModel {
    @SerializedName("login")
    private String login;
    @SerializedName("id")
    private int id;
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


    public GitHubUserModel() {
    }

    public GitHubUserModel(String login, int id, String node_id,
                           String avatar_url, String gravatar_id,
                           String url, String email, String name, String company) {
        this.login = login;
        this.id = id;
        this.node_id = node_id;
        this.avatar_url = avatar_url;
        this.gravatar_id = gravatar_id;
        this.url = url;
        this.email = email;
        this.name = name;
        this.company = company;
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
        return userData;
    }

}
