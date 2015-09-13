package fr.bischof.raphael.gothiite.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for run data.
 * Created by biche on 13/09/2015.
 */
public class RunDbHelper  extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "run.db";

    public RunDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_RUN_TABLE = "CREATE TABLE " + RunContract.RunEntry.TABLE_NAME + " (" +
                RunContract.RunEntry._ID + " TEXT PRIMARY KEY," +
                RunContract.RunEntry.COLUMN_AVG_SPEED + " REAL NOT NULL, " +
                RunContract.RunEntry.COLUMN_RUN_TYPE_ID + " TEXT NOT NULL, " +
                RunContract.RunEntry.COLUMN_START_DATE + " INTEGER NOT NULL, " +
                RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT + " REAL NOT NULL " +
                " FOREIGN KEY (" + RunContract.RunEntry.COLUMN_RUN_TYPE_ID + ") REFERENCES " +
                RunContract.RunTypeEntry.TABLE_NAME + " (" + RunContract.RunTypeEntry._ID + "));";

        final String SQL_CREATE_RUN_INTERVAL_TABLE = "CREATE TABLE " + RunContract.RunIntervalEntry.TABLE_NAME + " (" +
                RunContract.RunIntervalEntry._ID + " INTEGER PRIMARY KEY," +

                RunContract.RunIntervalEntry.COLUMN_DISTANCE_DONE + " REAL NOT NULL, " +
                RunContract.RunIntervalEntry.COLUMN_END_DATE + " INTEGER NOT NULL, " +
                RunContract.RunIntervalEntry.COLUMN_END_POSITION_LATITUDE + " REAL NOT NULL, " +
                RunContract.RunIntervalEntry.COLUMN_END_POSITION_LONGITUDE + " REAL NOT NULL," +
                RunContract.RunIntervalEntry.COLUMN_ORDER + " INTEGER NOT NULL, " +
                RunContract.RunIntervalEntry.COLUMN_START_DATE + " INTEGER NOT NULL, " +
                RunContract.RunIntervalEntry.COLUMN_START_POSITION_LATITUDE + " REAL NOT NULL, " +
                RunContract.RunIntervalEntry.COLUMN_START_POSITION_LONGITUDE + " REAL NOT NULL, " +
                RunContract.RunIntervalEntry.COLUMN_RUN_ID + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + RunContract.RunIntervalEntry.COLUMN_RUN_ID + ") REFERENCES " +
                RunContract.RunEntry.TABLE_NAME + " (" + RunContract.RunEntry._ID + "));";

        final String SQL_CREATE_RUN_TYPE_TABLE = "CREATE TABLE " + RunContract.RunTypeEntry.TABLE_NAME + " (" +
                RunContract.RunTypeEntry._ID + " TEXT PRIMARY KEY," +
                RunContract.RunTypeEntry.COLUMN_CAN_BE_DELETED + " INTEGER NOT NULL, " +
                RunContract.RunTypeEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                RunContract.RunTypeEntry.COLUMN_DISTANCE_GROWING + " INTEGER NOT NULL, " +
                RunContract.RunTypeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RunContract.RunTypeEntry.COLUMN_USER_ID + " TEXT NOT NULL " +
                ");";

        final String SQL_CREATE_RUN_TYPE_INTERVAL_TABLE = "CREATE TABLE " + RunContract.RunTypeIntervalEntry.TABLE_NAME + " (" +
                RunContract.RunTypeIntervalEntry._ID + " TEXT PRIMARY KEY," +
                RunContract.RunTypeIntervalEntry.COLUMN_DISTANCE_TO_DO + " REAL NOT NULL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID + " TEXT NOT NULL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_EFFORT + " INTEGER NOT NULL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_ORDER + " INTEGER NOT NULL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_SPEED_ESTIMATED + " REAL NOT NULL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_SPEED_ESTIMATED + " REAL NOT NULL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO + " REAL NOT NULL, " +
                " FOREIGN KEY (" + RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID + ") REFERENCES " +
                RunContract.RunTypeEntry.TABLE_NAME + " (" + RunContract.RunTypeEntry._ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_RUN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RUN_INTERVAL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RUN_TYPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RUN_TYPE_INTERVAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RunContract.RunEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RunContract.RunIntervalEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RunContract.RunTypeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RunContract.RunTypeIntervalEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
