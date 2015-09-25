package fr.bischof.raphael.gothiite.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Provider to fetch Runs from Gothiite App
 * Created by biche on 13/09/2015.
 */
public class RunProvider extends ContentProvider {

    public static final int RUNS = 100;
    public static final int RUN = 101;
    public static final int RUNS_WITH_RUN_TYPE = 120;
    public static final int RUN_INTERVALS = 200;
    public static final int RUN_INTERVAL = 201;
    public static final int RUN_INTERVALS_WITH_RUN = 220;
    public static final int RUN_TYPES = 300;
    public static final int RUN_TYPE = 301;
    public static final int RUN_TYPE_INTERVALS = 400;
    public static final int RUN_TYPE_INTERVAL = 401;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RunDbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sRunsQueryBuilder;
    private static final SQLiteQueryBuilder sRunIntervalsQueryBuilder;
    private static final SQLiteQueryBuilder sRunIntervalsWithRunQueryBuilder;
    private static final SQLiteQueryBuilder sRunTypesQueryBuilder;
    private static final SQLiteQueryBuilder sRunTypeIntervalsQueryBuilder;
    private static final SQLiteQueryBuilder sRunsWithRunTypeQueryBuilder;


    private static final String sRunIntervalWithRunSelection =
            RunContract.RunEntry.TABLE_NAME+
                    "." + RunContract.RunEntry._ID + " = ?";

