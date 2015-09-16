package fr.bischof.raphael.gothiite.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Adapter that merge data local data and data from Parse.com
 * Created by rbischof on 16/09/2015.
 */
public class GothiiteSyncAdapter extends AbstractThreadedSyncAdapter {

    public GothiiteSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public GothiiteSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

    }
}
