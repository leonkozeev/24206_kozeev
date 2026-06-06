package ru.nsu.nbody.simulation.impl;

import ru.nsu.nbody.simulation.Message;
import ru.nsu.nbody.simulation.Simulator;


public class SetPause implements Message {
    private final boolean pause;
    
    /** @param pause flag: {@code true} - stops the simulation, {@code false} - resumes it.*/
    public SetPause(boolean pause) {
        this.pause = pause;
    }

    @Override
    public void execute(Simulator self) {
        if (self instanceof RungeKutta rk) {
            rk.pause = pause;
            rk.clock.setPause(pause);
        } else {
            throw new IllegalStateException("Simulator is not an instance of RungeKutta");
        }
    }
}
