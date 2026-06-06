package ru.nsu.nbody.view.body;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

import ru.nsu.nbody.simulation.Body;
import ru.nsu.nbody.simulation.SimSnapshot;
import ru.nsu.nbody.simulation.Simulator;
import ru.nsu.nbody.simulation.impl.AddBody;
import ru.nsu.nbody.simulation.impl.RemoveBody;
import ru.nsu.nbody.simulation.impl.RungeKutta;
import ru.nsu.nbody.simulation.impl.SetBody;
import ru.nsu.nbody.simulation.impl.SetPause;
import ru.nsu.nbody.simulation.impl.SetSpeed;
import ru.nsu.nbody.util.InputMappingGroup;
import ru.nsu.nbody.util.SpeedClock;
import ru.nsu.nbody.util.Vector3d;
import ru.nsu.nbody.view.lemurs.SetBodyDialog;
import ru.nsu.nbody.view.listener.AddBodyListener;

public class BodyAppState extends BaseAppState implements BodyControl {
    private static final String ACT_ADD_BODY = "AddBody";
    private static final String ACT_PAUSE = "TogglePause";
    private static final String ACT_RMB = "RMBPick";
    private final List<VisualBody> bodies = new ArrayList<>();
    private final Simulator simulator;
    private final SpeedClock clock;
    private double timePoint = 0;
    private final Thread simThread;

    private double snTime = 0.0;
    private int pauseDepth = 0;

    private InputMappingGroup inputGroup;

    public BodyAppState() {
        this.simulator = new RungeKutta();
        this.clock = simulator.clock();
        simThread = new Thread(simulator);
    }

    @Override
    protected void initialize(Application app) {
        setSpeed(10);
        addBody(new Body(1e6, new Vector3d(0,0,0), new Vector3d(0,0,-10)));
        addBody(new Body(1e3, new Vector3d(100,0,0), new Vector3d(0,0,100)));
        addBody(new Body(1e5, new Vector3d(0,1000,0), new Vector3d(0,0,-33.1)));
        addBody(new Body(10, new Vector3d(0,1050,0), new Vector3d(44.7,0,-31.6)));


        simThread.start();
        inputGroup = new InputMappingGroup(app.getInputManager());
        inputGroup.addMapping(ACT_ADD_BODY, new KeyTrigger(KeyInput.KEY_B));
        inputGroup.addListener(new AddBodyListener(app, this), ACT_ADD_BODY);

        inputGroup.addMapping(ACT_PAUSE, new KeyTrigger(KeyInput.KEY_P));
        inputGroup.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (ACT_PAUSE.equals(name) && isPressed) {
                if (isPause()) resume();
                else pause();
            }
        }, ACT_PAUSE);

        inputGroup.addMapping(ACT_RMB, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputGroup.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (ACT_RMB.equals(name) && isPressed) handleRightClick(app);
        }, ACT_RMB);
    }

    @Override
    protected void cleanup(Application app) {
        inputGroup.cleanup();
        if (bodies != null) {
            for (VisualBody b: bodies) {
                b.cleanup(((SimpleApplication) app).getRootNode());
            }
        }
        simThread.interrupt();
    }

    @Override
    public void update(float tpf) {
        SimSnapshot snapshot = simulator.get();
        if (snapshot != null && snapshot.time() > snTime) {
            timePoint = snTime;
            Body[] modelBodies = snapshot.bodys();
            for (int i = 0; i < modelBodies.length; i++) {
                bodies.get(i).set(modelBodies[i], (float)(snapshot.time() - snTime));
            }
            snTime = snapshot.time();
        }
        if (!isPause()) {
            double now = clock.time();
            double delta = now - timePoint;
            if (delta > 1e-3) {
                timePoint = now;
                for (VisualBody body : bodies) {
                    body.update((float) delta);
                }
            }
        }
    }

    @Override protected void onEnable() {
        simulator.addTask(new SetPause(false));
    }
    @Override protected void onDisable() {
        simulator.addTask(new SetPause(true));
    }

    @Override
    public void addBody(Body body) {
        int id = bodies.size();
        SimpleApplication app = (SimpleApplication)getApplication();
        bodies.add(new VisualBody(id, body, app.getRootNode(), app.getAssetManager()));
        simulator.addTask(new AddBody(body));
    }
    @Override
    public void setBody(int id, Body body) {
        SimpleApplication app = (SimpleApplication)getApplication();
        if (0 <= id && id < bodies.size()) {
            bodies.set(id, new VisualBody(id, body, app.getRootNode(), app.getAssetManager()));
            simulator.addTask(new SetBody(id, body));
        }
    }

    @Override
    public void removeBody(int id) {
        if (id < 0 || id >= bodies.size()) return;
        SimpleApplication app = (SimpleApplication) getApplication();
        int last = bodies.size() - 1;

        bodies.get(id).cleanup(app.getRootNode());
        if (id != last) {
            bodies.set(id, bodies.get(last));
        }
        bodies.remove(last);

        simulator.addTask(new RemoveBody(id));
    }

    @Override
    public void pause() {
        pauseDepth++;
        if (pauseDepth == 1) {
            simulator.addTask(new SetPause(true));
        }
    }
    @Override
    public void resume() {
        pauseDepth--;
        if (pauseDepth <= 0) {
            simulator.addTask(new SetPause(false));
            pauseDepth = 0;
        }
    }

    @Override
    public boolean isPause() {
        return pauseDepth > 0;
    }

    @Override
    public void setSpeed(double speed) {
        simulator.addTask(new SetSpeed(speed));
    }


    private void handleRightClick(Application app) {
        SimpleApplication sapp = (SimpleApplication) app;
        Ray ray = new Ray(
            sapp.getCamera().getLocation(),
            sapp.getCamera().getDirection()
        );

        int hit = -1;
        float bestDist = Float.MAX_VALUE;

        for (int i = 0; i < bodies.size(); i++) {
            VisualBody vb = bodies.get(i);
            Vector3f center = vb.pos();
            float r = vb.radius();

            Vector3f oc = ray.getOrigin().subtract(center);
            float b = oc.dot(ray.getDirection());
            float c = oc.dot(oc) - r * r;
            float disc = b * b - c;
            if (disc >= 0) {
                float dist = -b - (float) Math.sqrt(disc);
                if (dist > 0 && dist < bestDist) {
                    bestDist = dist;
                    hit = i;
                }
            }
        }

        if (hit >= 0) {
            SetBodyDialog dialog = new SetBodyDialog(this, hit, bodies.get(hit).body());
            dialog.show(app);
        }
    }
}