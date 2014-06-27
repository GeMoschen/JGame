package de.gemo.game.terrain.entities.weapons;

import java.io.*;
import java.util.*;

import de.gemo.game.terrain.entities.*;
import de.gemo.game.terrain.handler.*;
import de.gemo.game.terrain.world.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.textures.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class EntityBazooka extends EntityWeapon {

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
        super(world, owner, position, angle, power);
    }

    @Override
    protected void init(float angle, float power) {
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
        glRotatef(this.angle - 180, 0, 0, 1);
        glTranslatef(3, 1, 0);
        texture.render(1, 1, 1, 1);
    }

    int i = 0;

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
            // create clouds behind
            i++;
            if (i == 2) {
                Vector2f spawnPosition = this.position.clone();
                spawnPosition.move(-vX / 6, -vY / 6);
                new EntityCloud(this.world, spawnPosition);
                i = 0;
            }

            // advance position
            this.position.move(vX, vY);

            // handle out of bounds
            if (this.world.isOutOfEntityBounds(this.position)) {
                RenderHandler.removeObject(this);
                PhysicsHandler.removeObject(this);
            }

            // set velocity
            this.velocity.set(vX, vY);
        } else {

            // update position
            this.position.set(raycast[0], raycast[1]);

            // explode
            this.explode();
        }
    }

    private void explode() {
        // remove from handler
        RenderHandler.removeObject(this);
        PhysicsHandler.removeObject(this);

        // update world
        this.world.explode(this.position.getX(), this.position.getY(), this.blastRadius);

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
            toVector.setY(toVector.getY() - 0.85f);
            toVector.setX(toVector.getX() * 3.5f);
            toVector.setY(toVector.getY() * 3.5f);
            player.setPushedByWeapon(true);
            player.addVelocity(toVector);
        }
    }
}
