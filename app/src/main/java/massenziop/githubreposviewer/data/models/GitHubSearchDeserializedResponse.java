package massenziop.githubreposviewer.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GitHubSearchDeserializedResponse {
    @SerializedName("items")
    List<GitHubRepoModel> items;

    public List<GitHubRepoModel> getItems() {
        return items;
    }

    public void setItems(List<GitHubRepoModel> items) {
        this.items = items;
    }
}
