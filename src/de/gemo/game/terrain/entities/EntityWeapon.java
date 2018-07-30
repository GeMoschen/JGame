package de.gemo.game.terrain.entities;

import de.gemo.game.terrain.handler.PhysicsHandler;
import de.gemo.game.terrain.handler.RenderHandler;
import de.gemo.game.terrain.world.World;
import de.gemo.gameengine.units.Vector2f;

import java.lang.reflect.Constructor;

public abstract class EntityWeapon implements IPhysicsObject, IRenderObject {

    protected World _world;
    protected EntityPlayer _owner;

    protected Vector2f _position, _velocity;
    protected float _angle = 0;

    protected float _maxPower = 1.55f;
    protected int _blastRadius = 60;
    protected int _damageRadius = _blastRadius + 10;
    protected float _gravity = 0.009f;

    public EntityWeapon(World world, EntityPlayer owner, Vector2f position, float angle, float power) {
        _world = world;
        _owner = owner;
        _position = position.clone();

        _velocity = new Vector2f();
        _angle = 0;

        init(angle, power);

        // add to handler
        PhysicsHandler.addObject(this);
        RenderHandler.addObject(this);
    }

    public static EntityWeapon fire(Class<? extends EntityWeapon> clazz, World world, EntityPlayer owner, Vector2f position, float angle, float power) {
        // check for null
        if (clazz == null) {
            return null;
        }

        // try to create the object
        try {
            Constructor<? extends EntityWeapon> constructor = clazz.getConstructor(World.class, EntityPlayer.class, Vector2f.class, float.class, float.class);
            return constructor.newInstance(world, owner, position, angle, power);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract boolean cameraFollows();

    protected abstract int getMinDamage();

    protected abstract int getMaxDamage();

    protected abstract void init(float angle, float power);

    protected int[] raycast(int x, int y, int x2, int y2) {
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
            if (_world.isPixelSolid(x, y, false)) {
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
        return _velocity;
    }

    @Override
    public void setPosition(Vector2f position) {
        _position.set(position.getX(), position.getY());
    }

    @Override
    public void setVelocity(Vector2f velocity) {
        _velocity.set(velocity.getX(), velocity.getY());
    }
}
