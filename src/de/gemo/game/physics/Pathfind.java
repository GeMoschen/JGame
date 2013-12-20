package de.gemo.game.physics;

import java.awt.font.*;
import java.util.*;

import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.manager.*;

import static org.lwjgl.opengl.GL11.*;

public class Pathfind extends GameEngine {

	public static Level level;

	private PathRectangle start = null, goal = null;
	private ArrayList<PathRectangle> path = new ArrayList<PathRectangle>();
	private float ms = 0f;

	public Pathfind(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
		super(windowTitle, windowWidth, windowHeight, fullscreen);
	}

	@Override
	protected void createManager() {
		level = new Level(38 * 5, 29 * 5);
	}

	@Override
	public void onMouseUp(boolean handled, MouseReleaseEvent event) {
		int fX = event.getX() / (PathRectangle.SIZE + 1);
		int fY = event.getY() / (PathRectangle.SIZE + 1);
		if (event.isLeftButton()) {
			start = level.getRectangle(fX, fY);
			this.updatePath();
		} else if (event.isRightButton()) {
			goal = level.getRectangle(fX, fY);
			this.updatePath();
		}

	}

	private void updatePath() {
		if (goal != null && start != null && !goal.isBlocked() && !start.isBlocked() && goal != start) {
			// System.out.println(level.getPath(start, goal));
			long nano = System.nanoTime();
			this.path = level.getPath(start, goal);
			long time = System.nanoTime() - nano;
			this.ms = time / 1000000f;
		}
	}

	@Override
	protected void renderGame2D() {
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		{
			level.renderLevel();

			if (start != null) {
				start.renderColor(1, 0, 0, 1);
			}
			if (goal != null) {
				goal.renderColor(0, 1, 0, 1);
			}
			if (path != null) {
				for (PathRectangle node : this.path) {
					node.renderColor(0, 0, 1, 1);
				}
			}

		}
		glPopMatrix();

		glEnable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		FontManager.getStandardFont().drawString(10, 10, "TOOK: " + ms + "ms");
	}
}
