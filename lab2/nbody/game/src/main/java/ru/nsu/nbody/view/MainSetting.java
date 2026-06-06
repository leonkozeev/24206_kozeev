package ru.nsu.nbody.view;

import com.jme3.app.SimpleApplication;

import ru.nsu.nbody.view.body.BodyControl;

public record MainSetting(
    double simulationSeed,
    float cameraSpeed,
    double jumpSize,
    float cameraFov
) {
    public MainSetting getStandard() {
        return new MainSetting(
            1.0,
            100.0f,
            1e3,
            100.0f
        );
    }

    public void setSetting(SimpleApplication app, BodyControl bodyControl) {
        bodyControl.setSpeed(simulationSeed);
        app.getFlyByCamera().setMoveSpeed(cameraSpeed);
        app.getCamera().setFov(cameraFov);
        // jumpSize
    }
}