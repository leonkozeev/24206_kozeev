package ru.nsu.nbody.view.lemurs;

import com.jme3.app.Application;
import com.simsilica.lemur.Button;

import ru.nsu.nbody.simulation.Body;
import ru.nsu.nbody.view.body.BodyControl;

public class SetBodyDialog extends SettingBody {
    private final BodyControl control;
    private final int id;

    public SetBodyDialog(BodyControl control, int id, Body body) {
        this.control = control;
        this.id = id;
        this.body = body;
    }

    @Override
    public void show(Application app) {
        control.pause();
        super.show(app);
        Button deleteBtn = window.addChild(new Button("Delete body"));
        deleteBtn.addClickCommands(src -> onDelete());
    }

    @Override
    protected void close() {
        control.resume();
        super.close();
    }

    @Override
    protected void onOk() {
        double mass = 0;
        try { mass = Double.parseDouble(massInput.getText()); }
        catch (NumberFormatException ignored) {}
        control.setBody(id, new Body(mass, posField.getValue(), velField.getValue()));
        close();
    }

    private void onDelete() {
        control.removeBody(id);
        close();
    }
}
