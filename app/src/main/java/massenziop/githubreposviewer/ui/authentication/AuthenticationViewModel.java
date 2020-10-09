package massenziop.githubreposviewer.ui.authentication;

import androidx.lifecycle.ViewModel;

import massenziop.githubreposviewer.data.networking.NetworkService;

public class AuthenticationViewModel extends ViewModel {
    public void getAccessToken(String code, String uri,  String state) {
        NetworkService.getInstance().getAccessToken(code, uri, state);
    }
}
