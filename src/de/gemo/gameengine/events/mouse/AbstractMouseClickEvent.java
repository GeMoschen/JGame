package de.gemo.gameengine.events.mouse;

public abstract class AbstractMouseClickEvent extends AbstractMouseEvent {

	private final MouseButton button;

	public AbstractMouseClickEvent(int x, int y, MouseButton button, int eventType) {
		super(x, y, eventType);
		this.button = button;
	}

	public MouseButton getButton() {
		return this.button;
	}

	public boolean isLeftButton() {
		return this.button.equals(MouseButton.LEFT);
	}

	public boolean isRightButton() {
		return this.button.equals(MouseButton.RIGHT);
	}

	public boolean isMiddleButton() {
		return this.button.equals(MouseButton.MIDDLE);
	}
}
