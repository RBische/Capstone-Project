package fr.bischof.raphael.gothiite.service;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
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
import java.util.Timer;
import java.util.TimerTask;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.activity.MainActivity;
import fr.bischof.raphael.gothiite.model.RunInterval;
import fr.bischof.raphael.gothiite.model.RunTypeInterval;

/**
 * Service running during a session. This service maintains GPS coordinates retrieving, plays sound when its needed, etc...
 * Created by biche on 29/11/2015.
 */
public class RunningService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String ACTION_STAY_AWAKE = "ActionToForceServiceToStayAwake";
    public static final String ACTION_SHOW_UI_FROM_RUN = "ShowUIFromRun";
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

    public void loadRun(ArrayList<RunTypeInterval> runIntervals, Activity boundActivity,OnRunningServiceUpdateListener listener) {
        this.mRunIntervals = runIntervals;
        this.mRunIntervalsToDo = runIntervals;
        this.mBoundActivity = boundActivity;
        mBoundListener = listener;
        startRun();
    }

    private void startRun() {
        mCurrentStartTime = System.currentTimeMillis();
        mRunStartTime = mCurrentStartTime;
        startTimer();
        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void startTimer() {
        if (mRunIntervalsToDo!=null&&mRunIntervalsToDo.size()>0){
            if (mBoundListener !=null){
                RunTypeInterval interval = null;
                if (mRunIntervalsToDo.size()>1){
                    interval = mRunIntervalsToDo.get(1);
                }
                mBoundListener.onTimerStarted((long)mRunIntervalsToDo.get(0).getTimeToDo(),mRunStartTime,mRunIntervalsToDo.get(0).isEffort(),interval);
            }
            CountDownTimer cT =  new CountDownTimer((long) mRunIntervalsToDo.get(0).getTimeToDo() - 200, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    timerEnded();
                }
            };
            cT.start();
        }else{
            endRun();
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
        mRunIntervalsToDo.remove(0);
        mCurrentStartTime = System.currentTimeMillis();
        if (mRunIntervalsToDo.size()==0){
            endRun();
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
            PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 0,
                    intentStreamingUI,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            Notification notification;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Notification.Builder builder = new Notification.Builder(getApplicationContext())
                        .setContentText("" + (currentInterval.getTimeToDo() / 1000) + "")
                        .setContentTitle(getApplicationContext().getString(R.string.now_running))
                        .setContentIntent(pi);
                //TODO: Replace that icon
                builder.setSmallIcon(R.drawable.ic_cast_dark);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                notification = builder.build();
            }else{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setContentText("" + (currentInterval.getTimeToDo() / 1000) + "")
                        .setContentTitle(getApplicationContext().getString(R.string.now_running))
                        .setContentIntent(pi);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                //TODO: Replace that icon
                builder.setSmallIcon(R.drawable.ic_cast_dark);
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

    public void endRun(){
        if(mNotificationShown){
            stopLocationUpdates();
            hideNotification();
            stopSelf();
        }
    }

    public ArrayList<RunTypeInterval> getRunIntervalsToDo() {
        return mRunIntervalsToDo;
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
        mLastLocationOfInterval = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected() ) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
        mRequestingLocationUpdates = false;
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
}
