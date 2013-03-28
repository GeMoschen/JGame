package de.gemo.game.jbox2d.tests;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import de.gemo.engine.manager.MouseManager;
import de.gemo.game.jbox2d.JBox2D;

import static org.lwjgl.opengl.GL11.*;

public abstract class Test {
    protected JBox2D jBox;

    protected ArrayList<Body> bodies = new ArrayList<Body>();
    protected ArrayList<Shape> shapes = new ArrayList<Shape>();

    protected Body nearestBody = null;

    public Test(JBox2D jBox) {
        this.jBox = jBox;
    }

    public abstract void init();

    public abstract void update(int delta);

    protected final void addBody(Body body, Shape shape) {
        this.bodies.add(body);
        this.shapes.add(shape);
    }

    public final void destroy() {
        for (Body body : this.bodies) {
            this.jBox.removeBody(body);
        }
        this.bodies.clear();
        this.shapes.clear();
    }

    protected void renderVec2(Vec2 vector) {
        glVertex2f(vector.x, vector.y);
    }

    public abstract int render();

    public final int renderBodies() {
        int bodyCount = 0;

        this.nearestBody = this.getNearestBody(MouseManager.INSTANCE.getMouseVector().getX(), MouseManager.INSTANCE.getMouseVector().getY(), 30);

        glPushMatrix();
        {
            // render bodies
            for (Body body : this.bodies) {
                boolean selected = (body == this.nearestBody);
                glPushMatrix();
                {
                    glTranslatef(body.getPosition().x, body.getPosition().y, 0);
                    glRotated(Math.toDegrees(body.getAngle()), 0, 0, 1);
                    if (selected) {
                        glColor4f(0, 1, 1, 1);
                    } else if (body.isAwake()) {
                        glColor4f(0, 1, 0, 1);
                    } else {
                        glColor4f(1, 0, 0f, 1);
                    }

                    Integer type = (Integer) body.getUserData();
                    // box
                    if (type == 0) {
                        PolygonShape shape = (PolygonShape) shapes.get(bodyCount);
                        glBegin(GL_LINE_LOOP);
                        renderVec2(shape.getVertex(0));
                        renderVec2(shape.getVertex(1));
                        renderVec2(shape.getVertex(2));
                        renderVec2(shape.getVertex(3));
                        glEnd();
                    }

                    // circle
                    if (type == 1) {
                        CircleShape shape = (CircleShape) shapes.get(bodyCount);
                        glBegin(GL_LINE_LOOP);
                        int num_segments = 16;
                        for (int ii = 0; ii < num_segments; ii++) {
                            float theta = 2.0f * 3.1415926f * (float) (ii) / (float) (num_segments);
                            float x = (float) (shape.m_radius * Math.cos(theta));
                            float y = (float) (shape.m_radius * Math.sin(theta));
                            glVertex2f(x, y);
                        }
                        glEnd();
                    }

                }
                glPopMatrix();
                bodyCount++;
            }
        }
        glPopMatrix();
        return bodyCount;
    }

    public abstract boolean handleKeyPress(int key);

    public abstract boolean handleMousePress(int button);

    public abstract void cleanUp();

    public final Body getNearestBody(float x, float y, float maxDistance) {
        Body result = null;
        float minDistance = Float.MAX_VALUE;
        Vec2 position = new Vec2(x, y);
        for (Body body : this.bodies) {
            float distance = Math.abs(MathUtils.distance(position, body.getPosition()));
            if (distance <= maxDistance && distance < minDistance) {
                result = body;
                minDistance = distance;
            }
        }
        return result;
    }
}
