package de.gemo.game.events.mouse;

public class MouseMoveEvent extends MouseEvent {

    private final int difX, difY;

    public MouseMoveEvent(int x, int y, int difX, int difY) {
        super(x, y);
        this.difX = difX;
        this.difY = difY;
    }

    public int getDifX() {
        return difX;
    }

    public int getDifY() {
        return difY;
    }
}
