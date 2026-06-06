package ru.nsu.nbody.simulation;

import ru.nsu.nbody.util.SpeedClock;

public interface Simulator extends Runnable {
    SimSnapshot get();
    void addTask(Message message);
    SpeedClock clock();
}