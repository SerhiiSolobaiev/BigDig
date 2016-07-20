package com.myandroid.bigdigb;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DeletePictureService extends IntentService {
    private static final String LOG_TAG = DeletePictureService.class.getSimpleName();

    private static final Uri PICTURE_URI = Uri
            .parse("content://com.myandroid.bigdiga.provider.PicturesContentProvider/pictures");

    private static final int DELAY = 15; //15 seconds
    private static final String EXTRA_PICTURE_ID = "PictureId";
    private static final String EXTRA_PICTURE_URL = "PictureUrl";
    private static final String EXTRA_PICTURE_BITMAP = "PictureBitmap";

    public DeletePictureService() {
        super("DeletePictureService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            TimeUnit.SECONDS.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = intent.getIntExtra(EXTRA_PICTURE_ID, 0);
        String url = intent.getStringExtra(EXTRA_PICTURE_URL);
        byte[] byteArray = intent.getByteArrayExtra(EXTRA_PICTURE_BITMAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        deleteInBigDigADB(id, url);
        savePictureToSdCard(bitmap);
    }

    /**
     * Delete picture from BigDigA BD
     *
     * @param id  Id picture to delete
     * @param url Url picture for showing in Toast
     */
    private void deleteInBigDigADB(int id, String url) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        try {
            Uri uri = ContentUris.withAppendedId(PICTURE_URI, id);
            int cnt = contentResolver.delete(uri, null, null);
            Log.d(LOG_TAG, "Deleted, count = " + cnt);
            showToastAboutDeleteCompleted(url);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * Save picture that will be deleted from BD to path /sdcard/BIGDIG/test/B
     *
     * @param imageToSave Picture for saving
     */
    private void savePictureToSdCard(Bitmap imageToSave) {
        File direct = new File("/sdcard/BIGDIG/test/B");

        File fileName = new File(direct, new Date().getTime() + ".jpg");
        if (fileName.exists()) {
            fileName.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Log.v(LOG_TAG, "Image saved to /sdcard/BIGDIG/test/B");
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Show Toast about successful delete from service
     *
     * @param url Url picture for showing in message
     */
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
