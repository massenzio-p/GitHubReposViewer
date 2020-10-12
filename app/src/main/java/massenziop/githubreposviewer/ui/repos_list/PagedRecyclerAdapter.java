package massenziop.githubreposviewer.ui.repos_list;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import massenziop.githubreposviewer.data.AppRepository;
import massenziop.githubreposviewer.data.models.GitHubRepoModel;
import massenziop.githubreposviewer.databinding.RepoRecyclerItemBinding;
import massenziop.githubreposviewer.ui.repos_list.OnGitHubRepoSelectedCallback;

public class PagedRecyclerAdapter extends PagedListAdapter<GitHubRepoModel, RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    OnGitHubRepoSelectedCallback onSelectedCallback;

    static class DiffCallBack extends DiffUtil.ItemCallback<GitHubRepoModel> {
        private List<GitHubRepoModel> oldItems, newItems;

        DiffCallBack() {
            this.oldItems = new ArrayList<>();
            this.newItems = new ArrayList<>();
        }

        @Override
        public boolean areItemsTheSame(@NonNull GitHubRepoModel oldItem, @NonNull GitHubRepoModel newItem) {
            return oldItem.getId() == (newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull GitHubRepoModel oldItem, @NonNull GitHubRepoModel newItem) {
            return oldItem.equals(newItem);
        }
    }

    public static class RepoViewHolder extends RecyclerView.ViewHolder {
        RepoRecyclerItemBinding binding;
        OnGitHubRepoSelectedCallback callback;

        public RepoViewHolder(
                @NonNull View itemView,
                RepoRecyclerItemBinding binding,
                OnGitHubRepoSelectedCallback callback) {
            super(itemView);
            this.binding = binding;
            this.callback = callback;
        }

        public void bind(GitHubRepoModel item) {
            binding.setRepo(item);
            binding.repoItemAvatar.setImageBitmap(null);
            binding.repoItemRoot.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onRepoSelected(item);
                }
            });
            Bitmap avatar = item.getAvatarBitMap();
            if (avatar == null) {
                String avatarURL = item.getOwner().getAvatar_url();
                if (!TextUtils.isEmpty(avatarURL)) {
                    AppRepository.getInstance().getAvatar(avatarURL, bitmap -> {
                        item.setAvatarBitMap(bitmap);
                        new Handler(Looper.getMainLooper()).post(() -> binding.repoItemAvatar.setImageBitmap(bitmap));
                    });
                }
            } else {
                binding.repoItemAvatar.setImageBitmap(avatar);
            }
        }
    }

    public PagedRecyclerAdapter(OnGitHubRepoSelectedCallback onSelectedResult, Context context) {
        super(new DiffCallBack());
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onSelectedCallback = onSelectedResult;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RepoRecyclerItemBinding binding = RepoRecyclerItemBinding.inflate(layoutInflater, parent, false);
        return new RepoViewHolder(binding.getRoot(), binding, onSelectedCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RepoViewHolder) holder).bind(getItem(position));
    }
}
