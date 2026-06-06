package ru.nsu.nbody.view;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.font.BitmapText;
import com.jme3.input.CameraInput;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

import ru.nsu.nbody.view.body.BodyAppState;
import ru.nsu.nbody.view.lemurs.SettingDialog;


public class Nbody extends SimpleApplication {
    private static final String SETTINGS_NODE_NAME = "__settings__";

    private BitmapText hudText;
    private BodyAppState bodyControl;


    public Nbody() {
    }

    public Nbody(AppState... initialStates) {
        super(initialStates);
    }

    @Override
    public void simpleInitApp() {
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        flyCam.setMoveSpeed(500f);
        flyCam.setDragToRotate(false);
        flyCam.setZoomSpeed(10);

        cam.setFov(100);
        cam.setFrustumFar(100_000);

        GuiGlobals.getInstance().setCursorEventsEnabled(false, false);

        inputManager.deleteMapping(CameraInput.FLYCAM_RISE);
        inputManager.deleteMapping(CameraInput.FLYCAM_LOWER);
        inputManager.addMapping(CameraInput.FLYCAM_RISE, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(CameraInput.FLYCAM_LOWER, new KeyTrigger(KeyInput.KEY_LSHIFT));

        bodyControl = new BodyAppState();
        stateManager.attach(bodyControl);

        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());

        guiNode.attachChild(hudText);

        inputManager.addMapping("settingWindow", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener((ActionListener) (String name, boolean isPressed, float tpf) -> {
            if (name.equals("settingWindow") && isPressed) {
                toggleSettings();
            }
        }, "settingWindow");
    }

    private void updateHUD() {
        Vector3f pos = cam.getLocation();

        float[] angles = cam.getRotation().toAngles(null);
        float pitch = angles[0];
        float yaw   = angles[1];

        float fov = cam.getFov();
        float _speed = flyCam.getMoveSpeed();

        hudText.setLocalTranslation(10, settings.getHeight() - 10, 0);
        hudText.setText(String.format("""
            Pos: %.2f %.2f %.2f
            Yaw: %.2f Pitch: %.2f
            FOV: %.2f
            Speed: %.2f""",
            pos.x, pos.y, pos.z,
            yaw, pitch,
            fov,
            _speed
        ));
    }

    private void toggleSettings() {
        if (guiNode.getChild(SETTINGS_NODE_NAME) != null) {
            // Already open — close it
            com.jme3.scene.Spatial existing = guiNode.getChild(SETTINGS_NODE_NAME);
            if (existing != null) {
                existing.removeFromParent();
            }
            inputManager.setCursorVisible(false);
            flyCam.setEnabled(true);
            GuiGlobals.getInstance().setCursorEventsEnabled(false, false);
            return;
        }

        SettingDialog window = new SettingDialog(this, bodyControl);
        window.setName(SETTINGS_NODE_NAME);

        // Enable cursor events so lemur receives mouse clicks
        GuiGlobals.getInstance().setCursorEventsEnabled(true, true);
        inputManager.setCursorVisible(true);
        flyCam.setEnabled(false);

        // Centre on screen (deferred one frame via a control so size is known)
        window.addControl(new com.jme3.scene.control.AbstractControl() {
            private boolean positioned = false;
            @Override
            protected void controlUpdate(float tpf) {
                if (!positioned) {
                    Vector3f size = ((com.simsilica.lemur.Container) spatial).getPreferredSize();
                    spatial.setLocalTranslation(
                        cam.getWidth()  / 2f - size.x / 2f,
                        cam.getHeight() / 2f + size.y / 2f,
                        10
                    );
                    positioned = true;
                }
            }
            @Override protected void controlRender(com.jme3.renderer.RenderManager rm,
                                                   com.jme3.renderer.ViewPort vp) {}
        });

        guiNode.attachChild(window);
    }

    @Override
    public void simpleUpdate(float tpf) {
        updateHUD();
    }
}
