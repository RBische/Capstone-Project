package fr.bischof.raphael.gothiite.model;

/**
 * Model class useful during a run
 * Created by biche on 29/11/2015.
 */
public class RunTypeInterval {
    private double timeToDo;
    private double distanceToDo;
    private boolean effort;

    public RunTypeInterval(double timeToDo, double distanceToDo, boolean effort) {
        this.timeToDo = timeToDo;
        this.distanceToDo = distanceToDo;
        this.effort = effort;
    }
}
