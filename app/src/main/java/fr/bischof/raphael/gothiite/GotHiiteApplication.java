package fr.bischof.raphael.gothiite;

import android.app.Application;

import com.parse.Parse;

/**
 * GotHiite application automatically create the Parse singleton to reuse it in app
 */
public class GotHiiteApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize parse
        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
    }
}
