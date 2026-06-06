package ru.nsu.nbody.view.lemurs;

import com.jme3.app.Application;

import ru.nsu.nbody.simulation.Body;
import ru.nsu.nbody.util.Vector3d;
import ru.nsu.nbody.view.body.BodyControl;

public class AddBodyDialog extends SettingBody {
    private final BodyControl control;

    public AddBodyDialog(BodyControl control, Body body) {
        this.control = control;
        this.body = body;
    }

    @Override
    public void show(Application app) {
        super.show(app);
        control.pause();
    }

    @Override
    protected void close() {
        control.resume();
        super.close();
    }

    @Override
    protected void onOk() {
        double mass = 0;
        try {
            mass = Double.parseDouble(massInput.getText());
        } catch (NumberFormatException e) {}
        Vector3d pos = posField.getValue();
        Vector3d vel = velField.getValue();
        Body newBody = new Body(mass, pos, vel);
        control.addBody(newBody);
        close();
    }
}