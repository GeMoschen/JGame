package de.gemo.game.terrain.entities;

import java.util.*;

import de.gemo.game.terrain.handler.*;
import de.gemo.game.terrain.utils.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class EntityBazooka implements IPhysicsObject, IRenderObject {

    private Vector2f position, velocity;
    private float dim = 5f;
    private World world;
    private EntityPlayer owner;

    public static float maxPower = 1.75f;
    private static final int maxDamage = 45;
    private static final int blastRadius = 80;
    private static final int damageRadius = 95;
    public static float gravity = 0.009f;

    public EntityBazooka(World world, EntityPlayer owner, Vector2f position, float angle, float power) {
        this.world = world;
        this.owner = owner;
        this.position = position.clone();
        this.velocity = Vector2f.add(this.position, new Vector2f(0, -maxPower * power * GameEngine.INSTANCE.getCurrentDelta()));
        this.velocity.rotateAround(this.position, angle);
        this.velocity = Vector2f.sub(this.velocity, this.position);

        // add to handler
        PhysicsHandler.addObject(this);
        RenderHandler.addObject(this);
    }

    @Override
    public void updatePhysics(int delta) {
        // get velocity
        float vX = this.velocity.getX();
        float vY = this.velocity.getY();

        vY += (gravity * delta);
        vX *= 0.999f;
        vY *= 0.999f;

        int[] raycast = this.raycast((int) this.position.getX(), (int) this.position.getY(), (int) (this.position.getX() + vX), (int) (this.position.getY() + vY));
        if (raycast == null) {
            this.position.move(vX, vY);
        } else {

            // remove from handler
            RenderHandler.removeObject(this);
            PhysicsHandler.removeObject(this);

            // explode
            int midX = raycast[0];
            int midY = raycast[1];

            this.position.set(midX, midY);

            int wallThickness = 7;
            this.world.filledCircle(midX, midY, EntityBazooka.blastRadius, wallThickness, TerrainType.CRATER, false);
            this.world.filledCircle(midX, midY, EntityBazooka.blastRadius - wallThickness, TerrainType.AIR, false);
            this.world.getTerrainParts(midX - EntityBazooka.blastRadius - 2, midY - EntityBazooka.blastRadius - 2, EntityBazooka.blastRadius * 2 + 4, EntityBazooka.blastRadius * 2 + 4, true);

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
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);

        glTranslatef(this.position.getX(), this.position.getY(), 0);
        glColor4f(0, 1, 0, 1);
        glBegin(GL_LINE_LOOP);
        {
            glVertex2f(-this.dim, -this.dim);
            glVertex2f(+this.dim, -this.dim);
            glVertex2f(+this.dim, +this.dim);
            glVertex2f(-this.dim, +this.dim);
        }
        glEnd();
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
