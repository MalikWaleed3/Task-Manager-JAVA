package Model;

public class StopWatch {

    private double startstopwatch;
    private double endstopwatch;

    public void starTimer(){
        this.startstopwatch = System.currentTimeMillis();
    }
    public void endTimer(){
        this.endstopwatch = System.currentTimeMillis();
    }
    public double getSeconds(){
        return (endstopwatch - startstopwatch) * 0.001;
    }
    public double getMinutes(){
        return (endstopwatch - startstopwatch) * 0.00001666667;
    }
    public double getHours(){
        return (endstopwatch - startstopwatch) * 0.000000277777833;
    }
}