package de.gemo.game.physics.entity;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_STIPPLE;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.newdawn.slick.Color;

import de.gemo.game.physics.Physics2D;

public class Ground extends EntityCollidable {

	private float halfWidth, halfHeight;
	private boolean down;

	public Ground(float x, float y, float width, float height, boolean down) {
		this(x, y, width, height, down, 1.0f);
	}

	public Ground(float x, float y, float width, float height, boolean down, float friction) {
		halfWidth = width / 2f;
		halfHeight = height / 2f;

		this.down = down;

		// box
		BodyDef def = new BodyDef();
		def.type = BodyType.STATIC;
		def.position.set(x / Physics2D.pxPerM, y / Physics2D.pxPerM);
		def.angle = 0;

		Body body = Physics2D.world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(halfWidth / Physics2D.pxPerM, halfHeight / Physics2D.pxPerM);
		FixtureDef fDef = new FixtureDef();
		fDef.shape = shape;
		fDef.density = 1;
		fDef.friction = friction;
		body.createFixture(fDef);

		super.init(body, x, y);
	}

	public float getHalfHeight() {
		return halfHeight;
	}

	public float getHalfWidth() {
		return halfWidth;
	}

	@Override
	public void debugRender() {
		glLineWidth(1);
		Vec2 pos = this.body.getPosition();

		glPushMatrix();
		{
			// translate to center
			glTranslatef(pos.x * Physics2D.pxPerM, pos.y * Physics2D.pxPerM, 0);
			glRotatef(this.getAngle(), 0, 0, 1);
			Color.black.bind();

			// render center
			glBegin(GL_POLYGON);
			glVertex3f(-halfWidth, -halfHeight, 0f);
			glVertex3f(+halfWidth, -halfHeight, 0f);
			glVertex3f(+halfWidth, +halfHeight, 0f);
			glVertex3f(-halfWidth, +halfHeight, 0f);
			glEnd();

			// render down
			if (down) {
				glLineWidth(2);
				glDisable(GL_LINE_STIPPLE);
				Color.white.bind();
				glBegin(GL_LINES);
				glVertex3f(-halfWidth, +halfHeight, 0f);
				glVertex3f(+halfWidth, +halfHeight, 0f);
				glEnd();
			}
		}
		glPopMatrix();
	}

	public void render() {
		this.debugRender();
	}

	public boolean isDown() {
		return down;
	}
}
