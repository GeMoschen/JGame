package de.gemo.game.terrain.entities;

import de.gemo.game.terrain.handler.PhysicsHandler;
import de.gemo.game.terrain.handler.RenderHandler;
import de.gemo.game.terrain.world.World;
import de.gemo.gameengine.manager.TextureManager;
import de.gemo.gameengine.textures.Animation;
import de.gemo.gameengine.textures.MultiTexture;
import de.gemo.gameengine.textures.SingleTexture;
import de.gemo.gameengine.units.Vector2f;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class EntityExplosion implements IPhysicsObject, IRenderObject {

    private static Animation ANIMATION = null;

    static {
        try {
            final SingleTexture singleTexture = TextureManager.loadSingleTexture("resources/fx/explosion.png", GL_LINEAR);
            final int dim = 256;
            final MultiTexture multiTexture = new MultiTexture(dim, dim);
            for (int y = 0; y < dim * 6; y += dim) {
                for (int x = 0; x < dim * 8; x += dim) {
                    multiTexture.addTextures(singleTexture.crop(x, y, dim, dim));
                }
            }
            ANIMATION = multiTexture.toAnimation();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Animation _animation;
    private World _world;
    private Vector2f _position;

    public EntityExplosion(World world, Vector2f position) {
        _animation = ANIMATION.clone();
        _animation.setEndListener(new Runnable() {
            @Override
            public void run() {
                // remove from handler
                PhysicsHandler.removeObject(EntityExplosion.this);
                RenderHandler.removeObject(EntityExplosion.this);
            }
        });
        _world = world;
        _position = position.clone();

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
        glScalef(0.5f, 0.5f, 1f);
        _animation.render(1, 1, 1, 1);
    }

    @Override
    public void updatePhysics(int delta) {
        _animation.step(40);
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
