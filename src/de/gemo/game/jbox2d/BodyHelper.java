package de.gemo.game.jbox2d;

import java.util.Random;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class BodyHelper {
    public static Body addBox(World world, float x, float y) {
        Random random = new Random();
        int halfWidth = random.nextInt(30) + 5;
        int halfHeight = random.nextInt(30) + 5;
        float angle = (float) random.nextInt(360);
        return addBox(world, x, y, halfWidth, halfHeight, angle);
    }

    public static Body addBox(World world, float x, float y, float halfWidth, float halfHeight, float angle) {
        // create box
        FixtureDef fd = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfWidth, halfHeight);
        fd.shape = shape;
        fd.density = 25.0F;

        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        fd.friction = 1F;
        fd.restitution = 0.0f;
        bd.position = new Vec2(x, y);

        Body box = world.createBody(bd);
        box.createFixture(fd);
        box.setUserData(0);
        box.m_mass = halfWidth * halfHeight;

        box.setTransform(box.getPosition(), (float) Math.toRadians(angle));
        return box;
    }

    public static Body addSphere(World world, float x, float y) {
        Random random = new Random();
        int radius = random.nextInt(25) + 5;

        // create circle
        FixtureDef fd = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.m_radius = radius;
        fd.shape = shape;
        fd.density = 25.0F;

        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        fd.friction = 1.5F;
        fd.restitution = 0.5f;
        bd.position = new Vec2(x, y);

        Body circle = world.createBody(bd);
        circle.createFixture(fd);
        circle.setUserData(1);
        circle.m_mass = (float) ((Math.PI * (radius * radius)) / 4f);

        return circle;
    }
}
