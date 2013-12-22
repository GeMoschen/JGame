package de.gemo.gameengine.collision;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;

import de.gemo.gameengine.units.Vector;

import static org.lwjgl.opengl.GL11.*;

public class Hitbox {

	public static Hitbox createRectangle(float halfWidth, float halfHeight) {
		Hitbox hitbox = new Hitbox(0, 0);
		hitbox.addPoint(-halfWidth, -halfHeight);
		hitbox.addPoint(+halfWidth, -halfHeight);
		hitbox.addPoint(+halfWidth, +halfHeight);
		hitbox.addPoint(-halfWidth, +halfHeight);
		return hitbox;
	}

	public static Hitbox createRectangle(Vector center, float halfWidth, float halfHeight) {
		return createRectangle(center.getX(), center.getY(), halfWidth, halfHeight);
	}

	public static Hitbox createRectangle(float x, float y, float halfWidth, float halfHeight) {
		Hitbox hitbox = new Hitbox(x, y);
		hitbox.addPoint(-halfWidth, -halfHeight);
		hitbox.addPoint(+halfWidth, -halfHeight);
		hitbox.addPoint(+halfWidth, +halfHeight);
		hitbox.addPoint(-halfWidth, +halfHeight);
		return hitbox;
	}

	private List<Vector> points = new ArrayList<Vector>();
	private Vector center;
	private float angle = 0f;

	public Hitbox(Vector center) {
		this.center = center;
	}

	public Hitbox(float x, float y) {
		this(new Vector(x, y));
	}

	public final List<Vector> getPoints() {
		return points;
	}

	public final int getPointCount() {
		return this.points.size();
	}

	public final Vector getPoint(int index) {
		return this.points.get(index);
	}

	public final void addPoint(Vector vector) {
		this.addPoint(vector.getX(), vector.getY());
	}

	public final void addPoint(float x, float y) {
		this.points.add(new Vector(this.getCenter().getX() + x, this.getCenter().getY() + y));
	}

	public final Vector getCenter() {
		return this.center;
	}

	public final void setCenter(float x, float y) {
		float difX = x - this.center.getX();
		float difY = y - this.center.getY();
		this.move(difX, difY);
	}

	public final void setCenter(Vector vector) {
		this.setCenter(vector.getX(), vector.getY());
	}

	public final void move(float x, float y) {
		this.center.move(x, y);
		for (Vector vector : this.points) {
			vector.move(x, y);
		}
	}

	public final void rotate(float angle) {
		this.angle += angle;

		if (this.angle < 0f) {
			this.angle += 360f;
		}
		if (this.angle > 360f) {
			this.angle -= 360f;
		}

		float rad = (float) Math.toRadians(angle);
		float sin = (float) Math.sin(rad);
		float cos = (float) Math.cos(rad);
		for (Vector vector : this.points) {
			vector.rotateAround(this.getCenter(), sin, cos);
		}
	}

	public final void rotateAround(Vector center, float angle) {
		this.angle += angle;

		if (this.angle < 0f) {
			this.angle += 360f;
		}
		if (this.angle > 360f) {
			this.angle -= 360f;
		}

		float rad = (float) Math.toRadians(angle);
		float sin = (float) Math.sin(rad);
		float cos = (float) Math.cos(rad);
		this.center.rotateAround(center, sin, cos);
		for (Vector vector : this.points) {
			vector.rotateAround(center, sin, cos);
		}
	}

	public void setAngle(Vector center, float angle) {
		this.rotateAround(center, -this.angle + angle);
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.rotate(-this.angle + angle);
	}

	private final void renderCenter() {
		// render center
		Color.red.bind();
		glBegin(GL_LINE_LOOP);
		glVertex3i(-2, -2, 0);
		glVertex3i(2, -2, 0);
		glVertex3i(+2, +2, 0);
		glVertex3i(-2, +2, 0);
		glEnd();
	}

	public void render() {
		// translate to center
		glPushMatrix();
		{
			glDisable(GL_LIGHTING);
			glDisable(GL_BLEND);
			glDisable(GL_TEXTURE_2D);
			glLineWidth(1f);

			// render center
			glPushMatrix();
			{
				glTranslatef(this.center.getX(), this.center.getY(), 0);
				this.renderCenter();
			}
			glPopMatrix();

			// render boundingbox
			glPushMatrix();
			{
				Color.green.bind();
				glBegin(GL_LINE_LOOP);
				for (Vector vector : this.points) {
					vector.render();
				}
				glEnd();
			}
			glPopMatrix();

			glEnable(GL_BLEND);
			glEnable(GL_TEXTURE_2D);
		}
		glPopMatrix();
	}

	public void scale(float scaleX, float scaleY) {
		float currentAngle = this.angle;
		this.setAngle(0);
		for (Vector vector : this.points) {
			float currentX = vector.getX() - this.center.getX();
			float currentY = vector.getY() - this.center.getY();
			currentX *= scaleX;
			currentY *= scaleY;
			vector.setX(this.center.getX() + currentX);
			vector.setY(this.center.getY() + currentY);
		}
		this.setAngle(currentAngle);
	}

	public void scaleX(float scaleX) {
		this.scale(scaleX, 1f);
	}

	public void scaleY(float scaleY) {
		this.scale(1f, scaleY);
	}

	public Hitbox clone() {
		Hitbox otherBox = new Hitbox(this.center.clone());
		for (Vector vector : this.points) {
			otherBox.addPoint(vector.clone());
		}
		return otherBox;
	}

	@Override
	public String toString() {
		String result = this.getClass().getSimpleName() + " { ";
		for (int i = 0; i < points.size(); i++) {
			result += points.get(i).toString();
			if (i < points.size() - 1) {
				result += " ; ";
			}
		}
		return result + " }";
	}

	public static Hitbox load(Vector baseVector, ArrayList<Vector> pointList) {
		Hitbox hitbox = new Hitbox(baseVector);
		for (Vector vector : pointList) {
			hitbox.addPoint(vector.getX(), vector.getY());
		}
		return hitbox;
	}

	public void export(ObjectOutputStream outputStream) {
		try {
			for (Vector vector : this.points) {
				outputStream.writeObject(vector);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
