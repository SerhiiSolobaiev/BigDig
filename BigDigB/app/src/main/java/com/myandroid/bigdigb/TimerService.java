package com.myandroid.bigdigb;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class TimerService extends IntentService {
    private static final String LOG_TAG = TimerService.class.getSimpleName();

    private static final Uri PICTURE_URI = Uri
            .parse("content://com.myandroid.bigdiga.provider.PicturesContentProvider/pictures");

    private static final int DELAY = 15; //15 seconds
    private static final String EXTRA_PICTURE_ID = "PictureId";
    private static final String EXTRA_PICTURE_URL = "PictureUrl";

    public TimerService() {
        super("TimerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            TimeUnit.SECONDS.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = intent.getIntExtra(EXTRA_PICTURE_ID, -1);
        String url = intent.getStringExtra(EXTRA_PICTURE_URL);
        deleteInBigDigADB(id, url);
    }

    private void deleteInBigDigADB(int id, String url) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        try {
            Uri uri = ContentUris.withAppendedId(PICTURE_URI, id);
            int cnt = contentResolver.delete(uri, null, null);
            Log.d(LOG_TAG, "Deleted, count = " + cnt);
            showToastAboutDeleteCompleted(url);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void showToastAboutDeleteCompleted(final String url) {
        new Handler(getApplicationContext().getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.url_deleted, url),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
