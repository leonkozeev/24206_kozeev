package ru.nsu.nbody.simulation.impl;

import java.util.ArrayList;
import java.util.List;

import ru.nsu.nbody.simulation.Body;
import ru.nsu.nbody.util.Vector3dM;


class SimulationState {
    public final List<Vector3dM> r;
    public final List<Vector3dM> v;
    public final List<Double> m;

    public SimulationState(int n) {
        this.r = new ArrayList<>(n);
        this.v = new ArrayList<>(n);
        this.m = new ArrayList<>(n);
    }

    public static SimulationState copyOf(SimulationState other) {
        int n = other.r.size();
        SimulationState copy = new SimulationState(n);
        for (int i = 0; i < n; i++) {
            copy.r.add(other.r.get(i).copy());
            copy.v.add(other.v.get(i).copy());
            copy.m.add(other.m.get(i));
        }
        return copy;
    }
}

public class DormandPrinceIntegrator {
    private static final double G = 1.0;

    private static final double[] C = {0, 0.2, 0.3, 0.8, 8.0/9.0, 1.0, 1.0};
    private static final double[][] A = {
        {},
        {0.2},
        {3.0/40.0, 9.0/40.0},
        {44.0/45.0, -56.0/15.0, 32.0/9.0},
        {19372.0/6561.0, -25360.0/2187.0, 64448.0/6561.0, -212.0/729.0},
        {9017.0/3168.0, -355.0/33.0, 46732.0/5247.0, 49.0/176.0, -5103.0/18656.0},
        {35.0/384.0, 0.0, 500.0/1113.0, 125.0/192.0, -2187.0/6784.0, 11.0/84.0}
    };
    private static final double[] B5 = {35.0/384.0, 0.0, 500.0/1113.0, 125.0/192.0, -2187.0/6784.0, 11.0/84.0, 0.0};
    private static final double[] B4 = {5179.0/57600.0, 0.0, 7571.0/16695.0, 393.0/640.0, -92097.0/339200.0, 187.0/2100.0, 1.0/40.0};

    private final SimulationState state = new SimulationState(10);
    private SimulationState k_buffer = null;
    private double dt = 1e-3;
    private double maxDt = 1.0;
    private final double tolerance = 1e-6;


    public void addBody(Body body) {
        state.r.add(Vector3dM.fromVector3d(body.r()));
        state.v.add(Vector3dM.fromVector3d(body.v()));
        state.m.add(body.m());
        k_buffer = null;
    }

    public void setBody(int id, Body body) {
        state.r.set(id, Vector3dM.fromVector3d(body.r()));
        state.v.set(id, Vector3dM.fromVector3d(body.v()));
        state.m.set(id, body.m());
        k_buffer = null;
    }

        public void removeBody(int id) {
        int last = state.r.size() - 1;
        if (id < 0 || id > last) return;
        if (id != last) {
            state.r.set(id, state.r.get(last));
            state.v.set(id, state.v.get(last));
            state.m.set(id, state.m.get(last));
        }
        state.r.remove(last);
        state.v.remove(last);
        state.m.remove(last);
        k_buffer = null;
    }

    public Body[] getBodies() {
        Body[] bodies = new Body[state.r.size()];
        for (int i = 0; i < state.r.size(); i++) {
            bodies[i] = new Body(state.m.get(i), state.r.get(i).toVector3d(), state.v.get(i).toVector3d());
        }
        return bodies;
    }

    public double step() {
        int n = state.r.size();
        if (n == 0) return maxDt;

        boolean stepAccepted = false;
        double simTime = 0;

        while (!stepAccepted) {
            SimulationState[] k = new SimulationState[7];
            
            k[0] = (k_buffer != null) ? k_buffer : calculateDerivative(state);

            for (int i = 1; i < 7; i++) {
                SimulationState intermediate = SimulationState.copyOf(state);
                for (int node = 0; node < n; node++) {
                    for (int j = 0; j < i; j++) {
                        intermediate.r.get(node).raddmul(k[j].r.get(node), dt * A[i][j]);
                        intermediate.v.get(node).raddmul(k[j].v.get(node), dt * A[i][j]);
                    }
                }
                k[i] = calculateDerivative(intermediate);
            }

            double maxError = 0;
            for (int i = 0; i < n; i++) {
                for (int d = 0; d < 3; d++) {
                    double errR = 0;
                    double errV = 0;
                    for (int s = 0; s < 7; s++) {
                        double diffB = (B5[s] - B4[s]) * dt;
                        errR += diffB * k[s].r.get(i).getComponent(d);
                        errV += diffB * k[s].v.get(i).getComponent(d);
                    }
                    maxError = Math.max(maxError, Math.max(Math.abs(errR), Math.abs(errV)));
                }
            }

            if (maxError < tolerance) {
                for (int i = 0; i < n; i++) {
                    for (int s = 0; s < 7; s++) {
                        state.r.get(i).raddmul(k[s].r.get(i), dt * B5[s]);
                        state.v.get(i).raddmul(k[s].v.get(i), dt * B5[s]);
                    }
                }
                k_buffer = k[6];
                stepAccepted = true;
                simTime = dt;
            }

            double scale = 0.9 * Math.pow(tolerance / (maxError + 1e-16), 0.2);
            dt = Math.min(maxDt, dt * Math.max(0.1, Math.min(2.0, scale)));
        }

        return simTime;
    }

    private SimulationState calculateDerivative(SimulationState s) {
        int n = s.r.size();
        SimulationState ds = new SimulationState(n);
        
        for (int i = 0; i < n; i++) {
            ds.m.add(s.m.get(i));
            ds.r.add(s.v.get(i).copy());
            ds.v.add(new Vector3dM(0, 0, 0));
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Vector3dM dr = s.r.get(j).copy().rsub(s.r.get(i));
                double distSq = dr.dot(dr) + 1e-18; 
                double invDist3 = G / (distSq * Math.sqrt(distSq));
                ds.v.get(i).raddmul(dr, s.m.get(j) * invDist3);
                ds.v.get(j).raddmul(dr, -s.m.get(i) * invDist3);
                
            }
        }
        return ds;
    }
}