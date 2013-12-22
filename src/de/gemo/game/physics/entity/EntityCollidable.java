package de.gemo.game.physics.entity;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import de.gemo.game.physics.Physics2D;

public class EntityCollidable {

	protected Body body;

	public void init(Body body, float x, float y) {
		this.body = body;
		this.body.setUserData(this);
		this.setPosition(x, y);
	}

	public boolean isInside(Vec2 point) {
		return this.body.getFixtureList().testPoint(point);
	}

	public Vec2 getPosition() {
		return this.body.getPosition();
	}

	public Vec2 getWorldCenter() {
		return this.body.getWorldCenter();
	}

	public float getX() {
		return this.getPosition().x;
	}

	public float getY() {
		return this.getPosition().y;
	}

	public void setPosition(float x, float y) {
		this.body.getPosition().set(x / Physics2D.pxPerM, y / Physics2D.pxPerM);
		// this.body.setAwake(true);
	}

	public void setPosition(Vec2 vector) {
		this.body.setTransform(vector, this.body.getAngle());
	}

	public Vec2 getLinearVelocity() {
		return this.body.getLinearVelocity();
	}

	public void setLinearVelocity(float x, float y) {
		this.body.getLinearVelocity().set(x, y);
		this.body.setAwake(true);
	}

	public void setLinearVelocity(Vec2 vector) {
		this.body.setLinearVelocity(vector);
	}

	public float getAngularVelocity() {
		return this.body.getAngularVelocity();
	}

	public float getAngle() {
		return (float) Math.toDegrees(this.body.getAngle());
	}

	public void setAngle(float angle) {
		this.body.setTransform(this.getPosition(), (float) Math.toRadians(angle));
	}

	public Body getBody() {
		return body;
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
		Physics2D.world.destroyBody(this.body);
	}

	public float getDistance(EntityCollidable entity) {
		return getDistance(entity.getPosition());
	}

	public float getDistanceSquared(EntityCollidable entity) {
		return getDistanceSquared(entity.getPosition());
	}

	public float getDistance(Vec2 vector) {
		return MathUtils.distance(vector, this.getPosition());
	}

	public float getDistanceSquared(Vec2 vector) {
		return MathUtils.distanceSquared(vector, this.getPosition());
	}
}
