package massenziop.githubreposviewer.app_helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
