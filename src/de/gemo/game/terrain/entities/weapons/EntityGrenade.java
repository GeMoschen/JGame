package de.gemo.game.terrain.entities.weapons;

import java.awt.Font;
import java.io.*;
import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.opengl.*;

import de.gemo.game.terrain.entities.*;
import de.gemo.game.terrain.handler.*;
import de.gemo.game.terrain.world.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.textures.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class EntityGrenade extends EntityWeapon {

    private static SingleTexture texture = null;
    private long startTime;
    private final int timer = 5;
    private int ticksToLive;

    static {
        try {
            texture = TextureManager.loadSingleTexture("resources/weapons/grenade.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EntityGrenade(World world, EntityPlayer owner, Vector2f position, float angle, float power) {
        super(world, owner, position, angle, power);
        this.startTime = System.currentTimeMillis();
        this.ticksToLive = this.timer * GameEngine.INSTANCE.getTicksPerSecond();
    }

    @Override
    protected void init(float angle, float power) {
        this.gravity = 0.015f;
        this.maxPower = 1.25f;

        // construct velocity
        this.velocity = Vector2f.add(this.position, new Vector2f(0, -maxPower * power * 16));
        this.velocity.rotateAround(this.position, angle);
        this.velocity = Vector2f.sub(this.velocity, this.position);

        // get angle
        this.angle = this.position.getAngle(this.position.getX() + this.velocity.getX(), this.position.getY() + this.velocity.getY());
    }

    @Override
    public void render() {
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);

        glTranslatef(this.position.getX(), this.position.getY(), 0);
        glPushMatrix();
        {
            glRotatef(this.angle - 180, 0, 0, 1);
            glTranslatef(1, 1, 0);
            texture.render(1, 1, 1, 1);
        }
        glPopMatrix();

        glPushMatrix();
        {
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            TextureImpl.bindNone();

            int timeLeft = timer - (int) ((System.currentTimeMillis() - this.startTime) / 1000f);
            if (timeLeft > 0) {
                TrueTypeFont font = FontManager.getStandardFont(14, Font.BOLD);
                glTranslatef(-font.getWidth("" + timeLeft) / 2 + 3, -24, 0);
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

        int[] raycast = this.raycast((int) this.position.getX(), (int) this.position.getY(), (int) (this.position.getX() + vX + 9 * signumX), (int) (this.position.getY() + vY + 9 * signumY));
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
            float bounceFriction = 0.48f;
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

        List<EntityPlayer> players = PlayerHandler.getPlayersInRadius(this.position, this.damageRadius);
        for (EntityPlayer player : players) {
            float distance = (float) player.getPosition().distanceTo(this.position);
            int damage = (int) ((this.maxDamage + 8) * (1 - (distance / this.damageRadius)));
            player.addHealth(-damage);
        }
    }
}
