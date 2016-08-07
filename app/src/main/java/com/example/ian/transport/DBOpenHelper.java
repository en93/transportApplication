package com.example.ian.transport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ian on 22/08/2015.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "transport.db";
    private static final int DATABASE_VERSION = 1;

    //table info
    public static final String TABLE_STOPS = "TABLE_STOPS";
    public static final String STOP_ID = "_id";
    public static final String STOP_ADDRESS = "STOP_ADDRESS";
    public static final String STOP_DESCRIPTION = "STOP_DESCRIPTION";

    public static final String[] ALL_COLUMNS ={STOP_ID, STOP_ADDRESS, STOP_DESCRIPTION};

    //Create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_STOPS
                    +" ("+ STOP_ID +" TEXT PRIMARY KEY,"
                    + STOP_ADDRESS +" TEXT,"
                    + STOP_DESCRIPTION +" TEXT"+")";


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
        onCreate(db);
    }
}

/***************************************************************************
 Code store for old code:

 db.execSQL(INSERT_DUMMY_VALUES1);
 db.execSQL(INSERT_DUMMY_VALUES2);
 db.execSQL(INSERT_DUMMY_VALUES3);

 private static final String INSERT_DUMMY_VALUES1 = "INSERT INTO " + TABLE_STOPS + " VALUES ('1805', '24 Tide Close', 'A test')" ;
 private static final String INSERT_DUMMY_VALUES2 = "INSERT INTO " + TABLE_STOPS + " VALUES ('1905', '28 Tide Close', 'A trial')" ;
 private static final String INSERT_DUMMY_VALUES3 = "INSERT INTO " + TABLE_STOPS + " VALUES ('2015', '42 Tide Close', 'A friend')" ;
 */