package de.gemo.game.physics.entity;

import de.gemo.game.physics.Physics2D;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

public class EntityCollidable {

	protected Body _body;

	public void init(Body body, float x, float y) {
		_body = body;
		_body.setUserData(this);
		setPosition(x, y);
	}

	public boolean isInside(Vec2 point) {
		return _body.getFixtureList().testPoint(point);
	}

	public Vec2 getPosition() {
		return _body.getPosition();
	}

	public Vec2 getWorldCenter() {
		return _body.getWorldCenter();
	}

	public float getX() {
		return getPosition().x;
	}

	public float getY() {
		return getPosition().y;
	}

	public void setPosition(float x, float y) {
		_body.getPosition().set(x / Physics2D.PX_PER_M, y / Physics2D.PX_PER_M);
	}

	public void setPosition(Vec2 vector) {
		_body.setTransform(vector, _body.getAngle());
	}

	public Vec2 getLinearVelocity() {
		return _body.getLinearVelocity();
	}

	public void setLinearVelocity(float x, float y) {
		_body.getLinearVelocity().set(x, y);
		_body.setAwake(true);
	}

	public void setLinearVelocity(Vec2 vector) {
		_body.setLinearVelocity(vector);
	}

	public float getAngularVelocity() {
		return _body.getAngularVelocity();
	}

	public float getAngle() {
		return (float) Math.toDegrees(_body.getAngle());
	}

	public void setAngle(float angle) {
		_body.setTransform(getPosition(), (float) Math.toRadians(angle));
	}

	public Body getBody() {
		return _body;
	}

	public void render() {
	}

	public void debugRender() {
	}

	public void updatePrePhysics(int delta) {
	}

	public void updatePostPhysics(int delta) {
	}

	public void tick() {
	}

	public boolean beginCollision(EntityCollidable entity, Contact contact) {
		return true;
	}

	public boolean endCollision(EntityCollidable entity, Contact contact) {
		return true;
	}

	public void destroyBody() {
		Physics2D._world.destroyBody(_body);
	}

	public float getDistance(EntityCollidable entity) {
		return getDistance(entity.getPosition());
	}

	public float getDistanceSquared(EntityCollidable entity) {
		return getDistanceSquared(entity.getPosition());
	}

	public float getDistance(Vec2 vector) {
		return MathUtils.distance(vector, getPosition());
	}

	public float getDistanceSquared(Vec2 vector) {
		return MathUtils.distanceSquared(vector, getPosition());
	}
}
