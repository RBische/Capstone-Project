package fr.bischof.raphael.gothiite.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Provider to fetch Runs from Gothiite App
 * Created by biche on 13/09/2015.
 */
public class RunProvider extends ContentProvider {

    public static final int RUNS = 100;
    public static final int RUN = 101;
    public static final int RUN_INTERVAL = 201;
    public static final int RUN_TYPE = 301;
    public static final int RUN_TYPE_INTERVAL = 401;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RunDbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sRunsQueryBuilder;

    static{
        sRunsQueryBuilder = new SQLiteQueryBuilder();
        sRunsQueryBuilder.setTables(
                RunContract.RunEntry.TABLE_NAME + " INNER JOIN " +
                        RunContract.RunTypeEntry.TABLE_NAME +
                        " ON " + RunContract.RunEntry.TABLE_NAME +
                        "." + RunContract.RunEntry.COLUMN_RUN_TYPE_ID +
                        " = " + RunContract.RunTypeEntry.TABLE_NAME +
                        "." + RunContract.RunTypeEntry._ID);
    }

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RunContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, RunContract.PATH_RUN, RUNS);
        matcher.addURI(authority, RunContract.PATH_RUN + "/*", RUN);
        matcher.addURI(authority, RunContract.PATH_RUN_INTERVAL + "/*", RUN_INTERVAL);
        matcher.addURI(authority, RunContract.PATH_RUN_TYPE + "/*", RUN_TYPE);
        matcher.addURI(authority, RunContract.PATH_RUN_TYPE_INTERVAL + "/*", RUN_TYPE_INTERVAL);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RunDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case RUNS:
            {
                retCursor = getRuns(projection, sortOrder);
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
    public String getType(@NonNull Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case RUNS:
                return RunContract.RunEntry.CONTENT_TYPE;
            case RUN:
                return RunContract.RunEntry.CONTENT_ITEM_TYPE;
            case RUN_INTERVAL:
                return RunContract.RunIntervalEntry.CONTENT_ITEM_TYPE;
            case RUN_TYPE:
                return RunContract.RunTypeEntry.CONTENT_ITEM_TYPE;
            case RUN_TYPE_INTERVAL:
                return RunContract.RunTypeIntervalEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RUN: {
                long _id = db.insert(RunContract.RunEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = RunContract.RunEntry.buildRunUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
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
                long _id = db.insert(RunContract.RunTypeEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = RunContract.RunTypeEntry.buildRunTypeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case RUN_TYPE_INTERVAL: {
                long _id = db.insert(RunContract.RunTypeIntervalEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = RunContract.RunTypeIntervalEntry.buildRunTypeIntervalUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
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
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}