package fr.bischof.raphael.gothiite.calculator;

public final class Calculator {

    private static double calculateDistanceNeeded(double time, double vVO2Max, double ie) {
        return ((ie*Math.log(time/7)+100)*vVO2Max*time)/6;
    }

    private static double calculateVV02max(double time, double distance, double ie) {
        return 6*distance/((ie*Math.log(time/7)+100)*time);
    }

    private static double calculateTimeNeeded(double distance, double vVO2Max, double ie) {
        double time = 1;
        int i =0;
        double result;
        boolean infOk = false;
        boolean supOk = false;
        double step = 1;
        while (i<100000){
            result = calculateEquationResult(ie, time, vVO2Max, distance);
            if (infOk&&supOk){
                step = step/2;
                infOk = false;
                supOk = false;
            }
            if (result<0){
                infOk = true;
                time+=step;
            }else{
                supOk = true;
                time-=step;
            }
            i+=1;
        }
        return time;
    }

    private static double calculateEquationResult(double ie, double time, double vVO2Max, double distance) {
        double lnT = Math.log(time/7);
        double top = (ie*lnT+100)*vVO2Max*time;
        return top/6-distance;
    }
}
