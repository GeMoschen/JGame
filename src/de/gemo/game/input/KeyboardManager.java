package de.gemo.game.input;

import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;

import de.gemo.game.core.Engine;
import de.gemo.game.events.keyboard.KeyEvent;

public class KeyboardManager {

    private final Engine engine;
    public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
    private HashSet<Integer> holdKeys = new HashSet<Integer>();

    public KeyboardManager(Engine engine) {
        this.engine = engine;
        this.holdKeys = new HashSet<Integer>();
        for (int index = 0; index < 65536; index++) {
            pressedKeys.put(index, false);
        }
    }

    public void update() {
        // iterate over currently pressed keys to handle hold keys
        for (int currentKey : this.holdKeys) {
            if (Keyboard.isKeyDown(currentKey)) {
                // hold key
                engine.onKeyHold(new KeyEvent(currentKey, true));
            }
        }

        boolean currentState = false;
        int key = 0;
        boolean oldState;
        while (Keyboard.next()) {
            currentState = Keyboard.getEventKeyState();
            key = Keyboard.getEventKey();
            oldState = holdKeys.contains(key);
            if (!currentState && oldState) {
                // released key
                engine.onKeyReleased(new KeyEvent(key, false));
                holdKeys.remove(key);
            } else if (currentState && !oldState) {
                // newly pressed key
                engine.onKeyPressed(new KeyEvent(key, true));
                holdKeys.add(key);
            }
            pressedKeys.put(key, currentState);
        }
    }

    public boolean isKeyDown(int key) {
        return pressedKeys.get(key);
    }
}
