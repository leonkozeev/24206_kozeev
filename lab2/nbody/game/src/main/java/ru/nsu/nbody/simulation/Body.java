package ru.nsu.nbody.simulation;

import ru.nsu.nbody.util.Vector3d;

public record Body(double m, Vector3d r, Vector3d v) {
    public Body move(Vector3d a, double dt) {
        return new Body(m, r.addmul(v, dt).addmul(a, dt * dt / 2), v.addmul(a, dt));
    }
}