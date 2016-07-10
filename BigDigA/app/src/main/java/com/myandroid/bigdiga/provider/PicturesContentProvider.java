package com.myandroid.bigdiga.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.myandroid.bigdiga.model.Status;

import java.util.HashMap;

public class PicturesContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.myandroid.bigdiga.provider.PicturesContentProvider";
    static final String PICTURES_PATH = "pictures";
    static final String URL = "content://" + PROVIDER_NAME + "/" + PICTURES_PATH;
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String PICTURE_UUID = "_id";
    static final String PICTURE_URL = "url";
    static final String PICTURE_STATUS = "status";
    static final String PICTURE_TIME = "time";

    private static HashMap<String, String> PICTURES_PROJECTION_MAP;

    static final int PICTURES = 1;
    static final int PICTURE_ID = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "pictures", PICTURES);
        uriMatcher.addURI(PROVIDER_NAME, "pictures/#", PICTURE_ID);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "PicturesContentProvider";
    static final String PICTURES_TABLE_NAME = "pictures";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + PICTURES_TABLE_NAME +
                    " (" + PICTURE_UUID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PICTURE_URL + " TEXT, " +
                    PICTURE_STATUS + " TEXT, " +
                    PICTURE_TIME + " TEXT);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
//            ContentValues cv = new ContentValues();
//            for (int i = 2; i >= 0; i--) {
//                cv.put(PICTURE_URL, "url " + i);
//                cv.put(PICTURE_STATUS, Status.values()[i].getId());
//                db.insert(PICTURES_TABLE_NAME, null, cv);
//            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + PICTURES_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PICTURES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case PICTURES:
                qb.setProjectionMap(PICTURES_PROJECTION_MAP);
                break;
            case PICTURE_ID:
                qb.appendWhere(PICTURE_UUID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == "") {
            /**
             * By default sort on time
             */
            sortOrder = PICTURE_TIME;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            /**
             * Get all pictures
             */
            case PICTURES:
                return "vnd.android.cursor.dir/vnd." + PROVIDER_NAME + "." + PICTURES_PATH;

            /**
             * Get a particular picture
             */
            case PICTURE_ID:
                return "vnd.android.cursor.item/vnd." + PROVIDER_NAME + "." + PICTURES_PATH;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(PICTURES_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)) {
            case PICTURES:
                count = db.delete(PICTURES_TABLE_NAME, selection, selectionArgs);
                break;
            case PICTURE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(PICTURES_TABLE_NAME, PICTURE_UUID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)) {
            case PICTURES:
                count = db.update(PICTURES_TABLE_NAME, values, selection, selectionArgs);
                break;
            case PICTURE_ID:
                count = db.update(PICTURES_TABLE_NAME, values, PICTURE_UUID + " = "
                        + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
