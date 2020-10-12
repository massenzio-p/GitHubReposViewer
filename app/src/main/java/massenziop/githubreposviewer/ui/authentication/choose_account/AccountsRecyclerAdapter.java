package massenziop.githubreposviewer.ui.authentication.choose_account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.zip.Inflater;

import massenziop.githubreposviewer.databinding.AccountRecyclerItemBinding;

import static massenziop.githubreposviewer.ui.authentication.choose_account.AccountsRecyclerAdapter.*;

public class AccountsRecyclerAdapter extends RecyclerView.Adapter<AccountVH> {
    private final OnAccountChosenCallback callback;
    private final Account[] accounts;
    private final LayoutInflater inflater;

    static class AccountVH extends RecyclerView.ViewHolder {
        private final AccountRecyclerItemBinding binding;
        private final OnAccountChosenCallback callback;

        public AccountVH(@NonNull View itemView, AccountRecyclerItemBinding binding, OnAccountChosenCallback callback) {
            super(itemView);
            this.binding = binding;
            this.callback = callback;
        }

        void bind(Account item) {
            if (item != null) {
                binding.accountRecyclerTv.setText(item.name);
                binding.getRoot().setOnClickListener(v -> callback.onAccountChosen(item));
            }
        }
    }

    public AccountsRecyclerAdapter(OnAccountChosenCallback callback, Account[] accounts, Context context) {
        this.callback = callback;
        this.accounts = accounts;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public AccountVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AccountRecyclerItemBinding binding = AccountRecyclerItemBinding.inflate(inflater, parent, false);
        return new AccountVH(binding.getRoot(), binding, callback);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountVH holder, int position) {
        holder.bind(getItem(position));
    }

    private Account getItem(int position) {
        if (position < accounts.length) {
            return accounts[position];
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return accounts.length;
    }
}
