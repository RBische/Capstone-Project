package fr.bischof.raphael.gothiite.model;

import android.location.Location;

/**
 * Model for a Run Interval stored in DB
 * Created by rbischof on 30/11/2015.
 */
public class RunInterval {
    private double distanceDone;
    private long startTime;
    private long endTime;
    private Location startPosition;
    private Location endPosition;

    public RunInterval(double distanceDone, long startTime, long endTime, Location startPosition, Location endPosition) {
        this.distanceDone = distanceDone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public double getDistanceDone() {
        return distanceDone;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Location getStartPosition() {
        return startPosition;
    }

    public Location getEndPosition() {
        return endPosition;
    }
}
