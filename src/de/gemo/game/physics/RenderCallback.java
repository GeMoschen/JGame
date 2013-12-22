package de.gemo.game.physics;

import java.util.List;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Fixture;

import de.gemo.game.physics.entity.EntityCollidable;

public class RenderCallback implements QueryCallback {

    private List<EntityCollidable> renderList;

    public RenderCallback(List<EntityCollidable> renderList) {
        this.renderList = renderList;
    }

    @Override
    public boolean reportFixture(Fixture fixture) {
        if (fixture.getBody().getUserData() instanceof EntityCollidable) {
            this.renderList.add((EntityCollidable) fixture.getBody().getUserData());
        }
        return true;
    }
}
