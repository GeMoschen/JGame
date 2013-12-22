package de.gemo.gameengine.collision;

public interface ICollisionable {

	public int getScreenTileX();

	public int getScreenTileY();

	public boolean isDead();

	public void setDead();

	public void handleCollision(ICollisionable collisionable);

	public boolean broadphaseColliding(ICollisionable collisionable);

	public boolean narrowphaseColliding(ICollisionable collisionable);

	public void setHitbox(Hitbox hitbox);

	public Hitbox getHitbox();
}
