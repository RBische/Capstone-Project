package fr.bischof.raphael.gothiite.service;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.UUID;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.activity.MainActivity;
import fr.bischof.raphael.gothiite.calculator.Calculator;
import fr.bischof.raphael.gothiite.data.RunContract;
import fr.bischof.raphael.gothiite.model.RunInterval;
import fr.bischof.raphael.gothiite.model.RunTypeInterval;
import fr.bischof.raphael.gothiite.speech.MediaManager;
import fr.bischof.raphael.gothiite.speech.Synthesizer;

/**
 * Service running during a session. This service maintains GPS coordinates retrieving, plays sound when its needed, etc...
 * Created by biche on 29/11/2015.
 */
public class RunningService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long LENGTH_SHORT_VIBRATION = 400;
    private static final long LENGTH_LONG_VIBRATION = 800;
    private static final long LENGTH_PAUSE_VIBRATION = 200;
    private MediaManager mMediaManager;
    public static final String ACTION_STAY_AWAKE = "ActionToForceServiceToStayAwake";
    public static final String ACTION_SHOW_UI_FROM_RUN = "ShowUIFromRun";
    public static final String EXTRA_VVO2MAX = "ExtraVvo2max";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private static final int NOTIFICATION_ID = 1;
    // Binder given to clients
    private final IBinder mBinder = new RunningBinder();
    private ArrayList<RunTypeInterval> mRunIntervals;
    private ArrayList<RunTypeInterval> mRunIntervalsToDo;
    private int mNotificationCount = 0;
    private boolean mNotificationShown = true;
    private double mCurrentDistanceInInterval = 0;
    private Location mLastLocationOfInterval = null;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private Activity mBoundActivity;
    private OnRunningServiceUpdateListener mBoundListener;
    private ArrayList<RunInterval> mCurrentIntervalsDone = new ArrayList<>();
    private long mCurrentStartTime;
    private long mRunStartTime;
    private String mCurrentRunTypeName;
    private boolean mLoaded = false;
    private Location mStartPosition;
    private Location mEndPosition;
    private String mRunTypeId;
    private double mVVO2max;
    private boolean mHalfIntervalSoundPlayed = false;
    private boolean m10SecondsBeforeEndIntervalPlayed = false;
    private boolean m5SecondsBeforeEndIntervalPlayed = false;
    private CountDownTimer mCountDownTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaManager = new MediaManager(getBaseContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //When the fragment binding to the service stops, we also stops the service except if the mediaplayer is playing
        if(mRunIntervalsToDo.size()!=0){
            showNotification();
        }else{
            stopSelf();
        }
        return super.onUnbind(intent);
    }

    /**
     * Starts a run
     * @param runTypeId Runtype that has to be started
     * @param runIntervals RunTypeIntervals of the starting run
     * @param currentRunTypeName RunType name of the starting run
     * @param vVO2max User defined vVO2max
     * @param boundActivity Activity that called the bound service
     * @param listener Listener of the {@link OnRunningServiceUpdateListener} events
     */
    public void loadRun(String runTypeId, ArrayList<RunTypeInterval> runIntervals, String currentRunTypeName, double vVO2max, Activity boundActivity, OnRunningServiceUpdateListener listener) {
        this.mVVO2max = vVO2max;
        this.mRunTypeId = runTypeId;
        this.mRunIntervals = runIntervals;
        this.mRunIntervalsToDo = runIntervals;
        this.mBoundActivity = boundActivity;
        this.mCurrentRunTypeName = currentRunTypeName;
        this.mBoundListener = listener;
        this.mLoaded = true;
        startRun();
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    public void setOnRunningServiceUpdateListener(OnRunningServiceUpdateListener listener) {
        this.mBoundListener = listener;
    }

    private void startRun() {
        mCurrentStartTime = System.currentTimeMillis();
        mRunStartTime = mCurrentStartTime;
        notifyStartOfTheRun();
        startTimer();
        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void startTimer() {
        /*Pattern vibration (if effort) :
            start : bzzzz
            10s before end : bzz bzz
            5/4/3/2/1s before end : bzz
            end : bzzzz
          Pattern sound :
            &noneffort start : Time left & distance left
            &noneffort half run : Time left & distance left
            &noneffort 10s before end : time left + distance left
            &noneffort 5s before end : distance left
         */
        if (mRunIntervalsToDo!=null&&mRunIntervalsToDo.size()>0){
            if (mBoundListener !=null){
                RunTypeInterval interval = null;
                if (mRunIntervalsToDo.size()>1){
                    interval = mRunIntervalsToDo.get(1);
                }
                mBoundListener.onTimerStarted((long) mRunIntervalsToDo.get(0).getTimeToDo(), mRunStartTime, mRunIntervalsToDo.get(0).isEffort(), interval);
            }
            if(mNotificationShown){
                showNotification();
            }
            final long timeToDoInThisInterval = (long) mRunIntervalsToDo.get(0).getTimeToDo();
            final long distanceToDoInThisInterval = (long) mRunIntervalsToDo.get(0).getDistanceToDo();
            final boolean effort = mRunIntervalsToDo.get(0).isEffort();
            notifyStartInterval(effort, mRunIntervalsToDo.get(0).getTimeToDo(), mRunIntervalsToDo.get(0).getDistanceToDo());
            mCountDownTimer =  new CountDownTimer(timeToDoInThisInterval - 200, 1000) {
                public void onTick(long millisUntilFinished) {
                    if(!mHalfIntervalSoundPlayed &&timeToDoInThisInterval/2>(millisUntilFinished+200)&&timeToDoInThisInterval/2>15000){
                        mHalfIntervalSoundPlayed = true;
                        notifyHalfInterval(effort, millisUntilFinished, distanceToDoInThisInterval - mCurrentDistanceInInterval);
                    }else if (!m10SecondsBeforeEndIntervalPlayed &&(millisUntilFinished+200)<10000){
                        m10SecondsBeforeEndIntervalPlayed = true;
                        notify10SecondsBeforeEndInterval(effort, millisUntilFinished, distanceToDoInThisInterval - mCurrentDistanceInInterval);
                    }else if (!m5SecondsBeforeEndIntervalPlayed &&(millisUntilFinished+200)<5000){
                        m5SecondsBeforeEndIntervalPlayed = true;
                        notify5SecondsBeforeEndInterval(effort, distanceToDoInThisInterval - mCurrentDistanceInInterval);
                    }
                    if ((millisUntilFinished+200)<=5000){
                        notifyEach5LastsSecondsBeforeEndInterval();
                    }
                    if (mNotificationShown){
                        showNotification();
                    }
                }

                public void onFinish() {
                    notifyEnd();
                    timerEnded();
                }
            };
            mCountDownTimer.start();
        }else{
            endRun(true);
        }
    }

    private void timerEnded() {
        try {
            long timeToWait = ((long)mRunIntervalsToDo.get(0).getTimeToDo()-System.currentTimeMillis()-mCurrentStartTime);
            if (timeToWait>0){
                Thread.sleep(timeToWait);
            }
        } catch (InterruptedException e) {
            //Not a problem if happens
        }
        if (mBoundListener !=null){
            mBoundListener.onTimerEnded();
        }
        if (mRunIntervalsToDo.get(0).isEffort()){
            mCurrentIntervalsDone.add(new RunInterval(mCurrentDistanceInInterval,mCurrentStartTime,System.currentTimeMillis(),mStartPosition,mEndPosition));
        }
        mRunIntervalsToDo.remove(0);
        mCurrentDistanceInInterval = 0;
        mHalfIntervalSoundPlayed = false;
        m10SecondsBeforeEndIntervalPlayed = false;
        m5SecondsBeforeEndIntervalPlayed = false;
        mStartPosition = null;
        mEndPosition = null;
        mCurrentStartTime = System.currentTimeMillis();
        if (mRunIntervalsToDo.size()==0){
            endRun(true);
        }else{
            startTimer();
        }
    }

    public boolean isActive() {
        return mRunIntervalsToDo != null && mRunIntervalsToDo.size() > 0;
    }

    public void showNotification() {
        if (mRunIntervalsToDo.size()>0){
            RunTypeInterval currentInterval = mRunIntervalsToDo.get(0);
            Intent intentStreamingUI = new Intent(getBaseContext(), MainActivity.class);
            intentStreamingUI.setAction(ACTION_SHOW_UI_FROM_RUN);
            intentStreamingUI.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intentStreamingUI.putExtra(EXTRA_VVO2MAX,mVVO2max);
            PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 0,
                    intentStreamingUI,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            Notification notification;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Notification.Builder builder = new Notification.Builder(getApplicationContext())
                        .setContentText(String.format("%02d", (((long)currentInterval.getTimeToDo() - (System.currentTimeMillis() - mCurrentStartTime)) / 1000)) + "s " + getBaseContext().getString(R.string.left))
                                .setContentTitle(getApplicationContext().getString(R.string.now_running))
                                .setContentIntent(pi);
                builder.setSmallIcon(R.drawable.icon_notification);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                notification = builder.build();
            }else{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setContentText(String.format("%02d", (((long)currentInterval.getTimeToDo() - (System.currentTimeMillis() - mCurrentStartTime)) / 1000)) + "s " + getBaseContext().getString(R.string.left))
                                .setContentTitle(getApplicationContext().getString(R.string.now_running))
                                .setContentIntent(pi);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                builder.setSmallIcon(R.drawable.icon_notification);
                notification = builder.build();
            }
            notification.tickerText = getApplicationContext().getString(R.string.now_running);
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(NOTIFICATION_ID, notification);
            mNotificationCount+=1;
            mNotificationShown = true;
        }
    }

    public void hideNotification() {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        while (mNotificationCount>0){
            manager.cancel(NOTIFICATION_ID);
            mNotificationCount--;
        }
        stopForeground(true);
        mNotificationShown = false;
    }

    public void endRun(boolean withSave){
        if (withSave){
            //Preparing Run to save
            UUID runId = UUID.randomUUID();
            String runTypeId = mRunTypeId;
            long timeRunned = 0;
            long distanceRunned = 0;
            double speedLegerTest = 0;
            for(RunInterval interval:mCurrentIntervalsDone){
                timeRunned += interval.getEndTime()-interval.getStartTime();
                distanceRunned+= interval.getDistanceDone();
                if (runTypeId.equalsIgnoreCase("739c19d4-f987-4c87-a160-6de74017a3d1")){
                    speedLegerTest = interval.getDistanceDone()/((interval.getEndTime()-interval.getStartTime())/3600);
                }
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            float ie = Float.parseFloat(preferences.getString(getString(R.string.pref_ie), "-6.5"));
            double vVo2Max = Calculator.calculateVV02max(timeRunned,distanceRunned,ie);
            //In case of a leger test, the last speed represents the vVO2Max
            if (runTypeId.equalsIgnoreCase("739c19d4-f987-4c87-a160-6de74017a3d1")){
                vVo2Max = speedLegerTest;
            }
            double avg_speed = 0;
            if (timeRunned>0){
                avg_speed = distanceRunned/(timeRunned/3600);
            }
            Uri runsUri = RunContract.RunEntry.buildRunsUri();
            ArrayList<ContentValues> runsToSave = new ArrayList<>();
            ContentValues valuesToSaveForRun = new ContentValues();
            valuesToSaveForRun.put(RunContract.RunEntry.COLUMN_AVG_SPEED, avg_speed);
            valuesToSaveForRun.put(RunContract.RunEntry.COLUMN_RUN_TYPE_ID, runTypeId);
            valuesToSaveForRun.put(RunContract.RunEntry.COLUMN_START_DATE, mRunStartTime);
            valuesToSaveForRun.put(RunContract.RunEntry.COLUMN_VVO2MAX_EQUIVALENT, vVo2Max);
            valuesToSaveForRun.put(RunContract.RunEntry._ID, runId.toString());
            runsToSave.add(valuesToSaveForRun);
            getBaseContext().getContentResolver().bulkInsert(runsUri, runsToSave.toArray(new ContentValues[runsToSave.size()]));
            Uri runIntervalsUri = RunContract.RunIntervalEntry.buildRunIntervalsUri();
            ArrayList<ContentValues> runIntervalsToSave = new ArrayList<>();
            int i=0;
            for(RunInterval interval:mCurrentIntervalsDone){
                //Preparing RunIntervals to save
                ContentValues valuesToSave = new ContentValues();
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_DISTANCE_DONE,interval.getDistanceDone());
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_END_DATE,interval.getEndTime());
                if (interval.getEndPosition()!=null){
                    valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_END_POSITION_LATITUDE,interval.getEndPosition().getLatitude());
                    valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_END_POSITION_LONGITUDE,interval.getEndPosition().getLongitude());
                }
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_ORDER, i);
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_RUN_ID, runId.toString());
                valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_START_DATE, interval.getStartTime());
                if (interval.getStartPosition()!=null){
                    valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_START_POSITION_LATITUDE, interval.getStartPosition().getLatitude());
                    valuesToSave.put(RunContract.RunIntervalEntry.COLUMN_START_POSITION_LONGITUDE, interval.getStartPosition().getLongitude());
                }
                valuesToSave.put(RunContract.RunIntervalEntry._ID, UUID.randomUUID().toString());
                runIntervalsToSave.add(valuesToSave);
                i++;
            }
            getBaseContext().getContentResolver().bulkInsert(runIntervalsUri, runIntervalsToSave.toArray(new ContentValues[runIntervalsToSave.size()]));
            mBoundListener.onRunFinished();
        }
        if(mNotificationShown){
            hideNotification();
        }
        if(mCountDownTimer!=null){
            mCountDownTimer.cancel();
        }
        stopLocationUpdates();
        stopSelf();
    }

    public ArrayList<RunTypeInterval> getRunIntervals() {
        return mRunIntervals;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocationOfInterval!=null){
            mCurrentDistanceInInterval += mLastLocationOfInterval.distanceTo(location);
        }
        if (mStartPosition==null){
            mStartPosition = location;
        }
        mEndPosition = location;
        mLastLocationOfInterval = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
        mRequestingLocationUpdates = false;
    }

    public String getCurrentRunTypeName() {
        return mCurrentRunTypeName;
    }

    public double getDistanceRemaining() {
        if (mRunIntervalsToDo.size()>0){
            return mRunIntervalsToDo.get(0).getDistanceToDo() - mCurrentDistanceInInterval;
        }else{
            return 0;
        }
    }

    public double getDistanceRemainingInRun() {
        double distanceRemaining = 0;
        for(RunTypeInterval interval:mRunIntervalsToDo){
            distanceRemaining += interval.getDistanceToDo();
        }
        return distanceRemaining - mCurrentDistanceInInterval;
    }

    public void forceRefresh() {
        if (mRunIntervalsToDo!=null&&mRunIntervalsToDo.size()>0) {
            if (mBoundListener != null) {
                RunTypeInterval interval = null;
                if (mRunIntervalsToDo.size() > 1) {
                    interval = mRunIntervalsToDo.get(1);
                }
                mBoundListener.onTimerStarted((long) mRunIntervalsToDo.get(0).getTimeToDo()-(System.currentTimeMillis()-mCurrentStartTime), mRunStartTime, mRunIntervalsToDo.get(0).isEffort(), interval);
            }
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RunningBinder extends Binder {
        public RunningService getService() {
            // Return this instance of LocalService so clients can call public methods
            return RunningService.this;
        }
    }

    private void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(mBoundActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mBoundActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mRequestingLocationUpdates = true;
        }
    }

    private void notifyStartOfTheRun() {
        /*
        start : bzzzz*/
        vibrate(LENGTH_LONG_VIBRATION);
    }

    private void notifyStartInterval(boolean effort, double timeToDo, double distanceToDo) {
        /*
        &effort start : Time left & distance left*/
        if (effort){
            Synthesizer synthetizer;
            if (timeToDo/60000>=1){
                synthetizer = new Synthesizer(this,R.string.mp3_minutes_seconds_left,(int)(timeToDo/60000),(int)((timeToDo%60000)/1000));
            }else{
                synthetizer = new Synthesizer(this,R.string.mp3_seconds_left,(int)(timeToDo/1000));
            }
            String[] soundsTime = synthetizer.getSynthesizedParts();
            mMediaManager.addToQueue(soundsTime);
            if(distanceToDo>1000){
                synthetizer = new Synthesizer(this,R.string.mp3_kilometers_left,(int)(distanceToDo/1000));
            }else{
                synthetizer = new Synthesizer(this,R.string.mp3_meters_left,(int)distanceToDo);
            }
            String[] soundsDistance = synthetizer.getSynthesizedParts();
            mMediaManager.addToQueue(soundsDistance);
            mMediaManager.playSoundsInQueue();
        }
    }

    private void notifyHalfInterval(boolean effort, double timeToDo, double distanceToDo) {
        //half run : Time left & distance left
        if (effort){
            Synthesizer synthetizer;
            if (timeToDo/60000>=1){
                synthetizer = new Synthesizer(this,R.string.mp3_minutes_seconds_left,(int)(timeToDo/60000),(int)((timeToDo%60000)/1000));
            }else{
                synthetizer = new Synthesizer(this,R.string.mp3_seconds_left,(int)(timeToDo/1000));
            }
            String[] soundsTime = synthetizer.getSynthesizedParts();
            mMediaManager.addToQueue(soundsTime);
            if(distanceToDo>1000){
                synthetizer = new Synthesizer(this,R.string.mp3_kilometers_left,(int)(distanceToDo/1000));
            }else{
                synthetizer = new Synthesizer(this,R.string.mp3_meters_left,(int)distanceToDo);
            }
            String[] soundsDistance = synthetizer.getSynthesizedParts();
            mMediaManager.addToQueue(soundsDistance);
            mMediaManager.playSoundsInQueue();
        }
    }

    private void notify10SecondsBeforeEndInterval(boolean effort, double timeToDo, double distanceToDo) {
        /*
        &effort 10s before end : time left + distance left
        10s before end : bzz bzz*/
        vibrate(LENGTH_SHORT_VIBRATION,LENGTH_PAUSE_VIBRATION,LENGTH_SHORT_VIBRATION);
        if (effort){
            Synthesizer synthetizer;
            if (timeToDo/60000>=1){
                synthetizer = new Synthesizer(this,R.string.mp3_minutes_seconds_left,(int)(timeToDo/60000),(int)((timeToDo%60000)/1000));
            }else{
                synthetizer = new Synthesizer(this,R.string.mp3_seconds_left,(int)(timeToDo/1000));
            }
            String[] soundsTime = synthetizer.getSynthesizedParts();
            mMediaManager.addToQueue(soundsTime);
            if(distanceToDo>1000){
                synthetizer = new Synthesizer(this,R.string.mp3_kilometers_left,(int)(distanceToDo/1000));
            }else{
                synthetizer = new Synthesizer(this,R.string.mp3_meters_left,(int)distanceToDo);
            }
            String[] soundsDistance = synthetizer.getSynthesizedParts();
            mMediaManager.addToQueue(soundsDistance);
            mMediaManager.playSoundsInQueue();
        }
    }

    private void notify5SecondsBeforeEndInterval(boolean effort, double distanceToDo) {
        //&effort 5s before end : distance left
        if (effort){
            Synthesizer synthetizer;
            if(distanceToDo>1000){
                synthetizer = new Synthesizer(this,R.string.mp3_kilometers_left,(int)(distanceToDo/1000));
            }else{
                synthetizer = new Synthesizer(this,R.string.mp3_meters_left,(int)distanceToDo);
            }
            String[] soundsDistance = synthetizer.getSynthesizedParts();
            mMediaManager.addToQueue(soundsDistance);
            mMediaManager.playSoundsInQueue();
        }
    }

    private void notifyEach5LastsSecondsBeforeEndInterval() {
        //5/4/3/2/1s before end : bzz
        vibrate(LENGTH_SHORT_VIBRATION);
    }

    private void notifyEnd() {
        //end : bzzzz
        vibrate(LENGTH_LONG_VIBRATION);
    }

    private void vibrate(long... durations){
        long[] vibrationsPattern = new long[]{0};
        vibrationsPattern = generalConcatAll(vibrationsPattern,durations);
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
        v.vibrate(vibrationsPattern, -1);
    }

    private long[] generalConcatAll(long[]... jobs) {
        int len = 0;
        for (final long[] job : jobs) {
            len += job.length;
        }

        final long[] result = new long[len];

        int currentPos = 0;
        for (final long[] job : jobs) {
            System.arraycopy(job, 0, result, currentPos, job.length);
            currentPos += job.length;
        }

        return result;
    }
}
