package ru.nsu.nbody.view.lemurs;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.text.DocumentModel;

import ru.nsu.nbody.view.body.BodyControl;

// ── Helper: label + slider + text field in one row ───────────────────────────
class SettingRow extends Container {
    private final Slider slider;
    private final TextField textField;
    private final VersionedReference<Double> sliderRef;
    private final VersionedReference<DocumentModel> textRef;
    private boolean isUpdating = false;

    public SettingRow(String labelText, double min, double max, double initial) {
        setLayout(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even));
        setInsets(new Insets3f(2, 4, 2, 4));

        addChild(new Label(labelText));

        slider = addChild(new Slider());
        slider.getModel().setMinimum(min);
        slider.getModel().setMaximum(max);
        slider.getModel().setValue(initial);

        textField = addChild(new TextField(String.format("%.1f", initial)));
        textField.setPreferredWidth(70);

        sliderRef = slider.getModel().createReference();
        textRef   = textField.getDocumentModel().createReference();

        addControl(new SyncControl());
    }

    private class SyncControl extends AbstractControl {
        @Override
        protected void controlUpdate(float tpf) {
            if (isUpdating) return;
            if (sliderRef.update()) {
                isUpdating = true;
                textField.setText(String.format("%.1f", slider.getModel().getValue()));
                isUpdating = false;
            }
            if (textRef.update()) {
                try {
                    double val = Double.parseDouble(textField.getText().trim());
                    if (val >= slider.getModel().getMinimum() && val <= slider.getModel().getMaximum()) {
                        isUpdating = true;
                        slider.getModel().setValue(val);
                        isUpdating = false;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        @Override protected void controlRender(RenderManager rm, ViewPort vp) {}
    }

    public double getValue() { return slider.getModel().getValue(); }
}

// ── Main dialog ───────────────────────────────────────────────────────────────
public class SettingDialog extends Container {

    private final SimpleApplication app;
    private final BodyControl bodyControl;

    private final SettingRow rowSimSpeed;
    private final SettingRow rowCamSpeed;
    private final SettingRow rowFov;
    private final SettingRow rowFar;
    private final Button      btnPause;

    public SettingDialog(SimpleApplication app, BodyControl bodyControl) {
        this.app         = app;
        this.bodyControl = bodyControl;

        setLayout(new SpringGridLayout(Axis.Y, Axis.X));
        setInsets(new Insets3f(12, 12, 12, 12));

        addChild(new Label("Settings", new ElementId("title")));

        rowSimSpeed = addChild(new SettingRow("Simulation speed", 0.1,  200.0, bodyControl.isPause() ? 0 : 10));
        rowCamSpeed = addChild(new SettingRow("Camera speed",     10.0, 10000.0, app.getFlyByCamera().getMoveSpeed()));
        rowFov      = addChild(new SettingRow("Field of view",    20.0, 170.0,   app.getCamera().getFov()));
        rowFar      = addChild(new SettingRow("Render distance",  1000.0, 5_000_000.0, app.getCamera().getFrustumFar()));

        // Pause / resume button
        btnPause = addChild(new Button(bodyControl.isPause() ? "Resume simulation" : "Pause simulation"));
        btnPause.addClickCommands(src -> onTogglePause());

        Button btnClose = addChild(new Button("Close  [K]"));
        btnClose.addClickCommands(src -> closeDialog());

        // Apply settings every frame via a control
        addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) { applySettings(); }
            @Override protected void controlRender(RenderManager rm, ViewPort vp) {}
        });
    }

    private void onTogglePause() {
        bodyControl.pause();
        btnPause.setText(bodyControl.isPause() ? "Resume simulation" : "Pause simulation");
    }

    private void applySettings() {
        bodyControl.setSpeed(rowSimSpeed.getValue());
        app.getFlyByCamera().setMoveSpeed((float) rowCamSpeed.getValue());
        app.getCamera().setFov((float) rowFov.getValue());
        app.getCamera().setFrustumFar((float) rowFar.getValue());
    }

    private void closeDialog() {
        applySettings();
        removeFromParent();
        // Re-hide cursor, re-enable flyCam
        app.getInputManager().setCursorVisible(false);
        app.getFlyByCamera().setEnabled(true);
        // Re-disable lemur cursor events (we enabled them when opening)
        GuiGlobals.getInstance().setCursorEventsEnabled(false, false);
    }
}
