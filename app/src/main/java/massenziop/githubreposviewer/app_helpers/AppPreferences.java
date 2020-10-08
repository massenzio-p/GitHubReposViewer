package massenziop.githubreposviewer.app_helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class AppPreferences {
    public static final String APP_PREFERENCES = "app_preferences";
    public static final String PREF_LAST_ACCOUNT_NAME = "last_account_name";

    private static SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static String getLastAccountName(Context context) {
        return getSharedPreferences(context, APP_PREFERENCES)
                .getString(PREF_LAST_ACCOUNT_NAME, null);
    }

    public static void setLastAccountName(Context context, String newLastName) {
        SharedPreferences.Editor editor = getSharedPreferences(context, APP_PREFERENCES).edit();
        if (TextUtils.isEmpty(newLastName)) {
            editor
                    .clear()
                    .apply();
        } else {
            editor
                    .putString(PREF_LAST_ACCOUNT_NAME, newLastName)
                    .apply();
        }
    }
}
