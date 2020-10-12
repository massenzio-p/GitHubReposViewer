package massenziop.githubreposviewer.ui.authentication.choose_account;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import massenziop.githubreposviewer.databinding.AccountPickerDialogBinding;

public class AccountPickerDialog extends DialogFragment {
    private final Account[] accounts;
    private final OnAccountChosenCallback callback;

    private AccountPickerDialogBinding binding;


    public AccountPickerDialog(Account[] accounts, OnAccountChosenCallback callback) {
        this.accounts = accounts;
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AccountPickerDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecycler();
    }

    private void setUpRecycler() {
        binding.chooseAccountRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        Context context = getContext();
        if (context != null) {
            AccountsRecyclerAdapter mAdapter = new AccountsRecyclerAdapter(
                    account -> {
                        callback.onAccountChosen(account);
                        dismiss();
                    },
                    accounts,
                    getContext());
            binding.chooseAccountRecycler.setAdapter(mAdapter);
        }
    }
}
