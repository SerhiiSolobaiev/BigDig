package com.myandroid.bigdiga;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

public class Utility {
    private static final String BIG_DIG_B_PACKAGE = "com.myandroid.bigdigb";

    private static final String EXTRA_NAME = "BigDigA";
    private static final String EXTRA_FRAGMENT = "Fragment";
    private static final String EXTRA_PICTURE_URL = "PictureUrl";

    public static boolean openApp(Context context, String fragment, String pictureUrl) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(BIG_DIG_B_PACKAGE);
        if (i == null) {
            Toast.makeText(context, R.string.no_bigdig_b, Toast.LENGTH_SHORT).show();
            return false;
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.putExtra(EXTRA_NAME, true);
        i.putExtra(EXTRA_FRAGMENT, fragment);
        i.putExtra(EXTRA_PICTURE_URL, "http://i.imgur.com/dVs98CK.jpg?1"/*pictureUrl*/);
        Log.v("ForBigDigB:", "From fragment = " + fragment);
        Log.v("ForBigDigB:", "PictureUrl = " + pictureUrl);
        context.startActivity(i);
        return true;
    }
}
