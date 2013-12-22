package de.gemo.game.sim.core;

import java.awt.*;
import java.util.*;

import de.gemo.game.sim.tiles.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.manager.*;

import static org.lwjgl.opengl.GL11.*;

public class Pathfind extends GameEngine {

	public Level level;

	private Point start = null, goal = null;
	private AbstractTile startTile = null, goalTile = null;
	private ArrayList<Point> path = new ArrayList<Point>();
	private float ms = 0f;

	public Pathfind(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
		super(windowTitle, windowWidth, windowHeight, fullscreen);
	}

	@Override
	protected void createManager() {
		TileManager.initialize();
		start = new Point();
		goal = new Point();
		level = new Level(100, 100);
	}

	@Override
	public void onMouseUp(boolean handled, MouseReleaseEvent event) {
		int fX = event.getX() / (AbstractTile.TILE_SIZE + 1);
		int fY = event.getY() / (AbstractTile.TILE_SIZE + 1);
		if (event.isLeftButton()) {
			start.x = fX;
			start.y = fY;
			this.updatePath();
		} else if (event.isRightButton()) {
			goal.x = fX;
			goal.y = fY;
			this.updatePath();
		}

	}

	private void updatePath() {
		startTile = this.level.getTile(this.start.x, this.start.y);
		goalTile = this.level.getTile(this.goal.x, this.goal.y);
		if (startTile != null && goalTile != null && !startTile.isBlockingPath() && !goalTile.isBlockingPath() && start != goal) {
			// System.out.println(level.getPath(start, goal));
			long nano = System.nanoTime();
			this.path = level.getPath(this.start, this.goal);
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

			if (path != null) {
				for (Point node : this.path) {
					renderSingle(node.x, node.y, 0, 0, 1);
				}
			}

			if (goalTile != null) {
				renderSingle(goal.x, goal.y, 0, 1, 0);
			}

			if (startTile != null) {
				renderSingle(start.x, start.y, 1, 1, 0);
			}
		}
		glPopMatrix();

		glEnable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		FontManager.getStandardFont().drawString(10, 10, "TOOK: " + ms + "ms");
	}

	private void renderSingle(int x, int y, float r, float g, float b) {
		glPushMatrix();
		{
			glTranslatef(x * AbstractTile.TILE_SIZE + x, y * AbstractTile.TILE_SIZE + y, 0);
			glLineWidth(1);
			glDisable(GL_LIGHTING);
			glEnable(GL_BLEND);
			glColor3f(r, g, b);
			glBegin(GL_LINE_LOOP);
			{
				glVertex2i(0, 0);
				glVertex2i(AbstractTile.TILE_SIZE, 0);
				glVertex2i(AbstractTile.TILE_SIZE, AbstractTile.TILE_SIZE);
				glVertex2i(0, AbstractTile.TILE_SIZE);
			}
			glEnd();
		}
		glPopMatrix();
	}
}
