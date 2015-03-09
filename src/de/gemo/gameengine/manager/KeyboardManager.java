package de.gemo.gameengine.manager;

import java.util.*;

import org.lwjgl.input.*;

import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.keyboard.*;

public class KeyboardManager {
    public static KeyboardManager $ = null;

    private final GameEngine engine;
    public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
    private HashMap<Integer, Character> holdKeys = new HashMap<Integer, Character>();

    public static KeyboardManager getInstance(GameEngine engine) {
        if ($ == null) {
            return new KeyboardManager(engine);
        } else {
            throw new RuntimeException("ERROR: KeyManager is already created!");
        }
    }

    private KeyboardManager(GameEngine engine) {
        $ = this;
        this.engine = engine;
        this.holdKeys = new HashMap<Integer, Character>();
        for (int index = 0; index < 65536; index++) {
            pressedKeys.put(index, false);
        }
        Keyboard.enableRepeatEvents(false);
    }

    public void update() {
        // iterate over currently pressed keys to handle hold keys
        for (Map.Entry<Integer, Character> entry : this.holdKeys.entrySet()) {
            if (Keyboard.isKeyDown(entry.getKey())) {
                // hold key
                engine.handleKeyHold(new KeyEvent(entry.getKey(), entry.getValue(), true));
            }
        }

        while (Keyboard.next()) {
            boolean currentState = Keyboard.getEventKeyState();
            char character = Keyboard.getEventCharacter();
            int key = Keyboard.getEventKey();
            boolean oldState = holdKeys.containsKey(key);

            if (!currentState && oldState) {
                // released key
                engine.handleKeyReleased(new KeyEvent(key, holdKeys.remove(key), false));
            }
            if (currentState && !oldState) {
                // newly pressed key
                engine.handleKeyPressed(new KeyEvent(key, character, true));
                holdKeys.put(key, character);
            }
            pressedKeys.put(key, currentState);
        }
    }

    public boolean isKeyDown(int key) {
        return pressedKeys.get(key);
    }
}
