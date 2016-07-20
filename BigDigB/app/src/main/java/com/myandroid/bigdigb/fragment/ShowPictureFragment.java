package com.myandroid.bigdigb.fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.myandroid.bigdigb.DeletePictureService;
import com.myandroid.bigdigb.R;
import com.myandroid.bigdigb.model.Picture;
import com.myandroid.bigdigb.model.Status;
import com.myandroid.bigdigb.Utility;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowPictureFragment extends Fragment {
    private static final String LOG_TAG = ShowPictureFragment.class.getSimpleName();

    private static final String EXTRA_FRAGMENT = "Fragment";
    private static final String EXTRA_PICTURE_URL = "PictureUrl";
    private static final String EXTRA_PICTURE_ID = "PictureId";
    private static final String EXTRA_PICTURE_BITMAP = "PictureBitmap";

    private static final String OPENED_FROM_TEST = "TestFragment";
    private static final String OPENED_FROM_HISTORY = "HistoryFragment";

    private ImageView imageView;

    private static final Uri PICTURE_URI = Uri
            .parse("content://com.myandroid.bigdiga.provider.PicturesContentProvider/pictures");

    public static final String COLUMN_UUID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_URL = "url";

    private Status currentStatus = Status.UNKNOWN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_picture, container, false);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        String url = getActivity().getIntent().getStringExtra(EXTRA_PICTURE_URL);
        if (getActivity().getIntent().getStringExtra(EXTRA_FRAGMENT).equals(OPENED_FROM_TEST)) {
            Log.v(LOG_TAG, "Opened From Test Fragment");
            loadPicture(url, OPENED_FROM_TEST);
        }
        if (getActivity().getIntent().getStringExtra(EXTRA_FRAGMENT).equals(OPENED_FROM_HISTORY)) {
            Log.v(LOG_TAG, "Opened From History Fragment");
            loadPicture(url, OPENED_FROM_HISTORY);
        }

        return view;
    }

    /**
     * Get pictures from BigDigA BD
     *
     * @param id Id for searching picture
     * @return List of the pictures
     */
    private Picture getPictureById(int id) {
        ContentResolver client = getActivity().getContentResolver();
        String selection = COLUMN_UUID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor c = client.query(PICTURE_URI, null, selection, selectionArgs, null);
        Picture picture = new Picture();
        if (c.moveToFirst()) {
            do {
                picture.setId(c.getInt(c.getColumnIndex(COLUMN_UUID)));
                picture.setUrl(c.getString(c.getColumnIndex(COLUMN_URL)));
                picture.setTime(c.getString(c.getColumnIndex(COLUMN_TIME)));
                picture.setStatus(Status.values()[c.getInt(c.getColumnIndex(COLUMN_STATUS)) - 1]);

            } while (c.moveToNext());
        }
        return picture;
    }

    /**
     * Insert picture in BigDigA BD
     *
     * @param url    Picture url
     * @param status Picture status
     * @param time   Download time
     */
    private void insertIntoBigDigABD(String url, int status, String time) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_URL, url);
            cv.put(COLUMN_STATUS, status);
            cv.put(COLUMN_TIME, time);
            Uri newUri = contentResolver.insert(PICTURE_URI, cv);
            Log.d(LOG_TAG, "Inserted, result Uri : " + newUri.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * Update status of the picture in BigDigA BD
     *
     * @param id     Id picture
     * @param status Picture new status
     */
    private void updateInBigDigABD(int id, int status) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        String updateTime = String.valueOf(new Date().getTime());
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_STATUS, status);
            cv.put(COLUMN_TIME, updateTime);
            Uri uri = ContentUris.withAppendedId(PICTURE_URI, id);
            int cnt = contentResolver.update(uri, cv, null, null);
            Log.d(LOG_TAG, "Updated, count = " + cnt);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * Delete picture from BigDigA BD and save it to sdcard in DeletePictureService
     *
     * @param picture Picture that must be deleted
     * @param bitmap  Bitmap that must be saved to sdcard
     */
    private void deletePictureWithStatusDownloaded(Picture picture, Bitmap bitmap) {
        Intent intent = new Intent(getActivity(), DeletePictureService.class);
        intent.putExtra(EXTRA_PICTURE_ID, picture.getId());
        intent.putExtra(EXTRA_PICTURE_URL, picture.getUrl());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra(EXTRA_PICTURE_BITMAP, byteArray);

        getActivity().startService(intent);
    }

    /**
     * Update status of the picture
     *
     * @param picture Picture from BD
     */
    private void tryToUpdateStatus(Picture picture) {
        if (currentStatus != picture.getStatus())
            updateInBigDigABD(picture.getId(), currentStatus.getId());
        else
            Log.v(LOG_TAG, "Status for url: " + picture.getUrl() + " not changed!");
    }

    /**
     * Load picture into imageView
     *
     * @param url          Picture url that entered in BigDigA
     * @param fromFragment From which fragment will be loading
     */
    private void loadPicture(String url, String fromFragment) {
        final PictureTarget target = new PictureTarget(url, fromFragment);
        imageView.setTag(target);

        Picasso.with(getContext())
                .load(url)
                .placeholder(R.drawable.android)
                .error(R.drawable.error)
                .into(target);
    }

    /**
     * Class that do work depending on result of downloading picture
     */
    class PictureTarget implements Target {
        private String time = String.valueOf(new Date().getTime());
        private String url;
        private String fromFragment;

        PictureTarget(String url, String fromFragment) {
            this.url = url;
            this.fromFragment = fromFragment;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            currentStatus = Status.DOWNLOADED;
            imageView.setImageBitmap(bitmap);
            Log.v(LOG_TAG, "onBitmapLoaded");

            if (fromFragment.equals(OPENED_FROM_TEST)) {
                insertIntoBigDigABD(url, currentStatus.getId(), time);
            } else {
                deleteOrUpdatePicture(bitmap);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            currentStatus = Utility.isNetworkConnected(getActivity()) ?
                    Status.ERROR : Status.UNKNOWN;

            imageView.setImageDrawable(errorDrawable);
            Log.v(LOG_TAG, "onBitmapFailed");

            if (fromFragment.equals(OPENED_FROM_TEST)) {
                insertIntoBigDigABD(url, currentStatus.getId(), time);
            } else {
                deleteOrUpdatePicture(Utility.drawableToBitmap(errorDrawable));
            }
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }

        /**
         * If Status.DOWNLOADED - delete picture in DeletePictureService
         * Else try to update status
         *
         * @param bitmap Bitmap for saving it to sdcard
         */
        private void deleteOrUpdatePicture(Bitmap bitmap) {
            int pictureId = getActivity().getIntent().getIntExtra(EXTRA_PICTURE_ID, 0);
            if (pictureId != 0) {
                Picture picture = getPictureById(pictureId);

                if (picture.getStatus() == Status.DOWNLOADED) {
                    Log.v(LOG_TAG, "deletePictureWithStatusDownloaded, id == " + picture.getId());
                    deletePictureWithStatusDownloaded(picture, bitmap);
                } else if (picture.getStatus() == Status.UNKNOWN
                        || picture.getStatus() == Status.ERROR) {
                    tryToUpdateStatus(picture);
                }
            }
        }
    }
}
