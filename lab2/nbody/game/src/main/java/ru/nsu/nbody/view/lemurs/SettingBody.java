package ru.nsu.nbody.view.lemurs;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.PopupState;

import ru.nsu.nbody.simulation.Body;
import ru.nsu.nbody.util.Vector3d;

class VectorField extends Container {
    public final TextField x;
    public final TextField y;
    public final TextField z;

    public VectorField(Vector3d vec) {
        super(new SpringGridLayout(Axis.X, Axis.Y));
        x = addChild(new TextField(String.valueOf(vec.x())));
        y = addChild(new TextField(String.valueOf(vec.y())));
        z = addChild(new TextField(String.valueOf(vec.z())));
        x.setInsets(new Insets3f(0, 0, 0, 5));
        y.setInsets(new Insets3f(0, 0, 0, 5));
    }

    public Vector3d getValue() {
        try {
            return new Vector3d(
                Double.parseDouble(x.getText()),
                Double.parseDouble(y.getText()),
                Double.parseDouble(z.getText())
            );
        } catch (NumberFormatException e) {
            return new Vector3d(0, 0, 0);
        }
    }
}

public abstract class SettingBody {
    protected Container window;
    protected Body body;
    protected TextField massInput;
    protected VectorField posField;
    protected VectorField velField;
    protected PopupState popupState;
    protected Container dialogContainer;
    protected SimpleApplication app = null;

    abstract protected void onOk();

    protected void close() {
        if (app != null) {
            app.getFlyByCamera().setEnabled(true);
            app.getInputManager().setCursorVisible(false);
        }
        popupState.closePopup(dialogContainer);
        app = null;
    }

    public void show(Application app) {
        try {
            if (app instanceof SimpleApplication sapp) {
                this.app = sapp;
            }
            popupState = app.getStateManager().getState(PopupState.class);
            if (popupState == null) {
                popupState = new PopupState();
                app.getStateManager().attach(popupState);
            }

            dialogContainer = buildUI();
            dialogContainer.addControl(new AbstractControl() {
                @Override
                protected void controlUpdate(float tpf) {
                    float screenW = app.getContext().getSettings().getWidth();
                    float screenH = app.getContext().getSettings().getHeight();
                    Vector3f size = ((Container) spatial).getPreferredSize();
                    spatial.setLocalTranslation(
                        screenW / 2f - size.x / 2f,
                        screenH / 2f + size.y / 2f,
                        100
                    );
                }
                @Override protected void controlRender(RenderManager rm, ViewPort vp) {}
            });
            popupState.showPopup(dialogContainer);
            if (this.app != null) {
                this.app.getFlyByCamera().setEnabled(false);
                this.app.getInputManager().setCursorVisible(true);
            }
        } catch (Exception e) {
            System.err.println("ERROR in show: " + e.getMessage());
        }
    }

    /** Subclasses may append extra rows to {@code window} after calling super. */
    protected Container buildUI() {
        window = new Container(new SpringGridLayout(Axis.Y, Axis.X));
        window.setInsets(new Insets3f(10, 10, 10, 10));
        window.addChild(new Label("Object settings"));

        Container massGrid = window.addChild(new Container(new SpringGridLayout()));
        massGrid.addChild(new Label("Mass"));
        massInput = massGrid.addChild(new TextField(String.valueOf(body.m())));

        Container posGrid = window.addChild(new Container(new SpringGridLayout()));
        posGrid.addChild(new Label("Position (x y z)"));
        posField = new VectorField(body.r());
        posGrid.addChild(posField);

        Container velGrid = window.addChild(new Container(new SpringGridLayout()));
        velGrid.addChild(new Label("Velocity (x y z)"));
        velField = new VectorField(body.v());
        velGrid.addChild(velField);

        Container buttonRow = new Container(new SpringGridLayout(Axis.X, Axis.Y));
        Button okBtn = buttonRow.addChild(new Button("OK"));
        okBtn.addClickCommands(src -> onOk());
        Button cancelBtn = buttonRow.addChild(new Button("Cancel"));
        cancelBtn.addClickCommands(src -> close());
        window.addChild(buttonRow);

        return window;
    }
}
