package de.gemo.game.terrain.entities.weapons;

import de.gemo.game.terrain.entities.EntityCloud;
import de.gemo.game.terrain.entities.EntityExplosion;
import de.gemo.game.terrain.entities.EntityPlayer;
import de.gemo.game.terrain.entities.EntityWeapon;
import de.gemo.game.terrain.handler.PhysicsHandler;
import de.gemo.game.terrain.handler.PlayerHandler;
import de.gemo.game.terrain.handler.RenderHandler;
import de.gemo.game.terrain.world.World;
import de.gemo.gameengine.manager.TextureManager;
import de.gemo.gameengine.textures.SingleTexture;
import de.gemo.gameengine.units.Vector2f;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class EntityBazooka extends EntityWeapon {

    private static SingleTexture TEXTURE = null;

    static {
        try {
            TEXTURE = TextureManager.loadSingleTexture("resources/weapons/bazooka.png", GL_LINEAR);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public EntityBazooka(World world, EntityPlayer owner, Vector2f position, float angle, float power) {
        super(world, owner, position, angle, power);
    }

    @Override
    protected void init(float angle, float power) {
        // construct velocity
        _velocity = Vector2f.add(_position, new Vector2f(0, -_maxPower * power * 16));
        _velocity.rotateAround(_position, angle);
        _velocity = Vector2f.sub(_velocity, _position);

        // get _angle
        _angle = _position.getAngle(_position.getX() + _velocity.getX(), _position.getY() + _velocity.getY());
    }

    @Override
    public boolean cameraFollows() {
        return true;
    }

    @Override
    protected int getMinDamage() {
        return 3;
    }

    @Override
    protected int getMaxDamage() {
        return 60;
    }

    @Override
    public void render() {
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);

        glTranslatef(_position.getX(), _position.getY(), 0);
        glRotatef(_angle - 180, 0, 0, 1);
        glTranslatef(3, 1, 0);
        TEXTURE.render(1, 1, 1, 1);
    }

    int i = 0;

    @Override
    public void updatePhysics(int delta) {
        delta = 16;
        // get velocity
        float vX = _velocity.getX();
        float vY = _velocity.getY();

        _angle = _position.getAngle(_position.getX() + vX, _position.getY() + vY);

        vY += (_gravity * delta);
        vX *= 0.999f;
        vY *= 0.999f;

        int[] raycast = raycast((int) _position.getX(), (int) _position.getY(), (int) (_position.getX() + vX), (int) (_position.getY() + vY));
        if (raycast == null) {
            // create clouds behind
            i++;
            // if (i == 0) {
            Vector2f spawnPosition = _position.clone();
            spawnPosition.move(-vX / 6, -vY / 6);
            new EntityCloud(_world, spawnPosition);
            i = 0;
            // }

            // advance position
            _position.move(vX, vY);

            // handle out of bounds
            if (_world.isOutOfEntityBounds(_position)) {
                RenderHandler.removeObject(this);
                PhysicsHandler.removeObject(this);
            }

            // set velocity
            _velocity.set(vX, vY);
        } else {

            // updatePosition position
            _position.set(raycast[0], raycast[1]);

            // explode
            explode();
        }
    }

    private void explode() {
        // remove from handler
        RenderHandler.removeObject(this);
        PhysicsHandler.removeObject(this);

        // updatePosition world
        _world.explode(_position.getX(), _position.getY(), _blastRadius);
        new EntityExplosion(_world, _position);

        // scan for players
        List<EntityPlayer> players = PlayerHandler.getPlayersInRadius(_position, _damageRadius);
        for (EntityPlayer player : players) {
            // get distance
            float distance = (float) player.getPosition().distanceTo(_position);

            // give damage to players
            int damage = (int) ((getMaxDamage()) * (1 - (distance / _damageRadius)));
            damage = Math.max(damage, getMinDamage());
            damage = Math.min(damage, getMaxDamage());
            player.addHealth(-damage);

            // add momentum
            Vector2f toVector = Vector2f.sub(player.getPosition(), _position);
            toVector = Vector2f.normalize(toVector);
            toVector.setY(toVector.getY() - 0.35f);
            toVector.setX(toVector.getX() * 5f);
            toVector.setY(toVector.getY() * 5f);
            player.setPushedByWeapon(true);
            player.addVelocity(toVector);
        }
    }
}
