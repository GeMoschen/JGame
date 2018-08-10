package de.gemo.game.physics.entity;

import de.gemo.game.physics.Physics2D;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

public class Wall extends EntityCollidable {

	private static Color color = new Color(0, 0, 0);

	private float halfWidth, halfHeight;
	private boolean left = false;
	private boolean right = false;

	public Wall(float x, float y, float width, float height, boolean left, boolean right) {
		halfWidth = width / 2f;
		halfHeight = height / 2f;

		// box
		BodyDef def = new BodyDef();
		def.type = BodyType.STATIC;
		def.position.set(x / Physics2D.PX_PER_M, y / Physics2D.PX_PER_M);
		def.angle = 0;

		Body body = Physics2D._world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(halfWidth / Physics2D.PX_PER_M, halfHeight / Physics2D.PX_PER_M);
		FixtureDef fDef = new FixtureDef();
		fDef.shape = shape;
		fDef.density = 1;
		body.createFixture(fDef);
		this.left = left;
		this.right = right;

		this.init(body, x, y);
	}

	public void update(int delta) {
	}

	@Override
	public void debugRender() {
		glLineWidth(1);
		Vec2 pos = this._body.getPosition();

		glPushMatrix();
		{
			// translate to _center
			glTranslatef(pos.x * Physics2D.PX_PER_M, pos.y * Physics2D.PX_PER_M, 0);
			glRotatef(this.getAngle(), 0, 0, 1);

			// bind color
			if (Physics2D.SELECTED == getBody()) {
				glColor3f(0.8f, 0.1f, 0.1f);
			} else {
				Wall.color.bind();
			}

			// render
			glBegin(GL_POLYGON);
			glVertex3f(-halfWidth, -halfHeight, 0f);
			glVertex3f(+halfWidth, -halfHeight, 0f);
			glVertex3f(+halfWidth, +halfHeight, 0f);
			glVertex3f(-halfWidth, +halfHeight, 0f);
			glEnd();

			// render outline
			Color.white.bind();
			short stipplePattern = (short) 0xAAAA; // 0xAAAA = 1010 1010 1010
			// 1010
			glEnable(GL_LINE_STIPPLE); // enable stippling
			glLineStipple(5, stipplePattern);

			if (left) {
				glBegin(GL_LINES);
				glVertex3f(-halfWidth, -halfHeight, 0f);
				glVertex3f(-halfWidth, +halfHeight, 0f);
				glEnd();
			}

			if (right) {
				glBegin(GL_LINES);
				glVertex3f(+halfWidth, -halfHeight, 0f);
				glVertex3f(+halfWidth, +halfHeight, 0f);
				glEnd();
			}
		}
		glPopMatrix();
	}

	@Override
	public void render() {
		this.debugRender();
	}

	public float getHalfWidth() {
		return halfWidth;
	}

	public float getHalfHeight() {
		return halfHeight;
	}

	public boolean isLeft() {
		return left;
	}

	public boolean isRight() {
		return right;
	}
}
