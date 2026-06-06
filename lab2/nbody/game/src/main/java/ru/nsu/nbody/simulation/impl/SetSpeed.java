package ru.nsu.nbody.simulation.impl;

import ru.nsu.nbody.simulation.Message;
import ru.nsu.nbody.simulation.Simulator;


public class SetSpeed implements Message {
    private final double speed;

    public SetSpeed(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("speed must be positive!");
        }
        this.speed = speed;
    }

    @Override
    public void execute(Simulator self) {
        if (self instanceof RungeKutta rk) {
            rk.speed = speed;
            rk.clock.setSpeed(speed);
        } else {
            throw new IllegalStateException("Simulator is not an instance of RungeKutta");
        }
    }
}
