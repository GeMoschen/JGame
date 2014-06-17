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
    // private Vector3f location;
    private float currentAngle = 0f, wantedAngle = 0f, momentum = 0f, viewAngle = 0;

    public Vector3f velocity = new Vector3f();
    private Hitbox hitbox;
    private boolean alerted = false;

    private Path path = null;
    private int waypointIndex = 0;
    private Vector3f currentWaypoint = null;

    public Enemy(Vector3f location) {
        this.momentum = 1;
        this.createHitbox(location);
        this.viewAngle = (float) (-Math.random() * 60 + Math.random() * 60);
        if (this.viewAngle > 45) {
            this.viewAngle = 45f;
        }
        if (this.viewAngle < -45) {
            this.viewAngle = -45;
        }
    }

    private void createHitbox(Vector3f location) {
        this.hitbox = new Hitbox(location);
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
        this.updateViewAngle();
        this.hitbox.setAngle(angle + this.viewAngle);
        this.currentAngle = this.hitbox.getAngle() - this.viewAngle;
    }

    private void updateViewAngle() {
        float moveSpeed = 0.25f;
        if (this.momentum == 1) {
            this.viewAngle += moveSpeed;
        } else {
            this.viewAngle -= moveSpeed;
        }

        if (this.viewAngle >= 60) {
            this.momentum = 0;
        } else if (this.viewAngle <= -60) {
            this.momentum = 1;
        }
    }

    float minX = Float.MAX_VALUE;

    public void update(NavMesh navMesh, List<Tile> tileList) {
        this.velocity = new Vector3f(0, 0, 0);

        if (this.currentWaypoint != null) {
            this.setAngle(this.currentWaypoint.getAngle(this.hitbox.getCenter()));

            // this.setAngle(0);

            // float factor = 1.002f;
            // float invFactor = 1 / factor;
            // if (this.momentum > 0) {
            // if (this.viewAngle < 0) {
            // this.hitbox.scale(factor, factor);
            // } else if (this.viewAngle > 0) {
            // this.hitbox.scale(invFactor, invFactor);
            // }
            // } else {
            // if (this.viewAngle > 0) {
            // this.hitbox.scale(factor, factor);
            // } else if (this.viewAngle < 0) {
            // this.hitbox.scale(invFactor, invFactor);
            // }
            // }

            float maxVelocity = 0.02f;
            float maxForce = 0.3f;
            float mass = 1f;
            Vector3f desired = new Vector3f();
            Vector3f.sub(currentWaypoint, this.hitbox.getCenter(), desired);
            if (desired.getX() != 0 && desired.getY() != 0) {
                Vector3f.normalize(desired).scale(maxVelocity);
            }

            float dimX = this.hitbox.getAABB().getRight() - this.hitbox.getAABB().getLeft();
            if (dimX < this.minX) {
                System.out.println("min: " + minX);
                this.minX = dimX;
            }

            Vector3f steering = Vector3f.sub(desired, this.velocity);
            steering.truncate(maxForce);

            steering = (Vector3f) steering.scale(1f / mass);

            this.velocity = Vector3f.add(velocity, steering);
            this.move(this.velocity);

            if (this.isNearTarget(1.5f)) {
                this.waypointIndex++;
                if (this.path != null) {
                    if (this.waypointIndex == this.path.getPath().size()) {
                        this.findRandomGoal(navMesh, tileList);
                    } else {
                        this.currentWaypoint = this.path.getNode(this.waypointIndex);
                        this.wantedAngle = this.currentWaypoint.getAngle(this.hitbox.getCenter());
                        this.viewAngle -= 0;
                    }
                }
            }
        } else {
            this.findRandomGoal(navMesh, tileList);
        }
    }

    public void findRandomGoal(NavMesh navMesh, List<Tile> tileList) {
        Vector3f goal;
        boolean canSeeTarget = false;
        int tries = 0;
        this.waypointIndex = 0;
        this.path = null;
        this.currentWaypoint = null;
        while (!canSeeTarget && tries < 100) {
            goal = new Vector3f((float) Math.random() * GameEngine.INSTANCE.VIEW_WIDTH, (float) Math.random() * GameEngine.INSTANCE.VIEW_HEIGHT, 0);

            // create raycast
            Hitbox raycast = new Hitbox(0, 0);
            raycast.addPoint(this.hitbox.getCenter());
            raycast.addPoint(Vector3f.add(this.hitbox.getCenter(), new Vector3f(1, 1, 0)));

            // check for colliding polys
            canSeeTarget = true;
            for (Tile block : tileList) {
                Hitbox expanded = block.getHitbox().clone();
                expanded.scaleByPixel(20);
                if (CollisionHelper.isVectorInHitbox(goal, expanded)) {
                    canSeeTarget = false;
                    break;
                }
            }
            if (canSeeTarget) {
                this.path = navMesh.findPath(this.hitbox.getCenter(), goal, tileList);
                if (this.path != null) {
                    this.waypointIndex++;
                    this.currentWaypoint = this.path.getNode(this.waypointIndex);
                    this.wantedAngle = this.currentWaypoint.getAngle(this.hitbox.getCenter());
                    return;
                }
            }
            tries++;
        }
    }

    public void setTarget(Vector3f goal, NavMesh navMesh, List<Tile> tileList) {
        // create raycast
        Hitbox raycast = new Hitbox(0, 0);
        raycast.addPoint(this.hitbox.getCenter());
        raycast.addPoint(Vector3f.add(this.hitbox.getCenter(), new Vector3f(1, 1, 0)));
        boolean canSeeTarget = true;
        this.waypointIndex = 0;
        this.path = null;
        this.currentWaypoint = null;
        // check for colliding polys
        for (Tile block : tileList) {
            if (CollisionHelper.isVectorInHitbox(goal, block.expanded)) {
                canSeeTarget = false;
                break;
            }
        }
        if (canSeeTarget) {
            this.path = navMesh.findPath(this.hitbox.getCenter(), goal, tileList);
            if (this.path != null) {
                this.waypointIndex++;
                this.currentWaypoint = this.path.getNode(this.waypointIndex);
            }
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
                Vector3f lightToCurrent = Vector3f.sub(currentVertex, this.hitbox.getCenter());
                if (Vector3f.dot(normal, lightToCurrent) > 0) {
                    Vector3f point1 = Vector3f.add(currentVertex, Vector3f.sub(currentVertex, this.hitbox.getCenter(), null).scale(width), null);
                    Vector3f point2 = Vector3f.add(nextVertex, Vector3f.sub(nextVertex, this.hitbox.getCenter(), null).scale(width), null);
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

        glUniform2f(glGetUniformLocation(coneShader.getID(), "lightLocation"), this.hitbox.getCenter().getX(), height - this.hitbox.getCenter().getY());
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
            glTranslatef(this.hitbox.getCenter().getX(), this.hitbox.getCenter().getY(), this.hitbox.getCenter().getZ());
            glRotatef(this.getHitbox().getAngle(), 0, 0, 1);
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
        this.hitbox.render();

        glPushMatrix();
        {
            glColor4f(1, 1, 1, 1);
            glTranslatef(this.hitbox.getCenter().getX(), this.hitbox.getCenter().getY(), this.hitbox.getCenter().getZ());
            glBegin(GL_QUADS);
            {
                int block = 3;
                glVertex2f(-block, -block);
                glVertex2f(+block, -block);
                glVertex2f(+block, +block);
                glVertex2f(-block, +block);
            }
            glEnd();
        }
        glPopMatrix();

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

    private void move(Vector3f velocity2) {
        this.hitbox.move(this.velocity.getX() * 2, this.velocity.getY() * 2);
    }

    private boolean isNearTarget(float radius) {
        return this.hitbox.getCenter().distanceTo(this.currentWaypoint) < radius;
    }

    public void setAlerted(boolean alerted) {
        this.alerted = alerted;
    }

    public boolean collides(Enemy other) {
        return CollisionHelper.isVectorInHitbox(other.getLocation(), this.hitbox);
    }

    public Vector3f getLocation() {
        return this.hitbox.getCenter();
    }
}
