package fr.bischof.raphael.gothiite.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.data.RunContract;

/**
 * Adapter that merge data local data and data from Parse.com
 * Created by rbischof on 16/09/2015.
 */
public class GothiiteSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = GothiiteSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final String[] RUN_PROJECTION = {RunContract.RunEntry._ID,
            RunContract.RunEntry.COLUMN_RUN_TYPE_ID,
            RunContract.RunEntry.COLUMN_START_DATE,
            RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT,
            RunContract.RunEntry.COLUMN_AVG_SPEED};
    private static final String[] RUN_INTERVAL_PROJECTION = {RunContract.RunIntervalEntry._ID,
            RunContract.RunIntervalEntry.COLUMN_DISTANCE_DONE,
            RunContract.RunIntervalEntry.COLUMN_START_DATE,
            RunContract.RunIntervalEntry.COLUMN_END_DATE,
            RunContract.RunIntervalEntry.COLUMN_START_POSITION_LATITUDE,
            RunContract.RunIntervalEntry.COLUMN_START_POSITION_LONGITUDE,
            RunContract.RunIntervalEntry.COLUMN_END_POSITION_LATITUDE,
            RunContract.RunIntervalEntry.COLUMN_END_POSITION_LONGITUDE,
            RunContract.RunIntervalEntry.COLUMN_ORDER,
            RunContract.RunIntervalEntry.COLUMN_RUN_ID};
    private ContentResolver mContentResolver;

    public GothiiteSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = getContext().getContentResolver();
    }

    public GothiiteSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = getContext().getContentResolver();
    }


    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        if (context!=null){
            // Get an instance of the Android account manager
            AccountManager accountManager =
                    (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

            // Create the account type and default account
            Account newAccount = new Account(
                    context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

            // If the password doesn't exist, the account doesn't exist
            if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
                if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                    return null;
                }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

                onAccountCreated(newAccount, context);
            }
            return newAccount;
        }
        else{
            return null;
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        GothiiteSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser!=null){
            //TODO: Sync RunTypes
            syncRuns(currentUser);
        }
    }

    private void syncRuns(ParseUser currentUser) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Run");
        query.whereEqualTo("userId", currentUser);
        try {
            Log.d(TAG, "Je passe icei");
            List<ParseObject> runs = query.find();
            Log.d(TAG, "Je passe iceei");
            Uri runsUri = RunContract.RunEntry.buildRunsUri();
            Cursor runsData = mContentResolver.query(runsUri, RUN_PROJECTION, null, null, null);
            ArrayList<ParseObject> runsToSend = new ArrayList<>();
            ArrayList<ContentValues> runsToSave = new ArrayList<>();
            if (runsData!=null){
                //Preparing comparison by storing ids in a list
                ArrayList<String> serverCurrentRunsId = new ArrayList<>();
                ArrayList<String> localCurrentRunsId = new ArrayList<>();
                ArrayList<String> serverCurrentRunsToRetrieveId = new ArrayList<>();
                for(ParseObject serverObject:runs){
                    serverCurrentRunsId.add(serverObject.getObjectId());
                }
                runsData.moveToFirst();
                while (!runsData.isAfterLast()){
                    localCurrentRunsId.add(runsData.getString(runsData.getColumnIndex(RunContract.RunEntry._ID)));
                    if (!serverCurrentRunsId.contains(runsData.getString(runsData.getColumnIndex(RunContract.RunEntry._ID)))){
                        Calendar runDate = Calendar.getInstance();
                        runDate.setTimeInMillis(runsData.getLong(runsData.getColumnIndex(RunContract.RunEntry.COLUMN_START_DATE)));
                        ParseObject runToSend = new ParseObject("Run");
                        runToSend.add("startDate", runDate.getTime());
                        runToSend.add("averageSpeed", runsData.getDouble(runsData.getColumnIndex(RunContract.RunEntry.COLUMN_AVG_SPEED)));
                        runToSend.add("vVO2maxEquivalent", runsData.getDouble(runsData.getColumnIndex(RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT)));
                        runToSend.add("runTypeId", runsData.getString(runsData.getColumnIndex(RunContract.RunEntry.COLUMN_RUN_TYPE_ID)));
                        runToSend.add("userId", currentUser);
                        Log.d(TAG, "Je passe ici");
                        runToSend.setObjectId(runsData.getString(runsData.getColumnIndex(RunContract.RunEntry._ID)));
                        runsToSend.add(runToSend);
                    }
                    runsData.moveToNext();
                }
                runsData.close();
                for(ParseObject serverObject:runs){
                    if (!localCurrentRunsId.contains(serverObject.getObjectId())){
                        serverCurrentRunsToRetrieveId.add(serverObject.getObjectId());
                        ContentValues valuesToSave = new ContentValues();
                        valuesToSave.put(RunContract.RunEntry.COLUMN_AVG_SPEED,serverObject.getDouble("averageSpeed"));
                        valuesToSave.put(RunContract.RunEntry.COLUMN_RUN_TYPE_ID,serverObject.getParseObject("runTypeId").getObjectId());
                        valuesToSave.put(RunContract.RunEntry.COLUMN_START_DATE,serverObject.getDate("startDate").getTime());
                        valuesToSave.put(RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT,serverObject.getDouble("vVO2maxEquivalent"));
                        valuesToSave.put(RunContract.RunEntry._ID,serverObject.getObjectId());
                        runsToSave.add(valuesToSave);
                    }
                }
                Log.d(TAG,"et ici");
                ParseObject.saveAll(runsToSend);
                Log.d(TAG, "ainsi ici");
                mContentResolver.bulkInsert(runsUri, runsToSave.toArray(new ContentValues[runsToSave.size()]));
                Log.d(TAG, "mais pas ici");
                syncRunIntervals(serverCurrentRunsToRetrieveId,runsToSend);
            }

        } catch (ParseException e) {
            Log.e(TAG,"Error retrieving runs : " + e.getMessage());
        }
    }

    private void syncRunIntervals(ArrayList<String> serverCurrentRunsToRetrieveId, ArrayList<ParseObject> runsToSend) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RunInterval");
        query.whereContainedIn("runId", serverCurrentRunsToRetrieveId);
        try {
            Uri runIntervalsUri = RunContract.RunIntervalEntry.buildRunIntervalsUri();
            List<ParseObject> runIntervals = query.find();
            ArrayList<ContentValues> runIntervalsToSave = new ArrayList<>();
            ArrayList<ParseObject> runIntervalsToSend = new ArrayList<>();
            for(ParseObject serverObject:runIntervals){
                ContentValues valuesToSave = new ContentValues();
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_DISTANCE_DONE,serverObject.getDouble("distanceDone"));
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_END_DATE,serverObject.getDate("endDate").getTime());
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_END_POSITION_LATITUDE,serverObject.getDouble("endPositionLatitude"));
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_END_POSITION_LONGITUDE,serverObject.getDouble("endPositionLongitude"));
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_ORDER,serverObject.getInt("order"));
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_RUN_ID,serverObject.getParseObject("runId").getObjectId());
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_START_DATE,serverObject.getDate("startDate").getTime());
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_START_POSITION_LATITUDE,serverObject.getDouble("startPositionLatitude"));
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_START_POSITION_LONGITUDE,serverObject.getDouble("startPositionLongitude"));
                valuesToSave.put(RunContract.RunIntervalEntry._ID, serverObject.getObjectId());
                runIntervalsToSave.add(valuesToSave);
            }
            List<String> ids = new ArrayList<>();
            List<String> parameters = new ArrayList<>();
            for (ParseObject runToSend : runsToSend) {
                ids.add(runToSend.getObjectId());
                parameters.add("?");
            }
            Cursor runIntervalsData = mContentResolver.query(runIntervalsUri,RUN_INTERVAL_PROJECTION,RunContract.RunIntervalEntry.COLUMN_RUN_ID + " in (" + TextUtils.join(",", parameters) + ")",ids.toArray(new String[ids.size()]),null);
            if (runIntervalsData!=null) {
                runIntervalsData.moveToFirst();
                while (!runIntervalsData.isAfterLast()){
                    Calendar runIntervalStartDate = Calendar.getInstance();
                    runIntervalStartDate.setTimeInMillis(runIntervalsData.getLong(runIntervalsData.getColumnIndex(RunContract.RunIntervalEntry.COLUMN_START_DATE)));
                    Calendar runIntervalEndDate = Calendar.getInstance();
                    runIntervalEndDate.setTimeInMillis(runIntervalsData.getLong(runIntervalsData.getColumnIndex(RunContract.RunIntervalEntry.COLUMN_END_DATE)));
                    ParseObject runIntervalToSend = new ParseObject("RunInterval");
                    runIntervalToSend.add("startDate", runIntervalStartDate.getTime());
                    runIntervalToSend.add("endDate", runIntervalEndDate.getTime());
                    runIntervalToSend.add("endPositionLatitude", runIntervalsData.getDouble(runIntervalsData.getColumnIndex(RunContract.RunIntervalEntry.COLUMN_END_POSITION_LATITUDE)));
                    runIntervalToSend.add("endPositionLongitude", runIntervalsData.getDouble(runIntervalsData.getColumnIndex(RunContract.RunIntervalEntry.COLUMN_END_POSITION_LONGITUDE)));
                    runIntervalToSend.add("order", runIntervalsData.getInt(runIntervalsData.getColumnIndex(RunContract.RunIntervalEntry.COLUMN_ORDER)));
                    runIntervalToSend.add("distanceDone", runIntervalsData.getDouble(runIntervalsData.getColumnIndex(RunContract.RunIntervalEntry.COLUMN_DISTANCE_DONE)));
                    runIntervalToSend.add("startPositionLatitude", runIntervalsData.getDouble(runIntervalsData.getColumnIndex(RunContract.RunIntervalEntry.COLUMN_START_POSITION_LATITUDE)));
                    runIntervalToSend.add("startPositionLongitude", runIntervalsData.getDouble(runIntervalsData.getColumnIndex(RunContract.RunIntervalEntry.COLUMN_START_POSITION_LONGITUDE)));
                    runIntervalToSend.setObjectId(runIntervalsData.getString(runIntervalsData.getColumnIndex(RunContract.RunEntry._ID)));
                    for (ParseObject runToSend : runsToSend) {
                        if (runToSend.getObjectId().equals(runIntervalsData.getString(runIntervalsData.getColumnIndex(RunContract.RunIntervalEntry.COLUMN_RUN_ID)))){
                            runIntervalToSend.add("runId", runToSend);
                        }
                    }
                    runIntervalsToSend.add(runIntervalToSend);
                    runIntervalsData.moveToNext();
                }
                runIntervalsData.close();
            }
            ParseObject.saveAll(runIntervalsToSend);
            mContentResolver.bulkInsert(runIntervalsUri, runIntervalsToSave.toArray(new ContentValues[runIntervalsToSave.size()]));

        } catch (ParseException e) {
            Log.e(TAG, "Error retrieving runIntervals : " + e.getMessage());
        }
    }
}
