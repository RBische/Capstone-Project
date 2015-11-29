package fr.bischof.raphael.gothiite.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Service running during a session. This service maintains GPS coordinates retrieving, plays sound when its needed, etc...
 * Created by biche on 29/11/2015.
 */
public class RunningService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new RunningBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RunningBinder extends Binder {
        public RunningBinder getService() {
            // Return this instance of LocalService so clients can call public methods
            return RunningBinder.this;
        }
    }

}
