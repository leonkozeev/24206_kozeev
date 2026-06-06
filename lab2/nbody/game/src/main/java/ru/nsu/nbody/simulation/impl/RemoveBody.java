package ru.nsu.nbody.simulation.impl;

import ru.nsu.nbody.simulation.Message;
import ru.nsu.nbody.simulation.Simulator;

public class RemoveBody implements Message {
    private final int index;

    public RemoveBody(int index) {
        this.index = index;
    }

    @Override
    public void execute(Simulator self) {
        if (self instanceof RungeKutta rk) {
            rk.integrator.removeBody(index);
        } else {
            throw new IllegalStateException("Simulator is not an instance of RungeKutta");
        }
    }
}
