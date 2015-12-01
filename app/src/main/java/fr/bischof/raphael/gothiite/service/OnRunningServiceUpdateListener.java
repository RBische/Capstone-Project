package fr.bischof.raphael.gothiite.service;

import fr.bischof.raphael.gothiite.model.RunTypeInterval;

/**
 * Listen events from the running service
 * Created by biche on 30/11/2015.
 */
public interface OnRunningServiceUpdateListener {
    void onRunFinished();
    void onTimerEnded();
    void onTimerStarted(long duration, long timeStartRun, boolean effort, RunTypeInterval nextRunTypeInterval);
}
