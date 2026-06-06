package ru.nsu.nbody.view.listener;
    
import com.jme3.app.Application;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;

import ru.nsu.nbody.simulation.Body;
import ru.nsu.nbody.util.Vector3d;
import ru.nsu.nbody.view.body.BodyControl;
import ru.nsu.nbody.view.lemurs.AddBodyDialog;

public class AddBodyListener implements ActionListener {
    private final Application app;
    private final BodyControl control;


    public AddBodyListener(Application app, BodyControl control) {
        this.app = app;
        this.control = control;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("AddBody") && isPressed) {
            Vector3f camPos = app.getCamera().getLocation();
            Vector3f camDir = app.getCamera().getDirection();
            Vector3d pos = new Vector3d(Math.round(camPos.x), Math.round(camPos.y), Math.round(camPos.z));
            Vector3d vel = new Vector3d(Math.round(camDir.x * 10), Math.round(camDir.y * 10), Math.round(camDir.z * 10));

            AddBodyDialog dialog = new AddBodyDialog(control, new Body(1000, pos, vel));
            dialog.show(app);
        }
    }
}