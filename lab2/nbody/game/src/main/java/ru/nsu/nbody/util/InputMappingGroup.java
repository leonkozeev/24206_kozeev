package ru.nsu.nbody.util;

import java.util.ArrayList;
import java.util.List;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.Trigger;

public class InputMappingGroup {

    private final InputManager inputManager;
    private final List<String> mappingNames = new ArrayList<>();
    private final List<ActionListener> actionListeners = new ArrayList<>();
    private final List<AnalogListener> analogListeners = new ArrayList<>();

    public InputMappingGroup(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    public void addMapping(String name, Trigger trigger) {
        inputManager.addMapping(name, trigger);
        mappingNames.add(name);
    }

    public void addListener(ActionListener listener, String... names) {
        inputManager.addListener(listener, names);
        actionListeners.add(listener);
    }

    public void addListener(AnalogListener listener, String... names) {
        inputManager.addListener(listener, names);
        analogListeners.add(listener);
    }

    public void cleanup() {
        for (ActionListener al : actionListeners) {
            inputManager.removeListener(al);
        }
        for (AnalogListener al : analogListeners) {
            inputManager.removeListener(al);
        }
        for (String name : mappingNames) {
            inputManager.deleteMapping(name);
        }
        actionListeners.clear();
        analogListeners.clear();
        mappingNames.clear();
    }
}