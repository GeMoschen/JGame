package de.gemo.game.fov.units;

import java.util.*;

import org.lwjgl.util.vector.Vector2f;

import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.units.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class LightCone {
    private Vector3f location;
    public float red;
    public float green;
    public float blue;
    public float intensity = 1;
    private float angle = 0f, momentum = 0f;
    public Vector3f target = null;

    public Vector3f velocity = new Vector3f();
    public Hitbox hitbox;
    private boolean alerted = false;

    public LightCone(Vector3f location, float red, float green, float blue) {
        this.location = location;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.momentum = (float) Math.random() * 1.5f;
        if (Math.random() < 0.5) {
            this.momentum = -this.momentum;
        }
        this.createHitbox();
    }

    private void createHitbox() {
        this.hitbox = new Hitbox(this.location);
        this.hitbox.addPoint(0, 0);
        this.hitbox.addPoint(-94, -252);
        this.hitbox.addPoint(-70, -261);
        this.hitbox.addPoint(-47, -265);
        this.hitbox.addPoint(-24, -269);
        this.hitbox.addPoint(0, -270);
        this.hitbox.addPoint(+24, -269);
        this.hitbox.addPoint(+47, -265);
        this.hitbox.addPoint(+70, -261);
        this.hitbox.addPoint(+94, -252);
    }

    public void setAngle(float angle) {
        this.hitbox.setAngle(angle);
        this.angle = this.hitbox.getAngle();
    }

    public void render(List<Tile> blocks, Shader coneShader, Shader ambientShader, int width, int height) {

        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
        glColorMask(false, false, false, false);
        glDepthMask(false);
        glStencilFunc(GL_NEVER, 1, 0xFF);
        glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP); // draw 1s on test fail
                                                   // (always)
        // draw stencil pattern
        glStencilMask(0xFF);
        glClear(GL_STENCIL_BUFFER_BIT); // needs mask=0xFF

        // render ShadowFins
        List<ShadowFin> fins = new ArrayList<ShadowFin>();
        for (Tile block : blocks) {
            List<Vector3f> vertices = block.getHitbox().getPoints();
            for (int i = 0; i < vertices.size(); i++) {
                Vector3f currentVertex = vertices.get(i);
                Vector3f nextVertex = vertices.get((i + 1) % vertices.size());
                Vector3f edge = Vector3f.sub(nextVertex, currentVertex);
                Vector3f normal = new Vector3f(edge.getY(), -edge.getX(), 0);
                Vector3f lightToCurrent = Vector3f.sub(currentVertex, this.location);
                if (Vector3f.dot(normal, lightToCurrent) > 0) {
                    Vector3f point1 = Vector3f.add(currentVertex, Vector3f.sub(currentVertex, this.location, null).scale(width), null);
                    Vector3f point2 = Vector3f.add(nextVertex, Vector3f.sub(nextVertex, this.location, null).scale(width), null);
                    ShadowFin fin = new ShadowFin(currentVertex, point1, point2, nextVertex);
                    fin.render(0, 0, 0, 1f);
                    fins.add(fin);
                }
            }
        }

        // enable stencil
        glColorMask(true, true, true, true);
        glDepthMask(true);
        glStencilMask(0x00);
        // draw where stencil's value is 0
        glStencilFunc(GL_EQUAL, 1, 0xFF);
        /* (nothing to draw) */
        // draw only where stencil's value is 1
        glStencilFunc(GL_EQUAL, 0, 0xFF);

        // bind shader
        coneShader.bind();

        glUniform2f(glGetUniformLocation(coneShader.getID(), "lightLocation"), this.location.getX(), height - this.location.getY());
        if (!alerted) {
            glUniform3f(glGetUniformLocation(coneShader.getID(), "lightColorOne"), 0.1f, 0.4f, 0.0f);
            glUniform3f(glGetUniformLocation(coneShader.getID(), "lightColorTwo"), 0.05f, 0.2f, 0.0f);
        } else {
            glUniform3f(glGetUniformLocation(coneShader.getID(), "lightColorOne"), 0.4f, 0.1f, 0.0f);
            glUniform3f(glGetUniformLocation(coneShader.getID(), "lightColorTwo"), 0.2f, 0.05f, 0.0f);
        }

        // enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        // render cone
        glPushMatrix();
        {
            glTranslatef(this.location.getX(), this.location.getY(), this.location.getZ());
            glRotatef(this.angle, 0, 0, 1);
            glBegin(GL_POLYGON);
            {
                glVertex2f(0, 0);
                glVertex2f(-100, -270);
                glVertex2f(+100, -270);
            }
            glEnd();
        }
        glPopMatrix();

        // disable blending, shader and stencil
        glDisable(GL_BLEND);
        coneShader.unbind();
        glClear(GL_STENCIL_BUFFER_BIT);
        glDisable(GL_STENCIL_TEST);

        // // render ShadowFins
        // List<PolyDefault> shadows = new ArrayList<PolyDefault>();
        // for (Tile block : blocks) {
        // PolyDefault shadowPoly = new PolyDefault();
        // boolean foundShadow = false;
        // Vector2f[] vertices = block.getVertices();
        // for (int i = 0; i < vertices.length; i++) {
        // Vector2f currentVertex = vertices[i];
        // Vector2f nextVertex = vertices[(i + 1) % vertices.length];
        // Vector2f edge = Vector2f.sub(nextVertex, currentVertex, null);
        // Vector2f normal = new Vector2f(edge.getY(), -edge.getX());
        // Vector2f lightToCurrent = Vector2f.sub(currentVertex, this.location,
        // null);
        // if (Vector2f.dot(normal, lightToCurrent) > 0) {
        // PolyDefault finPoly = new PolyDefault();
        // Vector2f point1 = Vector2f.add(currentVertex, (Vector2f)
        // Vector2f.sub(currentVertex, this.location, null).scale(width), null);
        // Vector2f point2 = Vector2f.add(nextVertex, (Vector2f)
        // Vector2f.sub(nextVertex, this.location, null).scale(width), null);
        // finPoly.add(new Point2D(currentVertex.x, currentVertex.y));
        // finPoly.add(new Point2D(point1.x, point1.y));
        // finPoly.add(new Point2D(point2.x, point2.y));
        // finPoly.add(new Point2D(nextVertex.x, nextVertex.y));
        // shadowPoly = (PolyDefault) shadowPoly.union(finPoly);
        // foundShadow = true;
        // }
        // }
        //
        // if (foundShadow) {
        // shadows.add(shadowPoly);
        // }
        // }
        // PolyDefault polygon = new PolyDefault();
        // for (Vector3f vector : this.hitbox.getPoints()) {
        // polygon.add(vector.getX(), vector.getY());
        // }
        //
        // for (PolyDefault shadow : shadows) {
        // if (!polygon.isHole()) {
        // polygon = (PolyDefault) polygon.difference(shadow);
        // }
        // }

        // this.hitbox.render();
        // glBegin(GL_LINE_LOOP);
        // {
        // if (this.alerted) {
        // glColor4f(1, 0, 0, 0.5f);
        // } else {
        // glColor4f(0, 1, 0, 0.5f);
        // }
        // for (int i = 0; i < polygon.getNumPoints(); i++) {
        // glVertex2f((float) polygon.getX(i), (float) polygon.getY(i));
        // }
        // }
        // glEnd();

    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public boolean exists(List<Vector2f> list, Vector2f search) {
        for (Vector2f vector : list) {
            if (vector.x == search.x && vector.y == search.y) {
                return true;
            }
        }
        return false;
    }

    public void insertIfNotExists(List<Vector2f> list, Vector2f vector) {
        if (!exists(list, vector)) {
            list.add(vector);
        }
    }

    public void update() {
        this.setAngle(this.angle + this.momentum);
    }

    public boolean isNearTarget(float radius) {
        float xDist = target.getX() - location.getX();
        float yDist = target.getY() - location.getZ();
        return (float) Math.abs(Math.sqrt(xDist * xDist + yDist * yDist)) <= radius;
    }

    public float getDistance(Vector3f target) {
        float xDist = target.getX() - location.getX();
        float yDist = target.getY() - location.getY();
        return (float) Math.sqrt(xDist * xDist + yDist * yDist);
    }

    public void seek(List<LightCone> list) {
        if (target == null) {
            Vector3f.add(this.location, this.seperate(list), this.location);
            this.hitbox.setCenter(this.location);
            return;
        }

        float maxVelocity = 2f;
        float maxForce = 2f;
        float mass = 1f;
        Vector3f desired = new Vector3f();
        Vector3f.sub(target, location, desired);
        if (desired.getX() != 0 && desired.getY() != 0) {
            Vector3f.normalize(desired).scale(maxVelocity);
        }

        Vector3f steering = Vector3f.sub(desired, this.velocity);
        steering.truncate(maxForce);

        steering = (Vector3f) steering.scale(1f / mass);

        this.velocity = Vector3f.add(velocity, steering);

        // this.velocity = Vector2f.add(velocity, this.seperate(list), null);
        this.velocity.truncate(maxVelocity);

        // move
        Vector3f.add(this.location, velocity, this.location);
        this.hitbox.setCenter(this.location);

        if (this.isNearTarget(2)) {
            this.target = null;
        }
    }

    public void setAlerted(boolean alerted) {
        this.alerted = alerted;
    }

    public Vector3f seperate(List<LightCone> list) {
        Vector3f v = new Vector3f();
        // int neighborCount = 0;
        // for (LightCone agent : list) {
        // if (this == list) {
        // continue;
        // }
        // if (Math.abs(this.getDistance(agent.location)) < 150) {
        // v.x += agent.location.x - this.location.x;
        // v.y += agent.location.x - this.location.x;
        // neighborCount++;
        // }
        // }
        //
        // if (neighborCount == 0) {
        // return v;
        // }
        //
        // v.x /= neighborCount;
        // v.y /= neighborCount;
        //
        // v.x *= -1;
        // v.y *= -1;
        //
        // if (v.x == 0 && v.y == 0) {
        // return v;
        // }
        //
        // v.normalise();
        // this.truncate(v, 1.0f);
        return v;
    }

    public boolean collides(LightCone other) {
        return CollisionHelper.isVectorInHitbox(other.getLocation(), this.hitbox);
    }

    public Vector3f getLocation() {
        return location;
    }
}
