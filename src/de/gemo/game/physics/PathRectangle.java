package de.gemo.game.physics;

import static org.lwjgl.opengl.GL11.*;

public class PathRectangle {

	public static int SIZE = 5;
	private final Level level;
	private final int x, y;
	protected boolean blocked = false;
	private float distanceFromStart = 0;
	private float distanceFromGoal = 0;

	private PathRectangle[] neighbours;
	private PathRectangle prev;

	public PathRectangle(Level level, int x, int y) {
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public void renderColor(float r, float g, float b, float a) {
		glPushMatrix();
		{
			glLineWidth(1);
			glDisable(GL_LIGHTING);
			glEnable(GL_BLEND);
			glTranslatef(x * SIZE + x, y * SIZE + y, 0);
			glColor4f(r, g, b, a);
			glBegin(GL_LINE_LOOP);
			{
				glVertex2i(0, 0);
				glVertex2i(SIZE, 0);
				glVertex2i(SIZE, SIZE);
				glVertex2i(0, SIZE);
			}
			glEnd();
		}
		glPopMatrix();
	}

	public void render() {
		if (this.blocked) {
			renderColor(0.35f, 0.0f, 0.0f, 1f);
		} else {
			renderColor(0.15f, 0.15f, 0.15f, 1f);
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void initNeighbours() {
		neighbours = new PathRectangle[4];
		neighbours[0] = level.getRectangle(x - 1, 0);
		neighbours[1] = level.getRectangle(x + 1, 0);
		neighbours[2] = level.getRectangle(0, y - 1);
		neighbours[3] = level.getRectangle(0, y + 1);
	}

	public PathRectangle[] getNeighbours() {
		return neighbours;
	}

	/**
	 * Taken from Point2D
	 */
	@Override
	public int hashCode() {
		long bits = java.lang.Double.doubleToLongBits(getX());
		bits ^= java.lang.Double.doubleToLongBits(getY()) * 31;
		return (((int) bits) ^ ((int) (bits >> 32)));
	}

	public float getDistance(PathRectangle other) {
		// int dx = x - other.x;
		// int dy = y - other.y;
		//
		// // float result = (float) (Math.sqrt((dx * dx) + (dy * dy)));
		// // Optimization! Changed to distance^2 distance: (but looks more
		// "ugly")
		//
		// int result = (dx * dx) + (dy * dy);
		// return result;

		float h_diagonal = (float) Math.min(Math.abs(x - other.x), Math.abs(y - other.y));
		float h_straight = (float) (Math.abs(x - other.x) + Math.abs(y - other.y));
		float h_result = (float) (Math.sqrt(2) * h_diagonal + (h_straight - 2 * h_diagonal));
		float p = (1f / 10000f);
		h_result *= (1.0 + p);
		return h_result;
	}

	public PathRectangle getPrevious() {
		return prev;
	}

	public void setPrevious(PathRectangle prev) {
		this.prev = prev;
	}

	public void setDistanceFromGoal(float distanceFromGoal) {
		this.distanceFromGoal = distanceFromGoal;
	}

	public void setDistanceFromStart(float distanceFromStart) {
		this.distanceFromStart = distanceFromStart;
	}

	public float getDistanceFromGoal() {
		return distanceFromGoal;
	}

	public float getDistanceFromStart() {
		return distanceFromStart;
	}

}
