package com.example.ian.transport;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Ian on 31/08/2015.
 */
public class StopsProvider extends ContentProvider{

    private static final String AUTHORITY = "com.example.ian.transport.stopsprovider"; //global id for provider to framework
    private static final String BASE_PATH = "stopsdata";  //reps dataset
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH ); //ids content provider

    private static final int STOPS = 1; //give data
    private static final int STOPS_ID = 2; //deals with single record

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH); //parse uri and tell which op requested

    static{
        uriMatcher.addURI(AUTHORITY, BASE_PATH, STOPS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", STOPS_ID); //# = any numerical value
    }
    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database=helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return database.query(DBOpenHelper.TABLE_STOPS, DBOpenHelper.ALL_COLUMNS, selection, null, null, null, DBOpenHelper.STOP_ID + " DESC");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_STOPS, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_STOPS, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_STOPS, values, selection, selectionArgs);
    }
}
