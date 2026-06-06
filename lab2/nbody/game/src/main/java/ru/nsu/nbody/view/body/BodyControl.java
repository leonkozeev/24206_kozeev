package ru.nsu.nbody.view.body;

import ru.nsu.nbody.simulation.Body;

public interface BodyControl {
    public void addBody(Body body); 
    public void setBody(int id, Body body); 
    public void removeBody(int id);
    public void pause();
    public void resume();
    public boolean isPause();
    public void setSpeed(double speed);
}