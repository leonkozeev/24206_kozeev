package ru.nsu.nbody.simulation.impl;

import ru.nsu.nbody.simulation.Body;
import ru.nsu.nbody.simulation.Message;
import ru.nsu.nbody.simulation.Simulator;

public class SetBody implements Message {
    private final int index;
    private final Body body;

    public SetBody(int index, Body body) {
        this.index = index;
        this.body = body;
    }

    @Override
    public void execute(Simulator self) {
        if (self instanceof RungeKutta rk) {
            rk.integrator.setBody(index, body);
        } else {
            throw new IllegalStateException("Simulator is not an instance of RungeKutta");
        }
    }
}