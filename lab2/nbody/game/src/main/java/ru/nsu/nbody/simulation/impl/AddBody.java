package ru.nsu.nbody.simulation.impl;

import ru.nsu.nbody.simulation.Body;
import ru.nsu.nbody.simulation.Message;
import ru.nsu.nbody.simulation.Simulator;

public class AddBody implements Message {
    private final Body body;

    public AddBody(Body body) {
        System.out.println("Creating AddBody message with body: " + body);
        this.body = body;
    }

    @Override
    public void execute(Simulator self) {
        if (self instanceof RungeKutta rk) {
            System.out.println("Adding body: " + body);
            rk.integrator.addBody(body);
        } else {
            throw new IllegalStateException("Simulator is not an instance of RungeKutta");
        }
    }
}