package de.gemo.game.physics;

import de.gemo.game.physics.entity.EntityCollidable;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Fixture;

import java.util.ArrayList;
import java.util.List;

public class RenderCallback implements QueryCallback {

	private final List<EntityCollidable> _renderList;

	public RenderCallback() {
		_renderList = new ArrayList<>();
	}

	@Override
	public boolean reportFixture(Fixture fixture) {
		if (fixture.getBody().getUserData() instanceof EntityCollidable) {
			_renderList.add((EntityCollidable) fixture.getBody().getUserData());
		}
		return true;
	}

	public void reset() {
		_renderList.clear();
	}

	public List<EntityCollidable> getRenderList() {
		return _renderList;
	}
}
