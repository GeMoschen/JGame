package de.gemo.game.sim.tiles;

import java.awt.*;

public abstract class AbstractTile {

	public static final int TILE_SIZE = 10;

	private final String name;
	private final Color imgColor;
	protected boolean blockingPath = false;
	private final int ID;

	public AbstractTile(int ID, String name, Color imgColor, boolean blockingPath) {
		this.name = name;
		this.imgColor = imgColor;
		this.blockingPath = blockingPath;
		this.ID = ID;
	}

	public final String getName() {
		return name;
	}

	public final Color getIMGColor() {
		return imgColor;
	}

	public final boolean isBlockingPath() {
		return blockingPath;
	}

	public final int getID() {
		return ID;
	}

	@Override
	public final int hashCode() {
		return this.imgColor.hashCode();
	}

	public abstract void onChange(int x, int y, AbstractTile oldTile, AbstractTile newTile);

	public final void render() {
		this.render(0, 0);
	}

	public abstract void render(int x, int y);
}
