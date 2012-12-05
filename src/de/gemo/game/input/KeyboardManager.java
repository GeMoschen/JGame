package de.gemo.game.input;

import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;

import de.gemo.game.core.Game;
import de.gemo.game.events.keyboard.KeyEvent;

public class KeyboardManager {

    private final Game game;
    public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
    private HashSet<Integer> holdKeys = new HashSet<Integer>();

    public KeyboardManager(Game game) {
        this.game = game;
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
                game.onKeyHold(new KeyEvent(currentKey, true));
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
                game.onKeyReleased(new KeyEvent(key, false));
                holdKeys.remove(key);
            } else if (currentState && !oldState) {
                // newly pressed key
                game.onKeyPressed(new KeyEvent(key, true));
                holdKeys.add(key);
            }
            pressedKeys.put(key, currentState);
        }
    }

    public boolean isKeyDown(int key) {
        return pressedKeys.get(key);
    }
}
