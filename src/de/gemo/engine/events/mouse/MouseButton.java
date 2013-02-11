package de.gemo.engine.events.mouse;

public enum MouseButton {

    LEFT(0),

    RIGHT(1),

    MIDDLE(2),

    EXTRA_LEFT(3),

    EXTRA_RIGHT(4),

    UNDEFINED(-1);

    private final int ID;

    private MouseButton(int ID) {
        this.ID = ID;
    }

    public static MouseButton byID(int ID) {
        for (MouseButton button : MouseButton.values()) {
            if (button.ID == ID) {
                return button;
            }
        }
        return UNDEFINED;
    }
}
