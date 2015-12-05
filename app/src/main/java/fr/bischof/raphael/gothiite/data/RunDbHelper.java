package fr.bischof.raphael.gothiite.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.UUID;

import fr.bischof.raphael.gothiite.R;

/**
 * Manages a local database for run data.
 * Created by biche on 13/09/2015.
 */
public class RunDbHelper  extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 11;
    private Context mContext;

    static final String DATABASE_NAME = "run.db";

    public RunDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_RUN_TABLE = "CREATE TABLE " + RunContract.RunEntry.TABLE_NAME + " (" +
                RunContract.RunEntry._ID + " TEXT PRIMARY KEY," +
                RunContract.RunEntry.COLUMN_AVG_SPEED + " REAL NOT NULL, " +
                RunContract.RunEntry.COLUMN_RUN_TYPE_ID + " TEXT NOT NULL, " +
                RunContract.RunEntry.COLUMN_START_DATE + " INTEGER NOT NULL, " +
                RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT + " REAL NOT NULL, " +
                " FOREIGN KEY (" + RunContract.RunEntry.COLUMN_RUN_TYPE_ID + ") REFERENCES " +
                RunContract.RunTypeEntry.TABLE_NAME + " (" + RunContract.RunTypeEntry._ID + "));";

        final String SQL_CREATE_RUN_INTERVAL_TABLE = "CREATE TABLE " + RunContract.RunIntervalEntry.TABLE_NAME + " (" +
                RunContract.RunIntervalEntry._ID + " TEXT PRIMARY KEY," +

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
                RunContract.RunTypeEntry.COLUMN_ICON + " TEXT NOT NULL, " +
                RunContract.RunTypeEntry.COLUMN_NAME + " TEXT NOT NULL " +
                ");";

        final String SQL_CREATE_RUN_TYPE_INTERVAL_TABLE = "CREATE TABLE " + RunContract.RunTypeIntervalEntry.TABLE_NAME + " (" +
                RunContract.RunTypeIntervalEntry._ID + " TEXT PRIMARY KEY," +
                RunContract.RunTypeIntervalEntry.COLUMN_DISTANCE_TO_DO + " REAL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID + " TEXT NOT NULL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_EFFORT + " INTEGER NOT NULL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_ORDER + " INTEGER NOT NULL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_SPEED_ESTIMATED + " REAL, " +
                RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO + " REAL, " +
                " FOREIGN KEY (" + RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID + ") REFERENCES " +
                RunContract.RunTypeEntry.TABLE_NAME + " (" + RunContract.RunTypeEntry._ID + "));";

        final String SQL_CREATE_DELETE_TABLE = "CREATE TABLE " + RunContract.DeleteEntry.TABLE_NAME + " (" +
                RunContract.DeleteEntry._ID + " TEXT PRIMARY KEY," +
                RunContract.DeleteEntry.COLUMN_PK_COLUMN_NAME + " TEXT," +
                RunContract.DeleteEntry.COLUMN_ID + " TEXT, " +
                RunContract.DeleteEntry.COLUMN_NAME + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_RUN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RUN_INTERVAL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RUN_TYPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RUN_TYPE_INTERVAL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DELETE_TABLE);
        fillWithBaseData(sqLiteDatabase);
    }

    private void fillWithBaseData(SQLiteDatabase sqLiteDatabase) {
        addTabataRunType(sqLiteDatabase);
        addLegerTestRunType(sqLiteDatabase);
    }

    private void addLegerTestRunType(SQLiteDatabase sqLiteDatabase) {
        ContentValues tabataRunType = new ContentValues();
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_CAN_BE_DELETED, false);
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_DESCRIPTION, mContext.getString(R.string.leger_test_description));
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_DISTANCE_GROWING, true);
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_NAME, mContext.getString(R.string.leger_test_name));
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_ICON, mContext.getString(R.string.icon_leger_test));
        tabataRunType.put(RunContract.RunTypeEntry._ID, "739c19d4-f987-4c87-a160-6de74017a3d1");
        sqLiteDatabase.insert(RunContract.RunTypeEntry.TABLE_NAME, null, tabataRunType);
        long[] timeToDo = new long[]{128500,135000,120000,126000,130800,120000,124600,128600,120000,123800,127100,120000,123100,126000,120000,122700,125200,120000,122400,124600};
        int[] distanceToDo = new int[]{250,300,300,350,400,400,450,500,500,550,600,600,650,700,700,750,800,800,850,900};
        int[] speedEstimated = new int[]{7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
        for(int i =0;i<20;i++){
            ContentValues tabataRunTypeInterval = new ContentValues();
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_EFFORT, true);
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_ORDER, i);
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID, "739c19d4-f987-4c87-a160-6de74017a3d1");
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO, timeToDo[i]);
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_DISTANCE_TO_DO, distanceToDo[i]);
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_SPEED_ESTIMATED, speedEstimated[i]);
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry._ID, UUID.randomUUID().toString());
            sqLiteDatabase.insert(RunContract.RunTypeIntervalEntry.TABLE_NAME, null, tabataRunTypeInterval);
        }
    }

    private void addTabataRunType(SQLiteDatabase sqLiteDatabase) {
        ContentValues tabataRunType = new ContentValues();
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_CAN_BE_DELETED, false);
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_DESCRIPTION, mContext.getString(R.string.tabata_description));
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_DISTANCE_GROWING, false);
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_NAME, mContext.getString(R.string.tabata));
        tabataRunType.put(RunContract.RunTypeEntry.COLUMN_ICON, mContext.getString(R.string.icon_tabata));
        tabataRunType.put(RunContract.RunTypeEntry._ID, "db45f67a-70eb-401d-9e01-4aa42f1220d2");
        sqLiteDatabase.insert(RunContract.RunTypeEntry.TABLE_NAME, null, tabataRunType);
        for(int i =0;i<15;i++){
            int timeToDo = 10000;
            boolean effort = false;
            if (i%2==0){
                timeToDo = 20000;
                effort = true;
            }
            ContentValues tabataRunTypeInterval = new ContentValues();
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_EFFORT, effort);
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_ORDER, i);
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID, "db45f67a-70eb-401d-9e01-4aa42f1220d2");
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO, timeToDo);
            tabataRunTypeInterval.put(RunContract.RunTypeIntervalEntry._ID, UUID.randomUUID().toString());
            sqLiteDatabase.insert(RunContract.RunTypeIntervalEntry.TABLE_NAME, null, tabataRunTypeInterval);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RunContract.RunEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RunContract.RunIntervalEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RunContract.RunTypeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RunContract.RunTypeIntervalEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RunContract.DeleteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
