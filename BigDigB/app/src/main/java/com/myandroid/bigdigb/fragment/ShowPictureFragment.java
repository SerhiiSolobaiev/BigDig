package com.myandroid.bigdigb.fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.myandroid.bigdigb.R;
import com.myandroid.bigdigb.TimerService;
import com.myandroid.bigdigb.model.Picture;
import com.myandroid.bigdigb.model.Status;
import com.myandroid.bigdigb.Utility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowPictureFragment extends Fragment {
    private static final String LOG_TAG = "MainActivity";

    private static final String EXTRA_PICTURE_URL = "PictureUrl";
    private static final String EXTRA_FRAGMENT = "Fragment";

    private static final String OPENED_FROM_TEST = "TestFragment";
    private static final String OPENED_FROM_HISTORY = "HistoryFragment";

    private ImageView imageView;

    private static final Uri PICTURE_URI = Uri
            .parse("content://com.myandroid.bigdiga.provider.PicturesContentProvider/pictures");

    public static final String COLUMN_UUID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_URL = "url";

    private static final String EXTRA_PICTURE_ID = "PictureId";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_picture, container, false);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        //String url = "http://i.imgur.com/nKknl0q.jpg?1";

        Log.v(LOG_TAG, "in ShowPictureFragment.onCreateView(...)");
        if (getActivity().getIntent().getStringExtra(EXTRA_FRAGMENT).equals(OPENED_FROM_TEST)) {
            Log.v(LOG_TAG, "Opened From Test Fragment");
            openedFromTest();
        }
        if (getActivity().getIntent().getStringExtra(EXTRA_FRAGMENT).equals(OPENED_FROM_HISTORY)) {
            Log.v(LOG_TAG, "Opened From History Fragment");
            openedFromHistory();
        }

        return view;
    }

    private void openedFromTest() {
        String url = null;
        if (getActivity().getIntent().getStringExtra(EXTRA_PICTURE_URL) != null)
            url = getActivity().getIntent().getStringExtra(EXTRA_PICTURE_URL);
        loadPicture(url);
        int statusId = getStatusId();
        String time = String.valueOf(new Date().getTime());

        insertIntoBigDigADB(url, statusId, time);
    }

    private void openedFromHistory() {
        String url = null;
        if (getActivity().getIntent().getStringExtra(EXTRA_PICTURE_URL) != null)
            url = getActivity().getIntent().getStringExtra(EXTRA_PICTURE_URL);
        loadPicture(url);

        List<Picture> pictures = getPicturesByUrl(url);
        for (Picture picture : pictures) {
            if (picture.getStatus() == Status.DOWNLOADED) {
                deletePictureWithStatusDownloaded(picture);
            }
            if (picture.getStatus() == Status.ERROR || picture.getStatus() == Status.UNKNOWN) {
                tryToUpdateStatus(picture);
            }
        }
    }

    private int getStatusId() {
        if (!Utility.isNetworkConnected(getActivity())) {
            return Status.UNKNOWN.getId();
        } else if (imageView.getDrawable() == ResourcesCompat.getDrawable(getResources(),
                R.drawable.error, null)) {
            return Status.ERROR.getId();
        } else
            return Status.DOWNLOADED.getId();
    }

    private List<Picture> getPicturesByUrl(String url) {
        List<Picture> pictures = new ArrayList<>();

        ContentResolver client = getActivity().getContentResolver();
        String selection = COLUMN_URL + " = ?";
        String[] selectionArgs = new String[]{url};
        Cursor c = client.query(PICTURE_URI, null, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            do {
                Picture picture = new Picture();
                picture.setId(c.getInt(c.getColumnIndex(COLUMN_UUID)));
                picture.setUrl(c.getString(c.getColumnIndex(COLUMN_URL)));
                picture.setTime(c.getString(c.getColumnIndex(COLUMN_TIME)));
                picture.setStatus(Status.values()[c.getInt(c.getColumnIndex(COLUMN_STATUS)) - 1]);

                pictures.add(picture);
            } while (c.moveToNext());
        }
        return pictures;
    }

    private void insertIntoBigDigADB(String url, int status, String time) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_URL, url);
            cv.put(COLUMN_STATUS, status);
            cv.put(COLUMN_TIME, time);
            Uri newUri = contentResolver.insert(PICTURE_URI, cv);
            Log.d(LOG_TAG, "Inserted, result Uri : " + newUri.toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            Toast.makeText(getActivity(), "Error while inserting in BigDigA!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateInBigDigADB(int id, int status) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_STATUS, status);
            Uri uri = ContentUris.withAppendedId(PICTURE_URI, id);
            int cnt = contentResolver.update(uri, cv, null, null);
            Log.d(LOG_TAG, "Updated, count = " + cnt);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            Toast.makeText(getActivity(), "Error while updating in BigDigA!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePictureWithStatusDownloaded(Picture picture) {
        Intent intent = new Intent(getActivity(),TimerService.class);
        intent.putExtra(EXTRA_PICTURE_ID, picture.getId());
        intent.putExtra(EXTRA_PICTURE_URL, picture.getUrl());
        getActivity().startService(intent);

        savePictureToSdCard(picture.getUrl());
    }

    private void savePictureToSdCard(String url) {
        Bitmap bitmap = imageView.getDrawingCache();
        String folder = "/sdcard/BIGDIG/test/B";
        final File directory = new File(folder);
        final String fileName = url + ".png";
        directory.mkdirs();
        File file = new File(directory, fileName);
        if (file.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void tryToUpdateStatus(Picture picture) {
        if (getStatusId() != picture.getStatus().getId())
            updateInBigDigADB(picture.getId(), getStatusId());
        else
            Log.v(LOG_TAG, "Status not changed!");
    }

    private void loadPicture(String url) {
        Picasso.with(getContext())
                .load(url)
                .placeholder(R.drawable.android)
                .error(R.drawable.error)
                .into(imageView);
    }
}
