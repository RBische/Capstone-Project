package fr.bischof.raphael.gothiite.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.calculator.Calculator;
import fr.bischof.raphael.gothiite.data.RunContract;
import fr.bischof.raphael.gothiite.model.RunTypeInterval;
import fr.bischof.raphael.gothiite.service.OnRunningServiceUpdateListener;
import fr.bischof.raphael.gothiite.service.RunningService;
import fr.bischof.raphael.gothiite.ui.ColorPart;
import fr.bischof.raphael.gothiite.ui.IntervalView;

/**
 * A placeholder fragment containing a simple view.
 */
public class RunFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,OnRunningServiceUpdateListener {
    private static final int RUN_TYPE_LOADER = 1;
    private static final String[] RUN_TYPE_INTERVALS_PROJECTION = {RunContract.RunTypeIntervalEntry._ID,
            RunContract.RunTypeIntervalEntry.COLUMN_EFFORT,
            RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO,
            RunContract.RunTypeIntervalEntry.COLUMN_DISTANCE_TO_DO};
    private static final String[] RUN_TYPE_PROJECTION = {RunContract.RunTypeEntry._ID,
            RunContract.RunTypeEntry.COLUMN_NAME};
    private static final String EXTRA_RUN_INTERVALS = "RunIntervalsSaved";
    private static final String EXTRA_RUN_INTERVALS_TO_DO = "RunIntervalsToDo";
    @InjectView(R.id.tvRunType)
    public TextView mTvRunType;
    @InjectView(R.id.invRunType)
    public IntervalView mInvRunType;
    @InjectView(R.id.tvTimer)
    public TextView mTvTimer;
    @InjectView(R.id.tvBefore)
    public TextView mTvBefore;
    @InjectView(R.id.tvMetersBeforeNext)
    public TextView mTvMetersBeforeNext;
    @InjectView(R.id.tvEffortLeft)
    public TextView mTvEffortLeft;
    private double mVVO2max = 0;
    private String mCurrentRunTypeName;
    private ArrayList<RunTypeInterval> mRunIntervals;
    private ArrayList<RunTypeInterval> mRunIntervalsToDo;
    private RunningService mService;
    private boolean mBound = false;
    private boolean mNeedsToRefreshFromService = false;

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
        ButterKnife.inject(this, view);
        if (savedInstanceState==null){
            if(getActivity().getIntent().getData()==null){
                mNeedsToRefreshFromService = true;
            }else{
                mVVO2max = getActivity().getIntent().getDoubleExtra(CreateRunFragment.EXTRA_VVO2MAX,0);
                Cursor currentRunType = getActivity().getContentResolver().query(getActivity().getIntent().getData(),RUN_TYPE_PROJECTION,null,null,null);
                currentRunType.moveToFirst();
                if (!currentRunType.isAfterLast()){
                    mCurrentRunTypeName = currentRunType.getString(currentRunType.getColumnIndex(RunContract.RunTypeEntry.COLUMN_NAME));
                }
                getLoaderManager().initLoader(RUN_TYPE_LOADER, null, this);
            }
        }else{
            this.mRunIntervals = savedInstanceState.getParcelableArrayList(EXTRA_RUN_INTERVALS);
            this.mRunIntervalsToDo = savedInstanceState.getParcelableArrayList(EXTRA_RUN_INTERVALS_TO_DO);
        }
        if (getActivity()!=null&& getActivity() instanceof AppCompatActivity){
            Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar!=null){
                actionBar.setSubtitle(null);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

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
        mRunIntervals = new ArrayList<>();
        while (data.moveToNext()) {
            boolean effort = data.getInt(data.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_EFFORT))==1;
            double time = data.getDouble(data.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO));
            double distance = data.getDouble(data.getColumnIndex(RunContract.RunTypeIntervalEntry.COLUMN_DISTANCE_TO_DO));
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (distance==0&&effort){
                distance = Calculator.calculateDistanceNeeded(time,mVVO2max,preferences.getFloat(getString(R.string.pref_ie),-6.5f));
            }
            if (time==0){
                time = Calculator.calculateTimeNeeded(distance,mVVO2max,preferences.getFloat(getString(R.string.pref_ie),-6.5f));
            }
            mRunIntervals.add(new RunTypeInterval(time, distance, effort));
        }
        mRunIntervalsToDo=mRunIntervals;
        if(mService!=null&&!mService.isLoaded()){
            mService.loadRun(mRunIntervals,mCurrentRunTypeName, getActivity(), RunFragment.this);
        }
        data.moveToFirst();
        fillUI();
    }

    private void fillUI(){
        mTvRunType.setText(mCurrentRunTypeName);
        List<ColorPart> partsToDraw = new ArrayList<>();
        for(RunTypeInterval interval:mRunIntervals) {
            boolean effort = interval.isEffort();
            double time = interval.getTimeToDo();
            double distance = interval.getDistanceToDo();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (distance==0){
                distance = Calculator.calculateDistanceNeeded(time,mVVO2max,preferences.getFloat(getString(R.string.pref_ie),-6.5f));
            }
            if (time==0){
                time = Calculator.calculateTimeNeeded(distance,mVVO2max,preferences.getFloat(getString(R.string.pref_ie),-6.5f));
            }
            int timeInSec = (int)time;
            ColorPart part = new ColorPart(timeInSec,!effort);
            partsToDraw.add(part);
        }
        mInvRunType.updateParts(partsToDraw);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), RunningService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to StreamerBinder, cast the IBinder and get StreamerBinder instance
            RunningService.RunningBinder binder = (RunningService.RunningBinder) service;
            mService = binder.getService();
            //The intent is here only to make the bound service persistent
            Intent intent = new Intent(getActivity(), RunningService.class);
            intent.setAction(RunningService.ACTION_STAY_AWAKE);
            getActivity().startService(intent);
            if(mNeedsToRefreshFromService){
                mRunIntervals = mService.getRunIntervals();
                mRunIntervalsToDo = mService.getRunIntervalsToDo();
                mCurrentRunTypeName = mService.getCurrentRunTypeName();
                fillUI();
                mService.setOnRunningServiceUpdateListener(RunFragment.this);
                //TODO: Update currenttextviews
            }else {
                if (mRunIntervals!=null){
                    mService.loadRun(mRunIntervals,mCurrentRunTypeName, getActivity(), RunFragment.this);
                }
            }
            mService.hideNotification();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            if (mService.isActive()){
                mService.showNotification();
            }else{
                mService.stopSelf();
            }
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onTimerEnded() {

    }

    @Override
    public void onTimerStarted(long duration,final long timeStartRun, boolean effort, final RunTypeInterval nextRunTypeInterval) {
        final DecimalFormat df = new DecimalFormat("#");
        CountDownTimer cT =  new CountDownTimer(duration, 50) {
            public void onTick(long millisUntilFinished) {
                if (getActivity()!=null&&(int)millisUntilFinished/1000>0){
                    String nextRunType = getString(R.string.before_stop);
                    if (nextRunTypeInterval!=null){
                        if(nextRunTypeInterval.isEffort()){
                            nextRunType = getString(R.string.before_effort);
                        }else{
                            nextRunType = getString(R.string.before_rest);
                        }
                    }
                    //TODO: Ressources strings
                    mTvMetersBeforeNext.setText("" + df.format(mService.getDistanceRemaining()>0?mService.getDistanceRemaining():0) + " meters");
                    mTvBefore.setText(nextRunType);
                    mTvEffortLeft.setText("" + df.format(mService.getDistanceRemainingInRun()) + " meters left");
                    String v = String.format("%02d", millisUntilFinished/60000);
                    //String timeSinceStart = String.format("%02d", (System.currentTimeMillis()-timeStartRun)/1000);
                    mInvRunType.setTimeFromBeginning((int)(System.currentTimeMillis()-timeStartRun));
                    int va = (int)( (millisUntilFinished%60000)/1000);
                    mTvTimer.setText(v + ":" + String.format("%02d", va) + " seconds remaining");
                }
            }

            public void onFinish() {
                if (getActivity()!=null){
                    //mTvTimer.setText("done!");
                }
            }
        };
        cT.start();
    }

    public void stopRun() {
        if (mService!=null){
            mService.endRun();
        }
    }
}
