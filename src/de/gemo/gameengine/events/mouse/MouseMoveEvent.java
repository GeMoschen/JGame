package de.gemo.gameengine.events.mouse;

public class MouseMoveEvent extends AbstractMouseEvent {

	private final float difX, difY;

	public MouseMoveEvent(int x, int y, float difX, float difY) {
		super(x, y, 0);
		this.difX = difX;
		this.difY = difY;
	}

	public float getDifX() {
		return difX;
	}

	public float getDifY() {
		return difY;
	}
}
