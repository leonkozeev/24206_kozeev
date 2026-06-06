package ru.nsu.nbody.simulation.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import ru.nsu.nbody.simulation.Message;
import ru.nsu.nbody.simulation.SimSnapshot;
import ru.nsu.nbody.simulation.Simulator;
import ru.nsu.nbody.util.SpeedClock;


public class RungeKutta implements Simulator {
    static final double TPS = 20;
    double snTime;
    final AtomicReference<SimSnapshot> snapshot = new AtomicReference<>();
    final LinkedBlockingQueue<Message> tasks = new LinkedBlockingQueue<>();
    // final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    final SpeedClock clock = new SpeedClock(1.0);
    final DormandPrinceIntegrator integrator = new DormandPrinceIntegrator();
    boolean pause = false;
    double speed = 1.0;
    double time = 0;
    
    private void step() {
        time += integrator.step();
    }

    private void sleepTickStep() {
        double simStart = clock.time();
        double inSimStart = time;
        while (time - inSimStart < speed / TPS) {
            step();
        }
        double simEnd = clock.time();
        snTime = simEnd;
        double inSimEnd = time;
        try {
            double simTime = simEnd - simStart;
            double inSimTime = inSimEnd - inSimStart;
            // System.out.println("Simulated " + inSimTime + " second in " + simTime + " second, sleeping for " + (inSimTime - simTime) + " second");
            clock.sleep(inSimTime - simTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if (!pause) {
                while (!tasks.isEmpty()) {
                    tasks.poll().execute(this);
                }
                sleepTickStep();
                // System.out.println("tick(" + speed + "): " + time + "(" + snTime + ")" + );
                snapshot.set(new SimSnapshot(integrator.getBodies(), snTime));
            } else {
                try {
                    Message message = tasks.take();
                    message.execute(this);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public SimSnapshot get() {
        return snapshot.get();
    }

    @Override
    public void addTask(Message message) {
        tasks.offer(message);
    }

    @Override
    public SpeedClock clock() {
        return clock;
    }
}