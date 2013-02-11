package de.gemo.engine.inputmanager;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import de.gemo.engine.core.Engine;
import de.gemo.engine.events.keyboard.KeyEvent;

public class KeyboardManager {

    private final Engine engine;
    public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
    private HashMap<Integer, Character> holdKeys = new HashMap<Integer, Character>();

    public KeyboardManager(Engine engine) {
        this.engine = engine;
        this.holdKeys = new HashMap<Integer, Character>();
        for (int index = 0; index < 65536; index++) {
            pressedKeys.put(index, false);
        }
    }

    public void update() {
        // iterate over currently pressed keys to handle hold keys
        for (Map.Entry<Integer, Character> entry : this.holdKeys.entrySet()) {
            if (Keyboard.isKeyDown(entry.getKey())) {
                // hold key
                engine.onKeyHold(new KeyEvent(entry.getKey(), entry.getValue(), true));
            }
        }

        while (Keyboard.next()) {
            boolean currentState = Keyboard.getEventKeyState();
            char character = Keyboard.getEventCharacter();
            int key = Keyboard.getEventKey();
            boolean oldState = holdKeys.containsKey(key);
            if (!currentState && oldState) {
                // released key
                engine.onKeyReleased(new KeyEvent(key, holdKeys.remove(key), false));
            } else if (currentState && !oldState) {
                // newly pressed key
                engine.onKeyPressed(new KeyEvent(key, character, true));
                holdKeys.put(key, character);
            }
            pressedKeys.put(key, currentState);
        }
    }

    public boolean isKeyDown(int key) {
        return pressedKeys.get(key);
    }
}
