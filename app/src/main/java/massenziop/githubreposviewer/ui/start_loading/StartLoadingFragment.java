package massenziop.githubreposviewer.ui.start_loading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import massenziop.githubreposviewer.databinding.StartLoadingFragmentBinding;

public class StartLoadingFragment extends Fragment {
    private static final int HOLD_DELAY_MILLIS = 2000;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return StartLoadingFragmentBinding.inflate(inflater, container, false).getRoot();
    }

}
