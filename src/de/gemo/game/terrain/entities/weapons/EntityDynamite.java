package de.gemo.game.terrain.entities.weapons;

import de.gemo.game.terrain.entities.*;
import de.gemo.game.terrain.handler.PhysicsHandler;
import de.gemo.game.terrain.handler.PlayerHandler;
import de.gemo.game.terrain.handler.RenderHandler;
import de.gemo.game.terrain.world.World;
import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.manager.FontManager;
import de.gemo.gameengine.manager.TextureManager;
import de.gemo.gameengine.textures.SingleTexture;
import de.gemo.gameengine.units.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class EntityDynamite extends EntityWeapon implements WeaponNoCrosshair, WeaponDirectShoot {

    public static int TIMER = 5;
    private static SingleTexture TEXTURE = null;

    private long _startTime;
    private int _ticksToLive;

    static {
        try {
            TEXTURE = TextureManager.loadSingleTexture("resources/weapons/dynamite.png");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public EntityDynamite(World world, EntityPlayer owner, Vector2f position, float angle, float power) {
        super(world, owner, position, angle, power);
        _startTime = System.currentTimeMillis();
        _ticksToLive = TIMER * GameEngine.$.getTicksPerSecond();
    }

    @Override
    protected void init(float angle, float power) {
        _maxDamage = 65;
        _gravity = 0.015f;
        _maxPower = 0.1f;
        _blastRadius = 100;

        // construct velocity
        _velocity = Vector2f.add(_position, new Vector2f(0, -_maxPower * power * 16));
        _velocity.rotateAround(_position, angle);
        _velocity = Vector2f.sub(_velocity, _position);
    }

    @Override
    public boolean cameraFollows() {
        return false;
    }

    @Override
    public void render() {
        glTranslatef(_position.getX(), _position.getY(), 0);

        glPushMatrix();
        {
            // glBegin(GL_LINE_LOOP);
            // {
            // glVertex2i(-3, -15);
            // glVertex2i(+3, -15);
            // glVertex2i(+3, +15);
            // glVertex2i(-3, +15);
            // }
            // glEnd();
        }
        glPopMatrix();

        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glPushMatrix();
        {
            glTranslatef(1, 0, 0);
            TEXTURE.render(1, 1, 1, 1);
        }
        glPopMatrix();

        glPushMatrix();
        {
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            TextureImpl.bindNone();

            int timeLeft = TIMER - (int) ((System.currentTimeMillis() - _startTime) / 1000f);
            if (timeLeft > 0) {
                TrueTypeFont font = FontManager.getStandardFont(12, Font.BOLD);
                glTranslatef(-font.getWidth("" + timeLeft) / 2, -24, 0);
                font.drawString(0, 0, "" + timeLeft, Color.green);
            }
        }
        glPopMatrix();
    }

    @Override
    public void updatePhysics(int delta) {
        if (_ticksToLive < 1) {
            explode();
            return;
        }
        _ticksToLive--;

        delta = 16;
        // get velocity
        float vX = _velocity.getX();
        float vY = _velocity.getY();

        vY += (_gravity * delta);
        vX *= 0.999f;
        vY *= 0.999f;

        int signumX = 0, signumY = 0;
        if (vX < 0) {
            signumX = -1;
        }
        if (vY < 0) {
            signumY = -1;
        }

        if (vX > 0) {
            signumX = +1;
        }
        if (vY > 0) {
            signumY = +1;
        }

        int[] raycast = raycast((int) _position.getX(), (int) _position.getY(), (int) (_position.getX() + vX + 15 * signumX), (int) (_position.getY() + vY + 15 * signumY));
        if (raycast == null) {
            // advance position
            _position.move(vX, vY);

            // handle out of bounds
            if (_world.isOutOfEntityBounds(_position)) {
                RenderHandler.removeObject(this);
                PhysicsHandler.removeObject(this);
            }

            // set velocity
            _velocity.set(vX, vY);

            _angle = _position.getAngle(_position.getX() + vX, _position.getY() + vY);
        } else {
            // get normal
            Vector2f normal = _world.getNormal(raycast[0], raycast[1]);
            float f = 2F * (_velocity.getX() * normal.getX() + _velocity.getY() * normal.getY());
            _velocity.move(-normal.getX() * f, -normal.getY() * f);

            // friction bounciness
            float bounceFriction = 0.2f;
            _velocity.set(_velocity.getX() * bounceFriction, _velocity.getY() * bounceFriction);
            return;
        }
    }

    private void explode() {
        // remove from handler
        RenderHandler.removeObject(this);
        PhysicsHandler.removeObject(this);

        // explode
        _world.explode(_position.getX(), _position.getY(), _blastRadius, _blastRadius - 25);
        new EntityExplosion(_world, _position);

        // scan for players
        List<EntityPlayer> players = PlayerHandler.getPlayersInRadius(_position, _damageRadius);
        for (EntityPlayer player : players) {
            // get distance
            float distance = (float) player.getPosition().distanceTo(_position);

            // give damage to players
            int damage = (int) ((_maxDamage + 8) * (1 - (distance / _damageRadius)));
            player.addHealth(-damage);

            // add momentum
            Vector2f toVector = Vector2f.sub(player.getPosition(), _position);
            toVector = Vector2f.normalize(toVector);
            toVector.setY(toVector.getY() - 0.7f);
            toVector.setX(toVector.getX() * 5.5f);
            toVector.setY(toVector.getY() * 5.5f);
            player.setPushedByWeapon(true);
            player.addVelocity(toVector);
        }
    }
}
