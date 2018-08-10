package de.gemo.game.physics.entity;

import de.gemo.game.physics.Physics2D;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Ground extends EntityCollidable implements GrabberHolder {

	private final float _friction;
	private float _halfWidth, _halfHeight;
	private final boolean _down;

	private final List<Grabber> _grabbers = new ArrayList<>();
	private PolygonShape _shape;

	public Ground(float x, float y, float width, float height, boolean down) {
		this(x, y, width, height, down, 1.0f);
	}

	public Ground(float x, float y, float width, float height, boolean down, float friction) {
		_friction = friction;
		_halfWidth = width / 2f;
		_halfHeight = height / 2f;

		_down = down;
		createBody(x, y, friction);
	}

	private void createBody(final float x, final float y, final float friction) {
		if (_body != null) {
			Physics2D._world.destroyBody(_body);
			_body = null;
		}
		// box
		BodyDef def = new BodyDef();
		def.type = BodyType.STATIC;
		def.position.set(x / Physics2D.PX_PER_M, y / Physics2D.PX_PER_M);
		def.angle = 0;

		final Body body = Physics2D._world.createBody(def);
		final PolygonShape shape = new PolygonShape();
		shape.setAsBox(_halfWidth / Physics2D.PX_PER_M, _halfHeight / Physics2D.PX_PER_M);
		FixtureDef fDef = new FixtureDef();
		fDef.shape = shape;
		fDef.density = 1;
		fDef.friction = friction;
		_shape = (PolygonShape) body.createFixture(fDef).getShape();

		super.init(body, x, y);
		_body = body;

		// grabber
		_grabbers.clear();
		final float offsetX = getHalfWidth() / Physics2D.PX_PER_M;
		final float offsetY = getHalfHeight() / Physics2D.PX_PER_M;
		addGrabber(friction, -offsetX, -offsetY, GrabDirection.TOP_LEFT);
		addGrabber(friction, -offsetX, +offsetY, GrabDirection.BOTTOM_LEFT);
		addGrabber(friction, +offsetX, +offsetY, GrabDirection.BOTTOM_RIGHT);
		addGrabber(friction, +offsetX, -offsetY, GrabDirection.TOP_RIGHT);
	}

	private void addGrabber(final float friction, final float offsetX, final float offsetY, final GrabDirection grabDirection) {
		_grabbers.add(new Grabber(this, friction, offsetX, offsetY, grabDirection));
	}

	@Override
	public Grabber getGrabber(final GrabDirection direction) {
		return _grabbers.get(direction.ordinal());
	}

	@Override
	public void moveGrabber(final float x, final float y, final GrabDirection direction) {
		getGrabber(direction).move(x, y);
		switch (direction) {
			case TOP_LEFT: {
				getGrabber(GrabDirection.BOTTOM_LEFT).move(x, 0);
				getGrabber(GrabDirection.TOP_RIGHT).move(0, y);
				moveVertex(0, x, y);
				moveVertex(3, x, 0);
				moveVertex(1, 0, y);
				break;
			}
			case BOTTOM_LEFT: {
				getGrabber(GrabDirection.TOP_LEFT).move(x, 0);
				getGrabber(GrabDirection.BOTTOM_RIGHT).move(0, y);
				moveVertex(3, x, y);
				moveVertex(0, x, 0);
				moveVertex(2, 0, y);
				break;
			}
			case BOTTOM_RIGHT: {
				getGrabber(GrabDirection.TOP_RIGHT).move(x, 0);
				getGrabber(GrabDirection.BOTTOM_LEFT).move(0, y);
				moveVertex(2, x, y);
				moveVertex(1, x, 0);
				moveVertex(3, 0, y);
				break;
			}
			case TOP_RIGHT: {
				getGrabber(GrabDirection.TOP_LEFT).move(0, y);
				getGrabber(GrabDirection.BOTTOM_RIGHT).move(x, 0);
				moveVertex(1, x, y);
				moveVertex(0, 0, y);
				moveVertex(2, x, 0);
				break;
			}
		}
		_halfWidth = (getVertex(1).x - getVertex(0).x) / 2 * Physics2D.PX_PER_M;
		_halfHeight = (getVertex(2).y - getVertex(0).y) / 2 * Physics2D.PX_PER_M;
		final Vec2 position = getPosition();
		position.x += x / 2;
		position.y += y / 2;
		createBody(getX() * Physics2D.PX_PER_M, getY() * Physics2D.PX_PER_M, _friction);
	}

	private Vec2 getVertex(final int index) {
		return _shape.getVertex(index);
	}

	private void moveVertex(final int index, final float x, final float y) {
		getVertex(index).x += x;
		getVertex(index).y += y;
	}

	public float getHalfHeight() {
		return _halfHeight;
	}

	public float getHalfWidth() {
		return _halfWidth;
	}

	@Override
	public void debugRender() {
		glLineWidth(1);
		Vec2 pos = _body.getPosition();

		glPushMatrix();
		{
			// translate to _center
			glTranslatef(pos.x * Physics2D.PX_PER_M, pos.y * Physics2D.PX_PER_M, 0);
			glRotatef(getAngle(), 0, 0, 1);

			if (Physics2D.SELECTED == getBody()) {
				glColor3f(0.8f, 0.1f, 0.1f);
			} else {
				Color.black.bind();
			}

			// render _center
			final Vec2[] vertices = _shape.getVertices();
			renderPolygon(vertices, _shape.getVertexCount());

			// render down
			if (_down) {
				glLineWidth(2);
				glDisable(GL_LINE_STIPPLE);
				Color.white.bind();
				glBegin(GL_LINES);
				glVertex3f(vertices[2].x * Physics2D.PX_PER_M, vertices[2].y * Physics2D.PX_PER_M, 0f);
				glVertex3f(vertices[3].x * Physics2D.PX_PER_M, vertices[3].y * Physics2D.PX_PER_M, 0f);
				glEnd();
			}

			// render grabbers
			for (final Grabber grabber : _grabbers) {
				grabber.debugRender();
			}
		}
		glPopMatrix();

	}

	private void renderPolygon(final Vec2[] vertices, final int vertexCount) {
		glBegin(GL_POLYGON);
		for (int i = 0; i < vertexCount; i++) {
			glVertex3f(vertices[i].x * Physics2D.PX_PER_M, vertices[i].y * Physics2D.PX_PER_M, 0f);
		}
		glEnd();
	}

	public void render() {
		debugRender();
	}

	public boolean isDown() {
		return _down;
	}


}
