package de.gemo.game.terrain.entities;

import java.io.*;

import de.gemo.game.terrain.handler.*;
import de.gemo.game.terrain.world.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.textures.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class EntityCloud implements IPhysicsObject, IRenderObject {

    private static SingleTexture TEXTURE = null;
    
    private World _world;
    private Vector2f _position;
    private float _lifeTime, _scale, _angle;


    static {
        try {
            TEXTURE = TextureManager.loadSingleTexture("resources/fx/cloud_64x64.png");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public EntityCloud(World world, Vector2f position) {
        _world = world;
        _position = position.clone();
        _lifeTime = 1f;
        _scale = 1f;
        _angle = (float) (Math.random() * 360f);

        // add to handler
        PhysicsHandler.addObject(this);
        RenderHandler.addObject(this);
    }

    @Override
    public void render() {
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glTranslatef(_position.getX(), _position.getY(), -1);
        glRotatef(_angle, 0, 0, 1);
        glScalef(0.17f * _scale, 0.17f * _scale, 1f);
        TEXTURE.render(1, 1, 1, _lifeTime * 0.75f);
    }

    @Override
    public void updatePhysics(int delta) {
        _lifeTime -= 0.08f;
        _scale += 0.12f;
        if (_lifeTime <= 0) {
            // remove from handler
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
        return _position;
    }

    @Override
    public Vector2f getVelocity() {
        return new Vector2f();
    }

    @Override
    public void setPosition(Vector2f position) {
        _position.set(position.getX(), position.getY());
    }

    @Override
    public void setVelocity(Vector2f velocity) {
        // do nothing
    }

}
