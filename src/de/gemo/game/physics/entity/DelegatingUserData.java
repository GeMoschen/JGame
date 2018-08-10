package de.gemo.game.physics.entity;

public class DelegatingUserData {

	public enum Type {

		GRAB

	}

	private final Object _userData;
	private final EntityCollidable _entity;
	private final Type _type;

	public DelegatingUserData(final Object userData, final EntityCollidable entity,  final Type type) {
		_userData = userData;
		_entity = entity;
		_type = type;
	}

	public <T> T getUserData() {
		return (T) _userData;
	}

	public EntityCollidable getEntity() {
		return _entity;
	}

	public Type getType() {
		return _type;
	}
}