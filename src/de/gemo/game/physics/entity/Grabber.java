package de.gemo.game.physics.entity;

import de.gemo.game.physics.Physics2D;
import de.gemo.gameengine.renderer.IRenderable;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

public class Grabber implements IRenderable {

	private final GrabDirection _grabDirection;

	private final Fixture _fixture;
	private PolygonShape _shape;

	public Grabber(final EntityCollidable entity, final float friction, final float offsetX, final float offsetY, final GrabDirection grabDirection) {
		_grabDirection = grabDirection;
		_fixture = createShape(entity, friction, offsetX, offsetY, grabDirection);
		_shape = (PolygonShape) _fixture.getShape();
	}

	private Fixture createShape(final EntityCollidable entity, final float friction, final float offsetX, final float offsetY, final GrabDirection grabDirection) {
		final PolygonShape shape = new PolygonShape();
		final float size = 5f / Physics2D.PX_PER_M;
		shape.setAsBox(size, size, new Vec2(offsetX, offsetY), 0f);
		FixtureDef fDef = new FixtureDef();
		fDef.shape = shape;
		fDef.density = 1;
		fDef.friction = friction;
		fDef.userData = new DelegatingUserData(grabDirection, entity, DelegatingUserData.Type.GRAB);
		return entity.getBody().createFixture(fDef);
	}

	public Fixture getFixture() {
		return _fixture;
	}

	public GrabDirection getGrabDirection() {
		return _grabDirection;
	}

	@Override
	public void addToRenderPipeline() {

	}

	@Override
	public void render() {

	}

	@Override
	public void debugRender() {
		Color.red.bind();
		glBegin(GL_POLYGON);
		for (int i = 0; i < _shape.getVertexCount(); i++) {
			glVertex3f(_shape.m_vertices[i].x * Physics2D.PX_PER_M, _shape.m_vertices[i].y * Physics2D.PX_PER_M, 0f);
		}
		glEnd();
	}

	public void move(final float x, final float y) {
		for (int index = 0; index < _shape.getVertexCount(); index++) {
			_shape.m_vertices[index].x += x;
			_shape.m_vertices[index].y += y;
		}
	}
}