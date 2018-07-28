package de.gemo.game.terrain.entities.weapons;

import de.gemo.game.terrain.entities.EntityExplosion;
import de.gemo.game.terrain.entities.EntityPlayer;
import de.gemo.game.terrain.entities.EntityWeapon;
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

public class EntityGrenade extends EntityWeapon {

    public static int TIMER = 5;

    private static SingleTexture texture = null;
    private long startTime;
    private int ticksToLive;

    static {
        try {
            texture = TextureManager.loadSingleTexture("resources/weapons/grenade.png", GL_LINEAR);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public EntityGrenade(World world, EntityPlayer owner, Vector2f position, float angle, float power) {
        super(world, owner, position, angle, power);
        this.startTime = System.currentTimeMillis();
        this.ticksToLive = TIMER * GameEngine.$.getTicksPerSecond();
    }

    @Override
    protected void init(float angle, float power) {
        this.gravity = 0.015f;
        this.maxPower = 1.4f;

        // construct velocity
        this.velocity = Vector2f.add(this.position, new Vector2f(0, -maxPower * power * 16));
        this.velocity.rotateAround(this.position, angle);
        this.velocity = Vector2f.sub(this.velocity, this.position);

        // get angle
        this.angle = this.position.getAngle(this.position.getX() + this.velocity.getX(), this.position.getY() + this.velocity.getY());
    }

    @Override
    public boolean cameraFollows() {
        return true;
    }

    @Override
    public void render() {
        glTranslatef(this.position.getX(), this.position.getY(), 0);
        glRotatef(angle, 0, 0, 1);

        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glPushMatrix();
        {
            glTranslatef(3, 2, 0);
            texture.render(1, 1, 1, 1);
        }
        glPopMatrix();

        glPushMatrix();
        {
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            TextureImpl.bindNone();

            int timeLeft = TIMER - (int) ((System.currentTimeMillis() - this.startTime) / 1000f);
            if (timeLeft > 0) {
                TrueTypeFont font = FontManager.getStandardFont(12, Font.BOLD);
                glRotatef(-angle, 0, 0, 1);
                glTranslatef(-font.getWidth("" + timeLeft) / 2, -24, 0);
                font.drawString(0, 0, "" + timeLeft, Color.green);
            }
        }
        glPopMatrix();
    }

    @Override
    public void updatePhysics(int delta) {
        if (this.ticksToLive < 1) {
            this.explode();
            return;
        }
        this.ticksToLive--;

        delta = 16;
        // get velocity
        float vX = this.velocity.getX();
        float vY = this.velocity.getY();

        vY += (gravity * delta);
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

        int[] raycast = this.raycast((int) this.position.getX(), (int) this.position.getY(), (int) (this.position.getX() + vX + 5 * signumX), (int) (this.position.getY() + vY + 5 * signumY));
        if (raycast == null) {
            // advance position
            this.position.move(vX, vY);

            // handle out of bounds
            if (this.world.isOutOfEntityBounds(this.position)) {
                RenderHandler.removeObject(this);
                PhysicsHandler.removeObject(this);
            }

            // set velocity
            this.velocity.set(vX, vY);

            this.angle = this.position.getAngle(this.position.getX() + vX, this.position.getY() + vY);
        } else {
            // get normal
            Vector2f normal = this.world.getNormal(raycast[0], raycast[1]);
            float f = 2F * (this.velocity.getX() * normal.getX() + this.velocity.getY() * normal.getY());
            this.velocity.move(-normal.getX() * f, -normal.getY() * f);

            // friction bounciness
            float bounceFriction = 0.4f;
            this.velocity.set(this.velocity.getX() * bounceFriction, this.velocity.getY() * bounceFriction);
            return;
        }
    }

    private void explode() {
        // remove from handler
        RenderHandler.removeObject(this);
        PhysicsHandler.removeObject(this);

        // explode
        this.world.explode(this.position.getX(), this.position.getY(), this.blastRadius);
        new EntityExplosion(this.world, this.position);

        // scan for players
        List<EntityPlayer> players = PlayerHandler.getPlayersInRadius(this.position, this.damageRadius);
        for (EntityPlayer player : players) {
            // get distance
            float distance = (float) player.getPosition().distanceTo(this.position);

            // give damage to players
            int damage = (int) ((this.maxDamage + 8) * (1 - (distance / this.damageRadius)));
            player.addHealth(-damage);

            // add momentum
            Vector2f toVector = Vector2f.sub(player.getPosition(), this.position);
            toVector = Vector2f.normalize(toVector);
            toVector.setY(toVector.getY() - 0.35f);
            toVector.setX(toVector.getX() * 4.5f);
            toVector.setY(toVector.getY() * 4.5f);
            player.setPushedByWeapon(true);
            player.addVelocity(toVector);
        }
    }
}
