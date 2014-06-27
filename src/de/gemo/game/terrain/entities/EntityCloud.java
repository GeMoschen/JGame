package de.gemo.game.terrain.entities;

import java.io.*;

import de.gemo.game.terrain.handler.*;
import de.gemo.game.terrain.world.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.textures.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class EntityCloud implements IPhysicsObject, IRenderObject {

    protected World world;
    protected Vector2f position;
    protected float lifeTime, scale, angle;

    private static SingleTexture texture = null;

    static {
        try {
            texture = TextureManager.loadSingleTexture("resources/fx/cloud_64x64.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EntityCloud(World world, Vector2f position) {
        this.world = world;
        this.position = position.clone();
        this.lifeTime = 1f;
        this.scale = 1f;
        this.angle = (float) (Math.random() * 360f);

        // add to handler
        PhysicsHandler.addObject(this);
        RenderHandler.addObject(this);
    }

    @Override
    public void render() {
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glTranslatef(this.position.getX(), this.position.getY(), -1);
        glRotatef(this.angle, 0, 0, 1);
        glScalef(0.2f * this.scale, 0.2f * this.scale, 1f);
        texture.render(1, 1, 1, this.lifeTime * 0.75f);
    }

    @Override
    public void updatePhysics(int delta) {
        this.lifeTime -= 0.1f;
        this.scale += 0.15f;
        if (this.lifeTime <= 0) {
            // add to handler
            PhysicsHandler.removeObject(this);
            RenderHandler.removeObject(this);
        }
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
        return new Vector2f();
    }

    @Override
    public void setPosition(Vector2f position) {
        this.position.set(position.getX(), position.getY());
    }

    @Override
    public void setVelocity(Vector2f velocity) {
        // do nothing
    }

}