    static{
        sRunsQueryBuilder = new SQLiteQueryBuilder();
        sRunsQueryBuilder.setTables(
                RunContract.RunEntry.TABLE_NAME );
        sRunsWithRunTypeQueryBuilder = new SQLiteQueryBuilder();
        sRunsWithRunTypeQueryBuilder.setTables(
                RunContract.RunEntry.TABLE_NAME+ " INNER JOIN " +
                        RunContract.RunTypeEntry.TABLE_NAME +
                        " ON " + RunContract.RunTypeEntry.TABLE_NAME +
                        "." + RunContract.RunTypeEntry._ID +
                        " = " + RunContract.RunEntry.TABLE_NAME +
                        "." + RunContract.RunEntry.COLUMN_RUN_TYPE_ID );
        sRunIntervalsQueryBuilder = new SQLiteQueryBuilder();
        sRunIntervalsQueryBuilder.setTables(
                RunContract.RunIntervalEntry.TABLE_NAME );
        sRunIntervalsWithRunQueryBuilder = new SQLiteQueryBuilder();
        sRunIntervalsWithRunQueryBuilder.setTables(
                RunContract.RunEntry.TABLE_NAME+ " INNER JOIN " +
                        RunContract.RunTypeEntry.TABLE_NAME +
                        " ON " + RunContract.RunTypeEntry.TABLE_NAME +
                        "." + RunContract.RunTypeEntry._ID +
                        " = " + RunContract.RunEntry.TABLE_NAME +
                        "." + RunContract.RunEntry.COLUMN_RUN_TYPE_ID+ " INNER JOIN " +
                        RunContract.RunIntervalEntry.TABLE_NAME +
                        " ON " + RunContract.RunIntervalEntry.TABLE_NAME +
                        "." + RunContract.RunIntervalEntry.COLUMN_RUN_ID +
                        " = " + RunContract.RunEntry.TABLE_NAME +
                        "." + RunContract.RunEntry._ID );
        sRunTypesQueryBuilder = new SQLiteQueryBuilder();
        sRunTypesQueryBuilder.setTables(
                RunContract.RunTypeEntry.TABLE_NAME );
        sRunTypeIntervalsQueryBuilder = new SQLiteQueryBuilder();
        sRunTypeIntervalsQueryBuilder.setTables(
                RunContract.RunTypeIntervalEntry.TABLE_NAME );
    }

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RunContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, RunContract.PATH_RUN, RUNS);
        matcher.addURI(authority, RunContract.PATH_RUN_WITH_RUN_TYPE, RUNS_WITH_RUN_TYPE);
        matcher.addURI(authority, RunContract.PATH_RUN + "/*", RUN);
        matcher.addURI(authority, RunContract.PATH_RUN_INTERVAL , RUN_INTERVALS);
        matcher.addURI(authority, RunContract.PATH_RUN_INTERVAL + "/*", RUN_INTERVAL);
        matcher.addURI(authority, RunContract.PATH_RUN_INTERVAL_WITH_RUN + "/*", RUN_INTERVALS_WITH_RUN);
        matcher.addURI(authority, RunContract.PATH_RUN_TYPE, RUN_TYPES);
        matcher.addURI(authority, RunContract.PATH_RUN_TYPE + "/*", RUN_TYPE);
        matcher.addURI(authority, RunContract.PATH_RUN_TYPE_INTERVAL, RUN_TYPE_INTERVALS);
        matcher.addURI(authority, RunContract.PATH_RUN_TYPE_INTERVAL + "/*", RUN_TYPE_INTERVAL);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RunDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case RUNS:
            {
                retCursor = getRuns(projection, sortOrder);
                break;
            }
            case RUNS_WITH_RUN_TYPE:
            {
                retCursor = getRunsWithRunType(projection, sortOrder);
                break;
            }
            case RUN_TYPES:
            {
                retCursor = getRunTypes(projection, sortOrder);
                break;
            }
            case RUN_INTERVALS:
            {
                retCursor = getRunIntervals(projection, sortOrder);
                break;
            }
            case RUN_INTERVALS_WITH_RUN:
            {
                retCursor = getRunIntervalsWithRun(uri, projection, sortOrder);
                break;
            }
            case RUN_TYPE_INTERVALS:
            {
                retCursor = getRunTypeIntervals(projection, selection, selectionArgs, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext()!=null&&getContext().getContentResolver()!=null){
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    private Cursor getRunTypeIntervals(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return sRunTypeIntervalsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getRunTypes(String[] projection, String sortOrder) {
        return sRunTypesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getRunIntervals(String[] projection, String sortOrder) {
        return sRunIntervalsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getRunIntervalsWithRun(Uri uri, String[] projection, String sortOrder) {
        String runId = RunContract.RunIntervalEntry.getRunIdFromUri(uri);
        return sRunIntervalsWithRunQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sRunIntervalWithRunSelection,
                new String[]{runId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getRunsWithRunType(String[] projection, String sortOrder) {
        return sRunsWithRunTypeQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getRuns(String[] projection, String sortOrder) {
        return sRunsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case RUNS:
                return RunContract.RunEntry.CONTENT_TYPE;
            case RUN:
                return RunContract.RunEntry.CONTENT_ITEM_TYPE;
            case RUN_INTERVALS:
                return RunContract.RunIntervalEntry.CONTENT_TYPE;
            case RUN_INTERVAL:
                return RunContract.RunIntervalEntry.CONTENT_ITEM_TYPE;
            case RUN_INTERVALS_WITH_RUN:
                return RunContract.RunIntervalEntry.CONTENT_TYPE;
            case RUN_TYPES:
                return RunContract.RunTypeEntry.CONTENT_TYPE;
            case RUN_TYPE:
                return RunContract.RunTypeEntry.CONTENT_ITEM_TYPE;
            case RUN_TYPE_INTERVALS:
                return RunContract.RunTypeIntervalEntry.CONTENT_ITEM_TYPE;
            case RUN_TYPE_INTERVAL:
                return RunContract.RunTypeIntervalEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RUN: {
                db.insert(RunContract.RunEntry.TABLE_NAME, null, contentValues);
                returnUri = RunContract.RunEntry.buildRunUri(contentValues.get(RunContract.RunEntry._ID).toString());
                break;
            }
            case RUN_INTERVAL: {
                long _id = db.insert(RunContract.RunIntervalEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = RunContract.RunIntervalEntry.buildRunIntervalUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case RUN_TYPE: {
                db.insert(RunContract.RunTypeEntry.TABLE_NAME, null, contentValues);
                returnUri = RunContract.RunTypeEntry.buildRunTypeUri(contentValues.getAsString(RunContract.RunTypeEntry._ID));
                break;
            }
            case RUN_TYPE_INTERVALS: {
                db.insert(RunContract.RunTypeIntervalEntry.TABLE_NAME, null, contentValues);
                returnUri = RunContract.RunTypeIntervalEntry.buildRunTypeIntervalsUri();
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext()!=null&&getContext().getContentResolver()!=null){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RUNS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RunContract.RunEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (getContext()!=null){
                    getContext().getContentResolver().notifyChange(RunContract.RunEntry.buildRunsWithRunTypeUri(), null);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCount;
            case RUN_INTERVALS:
                db.beginTransaction();
                int returnCountInterval = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RunContract.RunIntervalEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountInterval++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (getContext()!=null){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCountInterval;
            case RUN_TYPES:
                db.beginTransaction();
                int returnCountType = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RunContract.RunTypeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountType++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (getContext()!=null){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCountType;
            case RUN_TYPE_INTERVALS:
                db.beginTransaction();
                int returnCountTypeInterval = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RunContract.RunTypeIntervalEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountTypeInterval++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (getContext()!=null){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCountTypeInterval;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case RUN_TYPE_INTERVAL: {
                String selection = RunContract.RunTypeIntervalEntry._ID +"=?";
                String[] selectionArgs = new String[]{RunContract.RunTypeIntervalEntry.getRunTypeIntervalIdFromUri(uri)};
                rowsDeleted = db.delete(
                        RunContract.RunTypeIntervalEntry.TABLE_NAME, selection, selectionArgs);
                if (getContext()!=null){
                    getContext().getContentResolver().notifyChange(RunContract.RunTypeIntervalEntry.buildRunTypeIntervalsUri(), null);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0&& getContext()!=null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case RUN_TYPE: {
                String whereClause = RunContract.RunTypeEntry._ID+"=?";
                String[] whereArgs = new String[]{RunContract.RunTypeEntry.getRunTypeIdFromUri(uri)};
                db.update(RunContract.RunTypeEntry.TABLE_NAME, contentValues, whereClause,whereArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext()!=null&&getContext().getContentResolver()!=null){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }
}
