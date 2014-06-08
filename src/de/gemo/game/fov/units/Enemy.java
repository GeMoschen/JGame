package de.gemo.game.fov.units;

import java.util.*;

import org.lwjgl.util.vector.Vector2f;

import de.gemo.game.fov.navigation.*;
import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.units.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Enemy {
    private Vector3f location;
    private float angle = 0f, momentum = 0f;

    public Vector3f velocity = new Vector3f();
    public Hitbox hitbox;
    private boolean alerted = false;

    private Path path = null;
    private int waypointIndex = 0;
    private Vector3f currentWaypoint = null;

    public Enemy(Vector3f location) {
        this.location = location;
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

    public void findRandomGoal(NavMesh navMesh, List<Tile> tileList) {
        Vector3f goal = new Vector3f((float) Math.random() * GameEngine.INSTANCE.VIEW_WIDTH, (float) Math.random() * GameEngine.INSTANCE.VIEW_HEIGHT, 0);
        boolean canSeeTarget = false;
        int tries = 0;
        this.waypointIndex = 0;
        this.path = null;
        this.currentWaypoint = null;
        while (!canSeeTarget && tries < 300) {
            // create raycast
            Hitbox raycast = new Hitbox(0, 0);
            raycast.addPoint(this.location);
            raycast.addPoint(Vector3f.add(this.location, new Vector3f(1, 1, 0)));

            // check for colliding polys
            canSeeTarget = true;
            for (Tile block : tileList) {
                if (CollisionHelper.findIntersection(block.expanded, raycast) != null) {
                    canSeeTarget = false;
                    break;
                }
            }
            if (canSeeTarget) {
                this.path = navMesh.findPath(this.location, goal, tileList);
                if (this.path != null) {
                    this.waypointIndex++;
                    this.currentWaypoint = this.path.getNode(this.waypointIndex);
                    return;
                }
            }
            tries++;
        }
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

        if (this.path != null) {
            this.path.render(this.waypointIndex);
        }

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

    public void update(NavMesh navMesh, List<Tile> tileList) {
        if (this.currentWaypoint != null) {

            this.velocity = new Vector3f(0, 0, 0);
            this.setAngle(this.currentWaypoint.getAngle(this.location));

            float maxVelocity = 0.02f;
            float maxForce = 1.5f;
            float mass = 1f;
            Vector3f desired = new Vector3f();
            Vector3f.sub(currentWaypoint, location, desired);
            if (desired.getX() != 0 && desired.getY() != 0) {
                Vector3f.normalize(desired).scale(maxVelocity);
            }

            Vector3f steering = Vector3f.sub(desired, this.velocity);
            steering.truncate(maxForce);

            steering = (Vector3f) steering.scale(1f / mass);

            this.velocity = Vector3f.add(velocity, steering);
            Vector3f.add(this.location, this.velocity, this.location);

            if (this.isNearTarget(2)) {
                this.waypointIndex++;
                if (this.path != null) {
                    if (this.waypointIndex == this.path.getPath().size()) {
                        this.findRandomGoal(navMesh, tileList);
                    } else {
                        this.currentWaypoint = this.path.getNode(this.waypointIndex);
                    }
                }
            }
        } else {
            this.findRandomGoal(navMesh, tileList);
        }
    }

    private boolean isNearTarget(float radius) {
        return this.location.distanceTo(this.currentWaypoint) < radius;
    }

    public float getDistance(Vector3f target) {
        float xDist = target.getX() - location.getX();
        float yDist = target.getY() - location.getY();
        return (float) Math.sqrt(xDist * xDist + yDist * yDist);
    }

    // public void seek(List<Enemy> list) {
    // if (currentWaypoint == null) {
    // Vector3f.add(this.location, this.seperate(list), this.location);
    // this.hitbox.setCenter(this.location);
    // return;
    // }
    //
    // float maxVelocity = 2f;
    // float maxForce = 2f;
    // float mass = 1f;
    // Vector3f desired = new Vector3f();
    // Vector3f.sub(currentWaypoint, location, desired);
    // if (desired.getX() != 0 && desired.getY() != 0) {
    // Vector3f.normalize(desired).scale(maxVelocity);
    // }
    //
    // Vector3f steering = Vector3f.sub(desired, this.velocity);
    // steering.truncate(maxForce);
    //
    // steering = (Vector3f) steering.scale(1f / mass);
    //
    // this.velocity = Vector3f.add(velocity, steering);
    //
    // // this.velocity = Vector2f.add(velocity, this.seperate(list), null);
    // this.velocity.truncate(maxVelocity);
    //
    // // move
    // Vector3f.add(this.location, velocity, this.location);
    // this.hitbox.setCenter(this.location);
    //
    // if (this.isNearTarget(5)) {
    // this.currentWaypoint = null;
    // }
    // }

    public void setAlerted(boolean alerted) {
        this.alerted = alerted;
    }

    public boolean collides(Enemy other) {
        return CollisionHelper.isVectorInHitbox(other.getLocation(), this.hitbox);
    }

    public Vector3f getLocation() {
        return location;
    }
}
