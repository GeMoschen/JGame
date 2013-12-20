package de.gemo.game.physics;

public class EmptyRectangle extends PathRectangle {

	public EmptyRectangle(Level level) {
		super(level, -1, -1);
		this.blocked = true;
	}

	@Override
	public void render() {
	}

}
