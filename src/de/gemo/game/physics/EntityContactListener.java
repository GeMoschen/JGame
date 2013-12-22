package de.gemo.game.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import de.gemo.game.physics.entity.EntityCollidable;

public class EntityContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        EntityCollidable entityA = null;
        EntityCollidable entityB = null;

        if (contact.getFixtureA().getBody().getUserData() instanceof EntityCollidable) {
            entityA = (EntityCollidable) contact.getFixtureA().getBody().getUserData();
        }
        if (contact.getFixtureB().getBody().getUserData() instanceof EntityCollidable) {
            entityB = (EntityCollidable) contact.getFixtureB().getBody().getUserData();
        }

        if (entityA != null && entityB != null) {
            boolean result = !entityA.beginCollision(entityB, contact);
            result = result || !entityB.beginCollision(entityA, contact);
            if (result) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        EntityCollidable entityA = null;
        EntityCollidable entityB = null;

        if (contact.getFixtureA().getBody().getUserData() instanceof EntityCollidable) {
            entityA = (EntityCollidable) contact.getFixtureA().getBody().getUserData();
        }
        if (contact.getFixtureB().getBody().getUserData() instanceof EntityCollidable) {
            entityB = (EntityCollidable) contact.getFixtureB().getBody().getUserData();
        }

        if (entityA != null && entityB != null) {
            boolean result = !entityA.endCollision(entityB, contact);
            result = result || !entityB.endCollision(entityA, contact);
            if (result) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse arg1) {

    }

    @Override
    public void preSolve(Contact arg0, Manifold arg1) {
    }

}
