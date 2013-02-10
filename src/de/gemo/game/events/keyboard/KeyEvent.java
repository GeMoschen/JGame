package de.gemo.game.events.keyboard;

public class KeyEvent {
    private final int key;
    private final char character;
    private final boolean keyState;

    public KeyEvent(int key, char character, boolean keyState) {
        this.key = key;
        this.character = character;
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

    public char getCharacter() {
        return character;
    }
}
