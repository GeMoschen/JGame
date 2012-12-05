package de.gemo.game.events.keyboard;

public class KeyEvent {
    private final int key;
    private final boolean keyState;

    public KeyEvent(int key, boolean keyState) {
        this.key = key;
        this.keyState = keyState;
    }

    public int getKey() {
        return key;
    }

    public boolean isKeyDown() {
        return keyState;
    }

    public boolean isKeyUp() {
        return !this.isKeyDown();
    }
}
