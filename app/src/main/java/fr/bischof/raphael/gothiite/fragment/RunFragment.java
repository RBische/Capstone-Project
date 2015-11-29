package fr.bischof.raphael.gothiite.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.calculator.Calculator;
import fr.bischof.raphael.gothiite.data.RunContract;
import fr.bischof.raphael.gothiite.model.RunTypeInterval;
import fr.bischof.raphael.gothiite.ui.ColorPart;
import fr.bischof.raphael.gothiite.ui.IntervalView;

/**
 * A placeholder fragment containing a simple view.
 */
public class RunFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int RUN_TYPE_LOADER = 1;
    private static final String[] RUN_TYPE_INTERVALS_PROJECTION = {RunContract.RunTypeIntervalEntry._ID,
            RunContract.RunTypeIntervalEntry.COLUMN_EFFORT,
            RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO,
            RunContract.RunTypeIntervalEntry.COLUMN_DISTANCE_TO_DO};
    @InjectView(R.id.invRunType)
    public IntervalView mInvRunType;
    private double mVVO2max = 0;

    public RunFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(RUN_TYPE_LOADER, null, this);
    }

    /*difficulté des séances en fonction du nombre de période de run

    2 tableaux différents, un course rapide, un autre course longue

    ajouter niveau de difficulté des séances*/


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RunContract.RunTypeIntervalEntry.TABLE_NAME+"."+RunContract.RunTypeIntervalEntry.COLUMN_ORDER + " ASC";
        Uri uri = RunContract.RunTypeIntervalEntry.buildRunTypeIntervalsUri();
        String runTypeId = RunContract.RunTypeEntry.getRunTypeIdFromUri(getActivity().getIntent().getData());
        return new CursorLoader(getActivity(),
                uri,
                RUN_TYPE_INTERVALS_PROJECTION,
                RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID + "= ?",
                new String[]{runTypeId},
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<RunTypeInterval> runIntervals = new ArrayList<>();
        List<ColorPart> partsToDraw = new ArrayList<>();
        while (data.moveToNext()) {
            boolean effort = data.getInt(data.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_EFFORT))==1;
            double time = data.getDouble(data.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO));
            double distance = data.getDouble(data.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_DISTANCE_TO_DO));
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (distance==0){
                distance = Calculator.calculateDistanceNeeded(time,mVVO2max,preferences.getFloat(getString(R.string.pref_ie),-6.5f));
            }
            if (time==0){
                time = Calculator.calculateTimeNeeded(distance,mVVO2max,preferences.getFloat(getString(R.string.pref_ie),-6.5f));
            }
            int timeInSec = (int)time;
            ColorPart part = new ColorPart(timeInSec,!effort);
            runIntervals.add(new RunTypeInterval(time,distance,effort));
            partsToDraw.add(part);
        }
        mInvRunType.updateParts(partsToDraw);
        data.moveToFirst();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
