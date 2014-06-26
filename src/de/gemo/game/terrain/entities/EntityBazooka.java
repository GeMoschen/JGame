package de.gemo.game.terrain.entities;

import java.io.*;
import java.util.*;

import de.gemo.game.terrain.handler.*;
import de.gemo.game.terrain.world.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.textures.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class EntityBazooka implements IPhysicsObject, IRenderObject {

    private Vector2f position, velocity;
    private World world;
    private EntityPlayer owner;
    private float angle = 0;

    public static float maxPower = 1.55f;
    private static final int maxDamage = 45;
    private static final int blastRadius = 80;
    private static final int damageRadius = 95;
    public static float gravity = 0.009f;
    private static SingleTexture texture = null;

    static {
        try {
            texture = TextureManager.loadSingleTexture("resources/weapons/bazooka.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EntityBazooka(World world, EntityPlayer owner, Vector2f position, float angle, float power) {
        this.world = world;
        this.owner = owner;
        this.position = position.clone();
        this.velocity = Vector2f.add(this.position, new Vector2f(0, -maxPower * power * 16));
        this.velocity.rotateAround(this.position, angle);
        this.velocity = Vector2f.sub(this.velocity, this.position);

        // add to handler
        PhysicsHandler.addObject(this);
        RenderHandler.addObject(this);
    }

    @Override
    public void updatePhysics(int delta) {

        delta = 16;
        // get velocity
        float vX = this.velocity.getX();
        float vY = this.velocity.getY();

        this.angle = this.position.getAngle(this.position.getX() + vX, this.position.getY() + vY);

        vY += (gravity * delta);
        vX *= 0.999f;
        vY *= 0.999f;

        int[] raycast = this.raycast((int) this.position.getX(), (int) this.position.getY(), (int) (this.position.getX() + vX), (int) (this.position.getY() + vY));
        if (raycast == null) {
            // advance position
            this.position.move(vX, vY);

            // handle out of bounds
            if (this.world.isOutOfEntityBounds(this.position)) {
                RenderHandler.removeObject(this);
                PhysicsHandler.removeObject(this);
            }
        } else {
            // remove from handler
            RenderHandler.removeObject(this);
            PhysicsHandler.removeObject(this);

            // explode
            int midX = raycast[0];
            int midY = raycast[1];

            this.position.set(midX, midY);

            this.world.explode(midX, midY, EntityBazooka.blastRadius);

            List<EntityPlayer> players = PlayerHandler.getPlayersInRadius(this.position, EntityBazooka.damageRadius);
            for (EntityPlayer player : players) {
                float distance = (float) player.getPosition().distanceTo(this.position);
                int damage = (int) ((EntityBazooka.maxDamage + 8) * (1 - (distance / EntityBazooka.damageRadius)));
                player.addHealth(-damage);
            }
        }
        this.velocity.set(vX, vY);
    }

    public int[] raycast(int x, int y, int x2, int y2) {
        int w = x2 - x;
        int h = y2 - y;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
        if (w < 0)
            dx1 = -1;
        else if (w > 0)
            dx1 = 1;
        if (h < 0)
            dy1 = -1;
        else if (h > 0)
            dy1 = 1;
        if (w < 0)
            dx2 = -1;
        else if (w > 0)
            dx2 = 1;
        int longest = Math.abs(w);
        int shortest = Math.abs(h);
        if (!(longest > shortest)) {
            longest = Math.abs(h);
            shortest = Math.abs(w);
            if (h < 0)
                dy2 = -1;
            else if (h > 0)
                dy2 = 1;
            dx2 = 0;
        }
        int numerator = longest >> 1;
        for (int i = 0; i <= longest; i++) {
            if (this.world.isPixelSolid(x, y, false)) {
                int[] result = new int[2];
                result[0] = x;
                result[1] = y;
                return result;
            }
            numerator += shortest;
            if (!(numerator < longest)) {
                numerator -= longest;
                x += dx1;
                y += dy1;
            } else {
                x += dx2;
                y += dy2;
            }
        }
        return null;
    }

    @Override
    public void render() {
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);

        glTranslatef(this.position.getX(), this.position.getY(), 0);
        glRotatef(this.angle - 180, 0, 0, 1);
        glTranslatef(1, 1, 0);
        texture.render(1, 1, 1, 1);
    }

    // ///////////////////////////////////////////////////////////////
    //
    // PhysicsObject
    //
    // ///////////////////////////////////////////////////////////////

    @Override
    public Vector2f getPosition() {
        return this.position;
    }

    @Override
    public Vector2f getVelocity() {
        return this.velocity;
    }

    @Override
    public void setPosition(Vector2f position) {
        this.position.set(position.getX(), position.getY());
    }

    @Override
    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity.getX(), velocity.getY());
    }
}