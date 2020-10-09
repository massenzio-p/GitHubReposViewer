package massenziop.githubreposviewer;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public interface NavigationControllerGetter {
    default public NavController getNavController() {
        if (this instanceof Fragment) {
            Fragment ths = (Fragment) this;
            Activity activity = ths.getActivity();
            if (activity != null) {
                return
                        Navigation.findNavController(activity, R.id.nav_host_fragment);
            }
        }
        return null;
    }
}
