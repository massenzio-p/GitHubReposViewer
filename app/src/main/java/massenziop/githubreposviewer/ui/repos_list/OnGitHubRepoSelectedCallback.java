package massenziop.githubreposviewer.ui.repos_list;

import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.data.models.GitHubUserModel;

public interface OnGitHubRepoSelectedCallback {
    void onRepoSelected(GitHubRepoModel repo);
}
