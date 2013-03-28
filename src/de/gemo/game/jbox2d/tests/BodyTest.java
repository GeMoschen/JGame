package de.gemo.game.jbox2d.tests;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.lwjgl.input.Keyboard;

import static org.lwjgl.opengl.GL11.*;

import de.gemo.engine.core.Engine;
import de.gemo.engine.manager.MouseManager;
import de.gemo.game.jbox2d.BodyHelper;
import de.gemo.game.jbox2d.JBox2D;

public class BodyTest extends Test {

    private Body selectedBody = null;

    public BodyTest(JBox2D jBox) {
        super(jBox);
    }

    @Override
    public void init() {
        Body body = BodyHelper.addBox(jBox.getWorld(), 320, Engine.INSTANCE.VIEW_HEIGHT - 40, 2, 20, 0);
        this.addBody(body, body.getFixtureList().getShape());

        body = BodyHelper.addBox(jBox.getWorld(), 320, Engine.INSTANCE.VIEW_HEIGHT - 70, 30, 10, 0);
        this.addBody(body, body.getFixtureList().getShape());

        body = BodyHelper.addBox(jBox.getWorld(), 320 + 20, Engine.INSTANCE.VIEW_HEIGHT - 85, 10, 5, 0);
        this.addBody(body, body.getFixtureList().getShape());

        body = BodyHelper.addSphere(jBox.getWorld(), 300, 200);
        this.addBody(body, body.getFixtureList().getShape());

        body = BodyHelper.addBox(jBox.getWorld(), 320 - 20, Engine.INSTANCE.VIEW_HEIGHT - 95, 10, 15, 0);
        this.addBody(body, body.getFixtureList().getShape());
    }

    @Override
    public void update(int delta) {
    }

    @Override
    public int render() {
        if (this.selectedBody != null) {
            Body body = this.selectedBody;
            glPushMatrix();
            {
                glTranslatef(body.getPosition().x, body.getPosition().y, 0);
                glRotated(Math.toDegrees(body.getAngle()), 0, 0, 1);
                glColor4f(1, 1, 0, 1);

                Integer type = (Integer) body.getUserData();

                // box
                if (type == 0) {
                    PolygonShape shape = (PolygonShape) body.getFixtureList().getShape();
                    glBegin(GL_LINE_LOOP);
                    renderVec2(shape.getVertex(0));
                    renderVec2(shape.getVertex(1));
                    renderVec2(shape.getVertex(2));
                    renderVec2(shape.getVertex(3));
                    glEnd();
                }

                // circle
                if (type == 1) {
                    CircleShape shape = (CircleShape) body.getFixtureList().getShape();
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
            return 1;
        }
        return 0;
    }

    @Override
    public boolean handleKeyPress(int key) {
        if (key == Keyboard.KEY_1) {
            Body body = BodyHelper.addBox(jBox.getWorld(), MouseManager.INSTANCE.getMouseVector().getX(), MouseManager.INSTANCE.getMouseVector().getY());
            this.addBody(body, body.getFixtureList().getShape());
            return true;
        }
        if (key == Keyboard.KEY_2) {
            Body body = BodyHelper.addSphere(jBox.getWorld(), MouseManager.INSTANCE.getMouseVector().getX(), MouseManager.INSTANCE.getMouseVector().getY());
            this.addBody(body, body.getFixtureList().getShape());
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMousePress(int button) {
        if (button == 0) {
            this.selectedBody = this.nearestBody;
            return true;
        }
        if (button == 2) {
            if (this.selectedBody != null) {
                Body body = this.selectedBody;
                Vec2 mouse = new Vec2(MouseManager.INSTANCE.getMouseVector().getX(), MouseManager.INSTANCE.getMouseVector().getY());
                Vec2 force = mouse.sub(body.getPosition()).mul(300000);
                body.applyForce(force, body.getPosition());
                return true;
            }
        }
        return true;
    }

    @Override
    public void cleanUp() {
        for (Body body : this.bodies) {
            if (body.getPosition().y > Engine.INSTANCE.VIEW_HEIGHT + 100) {
                this.jBox.removeBody(body);
            }
        }
    }

}
