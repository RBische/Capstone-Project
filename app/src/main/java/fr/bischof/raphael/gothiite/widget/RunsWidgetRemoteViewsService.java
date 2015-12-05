package fr.bischof.raphael.gothiite.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

/**
 * Service called to return views from the Runs widget
 * Created by biche on 16/08/2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RunsWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new RunsWidgetRemoteViewFactory(this.getApplicationContext()));
    }
}
