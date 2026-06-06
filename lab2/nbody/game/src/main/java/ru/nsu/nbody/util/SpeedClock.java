package ru.nsu.nbody.util;

import java.time.Duration;

public class SpeedClock {
    private static final double timeInTick = 1e-9;
    private static long getTime() {
        return System.nanoTime();
    }

    private long realTimePoint;
    private double time = 0;
    private double speed = 1.0;
    private boolean pause = false;

    public SpeedClock() {
        realTimePoint = getTime();
    }

    public SpeedClock(double speed) {
        this();
        if (speed > 0) {
            this.speed = speed;
        }
    }

    public synchronized double time() {
        update();
        return time;
    }

    public synchronized void setSpeed(double speed) {
        if (speed <= 0) return;
        update();
        this.speed = speed;
    }

    public synchronized void setPause(boolean pause) {
        update();
        if (this.pause && !pause) {
            realTimePoint = getTime();
        }
        this.pause = pause;
    }


    public void sleep(double sleepTime) throws InterruptedException {
        if (sleepTime <= 0) return;
        long delta = (long)(sleepTime * 1e9 / speed);
        if (delta > 0) {
            Thread.sleep(Duration.ofNanos(delta));
        }
    }

    private void update() {
        if (pause) return;
        long realNow = getTime();
        long realDelta = realNow - realTimePoint;
        realTimePoint = realNow;
        double delta = ((double) realDelta) * timeInTick * speed;
        time += delta;
    }
    
}