package ru.nsu.nbody.desktopmodule;

import com.jme3.system.AppSettings;

import ru.nsu.nbody.view.Nbody;

// import ru.nsu.nbody.view.Nbody;

/**
 * Used to launch a jme application in desktop environment
 *
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        final Nbody game = new Nbody();

        final AppSettings appSettings = new AppSettings(true);
        appSettings.setResizable(true);

        game.setSettings(appSettings);
        game.setShowSettings(true);
        game.start();
    }
}
