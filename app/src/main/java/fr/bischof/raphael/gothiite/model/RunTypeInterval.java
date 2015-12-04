package fr.bischof.raphael.gothiite.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import fr.bischof.raphael.gothiite.calculator.Calculator;

/**
 * Model class useful during a run
 * Created by biche on 29/11/2015.
 */
public class RunTypeInterval implements Parcelable {
    private double timeToDo;
    private double distanceToDo;
    private boolean effort;

    public RunTypeInterval(double timeToDo, double distanceToDo, boolean effort) {
        this.timeToDo = timeToDo;
        this.distanceToDo = distanceToDo;
        this.effort = effort;
    }

    public boolean isEffort() {
        return effort;
    }

    public double getTimeToDo() {
        return timeToDo;
    }

    public double getDistanceToDo() {
        return distanceToDo;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    private RunTypeInterval(Parcel in) {
        this.timeToDo = in.readDouble();
        this.distanceToDo = in.readDouble();
        boolean[] table = new boolean[1];
        in.readBooleanArray(table);
        this.effort = table[0];
    }

    public static final Creator<RunTypeInterval> CREATOR = new Creator<RunTypeInterval>() {
        @Override
        public RunTypeInterval createFromParcel(Parcel in) {
            return new RunTypeInterval(in);
        }

        @Override
        public RunTypeInterval[] newArray(int size) {
            return new RunTypeInterval[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(timeToDo);
        dest.writeDouble(distanceToDo);
        dest.writeBooleanArray(new boolean[]{effort});
    }
}
