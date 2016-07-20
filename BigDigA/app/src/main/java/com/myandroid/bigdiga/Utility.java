package com.myandroid.bigdiga;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class Utility {
    private static final String BIG_DIG_B_PACKAGE = "com.myandroid.bigdigb";

    private static final String EXTRA_NAME = "BigDigA";
    private static final String EXTRA_FRAGMENT = "Fragment";
    private static final String EXTRA_PICTURE_URL = "PictureUrl";
    private static final String EXTRA_PICTURE_ID = "PictureId";

    /**
     * Open app BigDigB for downloading picture (BigDigB have different scenarios
     * of the work depending on extra values from BigDigB)
     *
     * @param context    Application context
     * @param fragment   Fragment's name for putting it to extra
     * @param pictureUrl Picture's url for downloading
     * @param pictureId  Picture's id (0 for new picture(inserting to BD in BigDigB app))
     * @return Result of the opening BigDigB
     */
    public static boolean openApp(Context context, String fragment, String pictureUrl, int pictureId) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(BIG_DIG_B_PACKAGE);
        if (i == null) {
            Toast.makeText(context, R.string.no_bigdig_b, Toast.LENGTH_SHORT).show();
            return false;
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.putExtra(EXTRA_NAME, true);
        i.putExtra(EXTRA_FRAGMENT, fragment);
        i.putExtra(EXTRA_PICTURE_URL, pictureUrl/*http://i.imgur.com/dVs98CK.jpg?1*/);
        i.putExtra(EXTRA_PICTURE_ID, pictureId);
        Log.v("ForBigDigB:", "From fragment = " + fragment);
        Log.v("ForBigDigB:", "PictureUrl = " + pictureUrl);
        Log.v("ForBigDigB:", "PictureId = " + pictureId);
        context.startActivity(i);
        return true;
    }

    /**
     * Url validation
     *
     * @param enteredUrl Url which entered User
     * @return true - url valid, false - not
     */
    public static boolean isUrlValid(String enteredUrl) {
        return TextUtils.isEmpty(enteredUrl);
        //return Patterns.WEB_URL.matcher(enteredUrl).matches();
    }
}
