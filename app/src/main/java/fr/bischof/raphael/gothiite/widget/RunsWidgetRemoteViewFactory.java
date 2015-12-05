package fr.bischof.raphael.gothiite.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Calendar;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.activity.MainActivity;
import fr.bischof.raphael.gothiite.data.RunContract;
import fr.bischof.raphael.gothiite.dateformat.DateToShowFormat;

/**
 * Factory that handles views creation
 * Created by biche on 16/08/2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RunsWidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    private Cursor mData;
    private static final String[] RUNS_PROJECTION = {RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry._ID,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_RUN_TYPE_ID,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_START_DATE,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT,
            RunContract.RunEntry.TABLE_NAME+"."+RunContract.RunEntry.COLUMN_AVG_SPEED,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_NAME,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_ICON,
            RunContract.RunTypeEntry.TABLE_NAME+"."+RunContract.RunTypeEntry.COLUMN_DESCRIPTION};

    public RunsWidgetRemoteViewFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // Get today's data from the ContentProvider
        final long identityToken = Binder.clearCallingIdentity();
        String sortOrder = RunContract.RunEntry.COLUMN_START_DATE + " DESC";
        Uri runsWithRunTypeUri = RunContract.RunEntry.buildRunsWithRunTypeUri();
        mData = mContext.getContentResolver().query(runsWithRunTypeUri,RUNS_PROJECTION,null,null,sortOrder);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        mData.close();
    }

    @Override
    public int getCount() {
        return mData.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mData == null || !mData.moveToPosition(position)) {
            return null;
        }
        Calendar runDate = Calendar.getInstance();
        runDate.setTimeInMillis(mData.getLong(mData.getColumnIndex(RunContract.RunEntry.COLUMN_START_DATE)));
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_adapter_runs_list_item);
        views.setTextViewText(R.id.tvTitle,new DateToShowFormat().format(runDate.getTime()));
        views.setTextViewText(R.id.tvSubtitle, "" + mData.getLong(mData.getColumnIndex(RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT)) + " " + mContext.getString(R.string.kmh_vvo2max));
        views.setTextViewText(R.id.tvOtherDetail, mData.getString(mData.getColumnIndex(RunContract.RunTypeEntry.COLUMN_NAME)));
        views.setImageViewResource(R.id.ivRun,mContext.getResources().getIdentifier(mData.getString(mData.getColumnIndex(RunContract.RunTypeEntry.COLUMN_ICON)), "drawable", mContext.getPackageName()));
        final Intent fillInIntent = new Intent();
        String runId = mData.getString(mData.getColumnIndex(RunContract.RunEntry._ID));
        Uri runUri = RunContract.RunEntry.buildRunUri(runId);
        fillInIntent.setData(runUri);
        fillInIntent.putExtra(MainActivity.EXTRA_RUN_ID_TO_SHOW, runId);
        views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_adapter_runs_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
