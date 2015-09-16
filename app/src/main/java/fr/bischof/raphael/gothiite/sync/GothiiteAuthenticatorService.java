package fr.bischof.raphael.gothiite.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by rbischof on 16/09/2015.
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class GothiiteAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private GothiiteAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new GothiiteAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}