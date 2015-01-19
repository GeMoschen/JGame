package de.gemo.game.fov.units;

import java.util.*;

import org.lwjgl.util.vector.Vector2f;

import de.gemo.game.fov.core.*;
import de.gemo.game.fov.navigation.*;
import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.units.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class Enemy {
    // private Vector3f location;
    private float currentAngle = 0f, wantedAngle = 0f, momentum = 0f, viewAngle = 0;
    private Enemy currentInView = null;
    private float height = 20;

    public Vector3f velocity = new Vector3f();
    private Hitbox farHitbox, nearHitbox;
    private boolean alerted = false;

    private float minX = Float.MAX_VALUE;

    private Path path = null;
    private int waypointIndex = 0;
    private Vector3f currentWaypoint = null;
    private PatrolState patrolState = PatrolState.NORMAL;

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
        this.farHitbox = new Hitbox(location);
        this.nearHitbox = new Hitbox(location.clone());

        int nearDistance = 100;
        int farDistance = 250;
        int points = 9;
        float maxAngle = 42f;
        float stepAngle = maxAngle / (points - 1);
        float halfAngle = maxAngle / 2f;

        this.nearHitbox.rotate(-halfAngle);
        this.farHitbox.rotate(-halfAngle);

        this.nearHitbox.addPoint(0, 0);
        this.nearHitbox.addPoint(0, -nearDistance);
        this.farHitbox.addPoint(0, -farDistance);

        // construct near hitbox
        for (int i = 0; i < points - 1; i++) {
            this.nearHitbox.rotate(stepAngle);
            this.nearHitbox.addPoint(0, -nearDistance);
        }

        // construct far hitbox
        for (int i = 0; i < points - 1; i++) {
            this.farHitbox.rotate(stepAngle);
            this.farHitbox.addPoint(0, -farDistance);
        }

        this.farHitbox.setAngle(0);
        this.farHitbox.rotate(halfAngle + stepAngle);
        for (int i = 0; i < points - 1; i++) {
            this.farHitbox.rotate(-stepAngle);
            this.farHitbox.addPoint(0, -nearDistance);
        }
        this.farHitbox.rotate(-stepAngle);
        this.farHitbox.addPoint(0, -nearDistance);
    }

    public void setAngle(float angle) {
        this.updateViewAngle();
        this.farHitbox.setAngle(angle + this.viewAngle);
        this.nearHitbox.setAngle(angle + this.viewAngle);
        this.currentAngle = this.farHitbox.getAngle() - this.viewAngle;
    }

    private void updateViewAngle() {
        float rotationSpeed = 0.5f;
        if (this.momentum == 1) {
            this.viewAngle += rotationSpeed;
        } else {
            this.viewAngle -= rotationSpeed;
        }

        int maxAngle = 30;
        if (this.viewAngle >= maxAngle) {
            this.momentum = 0;
        } else if (this.viewAngle <= -maxAngle) {
            this.momentum = 1;
        }
    }

    private void canSeeAnyone(List<Enemy> enemies, NavMesh navMesh, List<Tile> tileList) {
        boolean near, far;
        ArrayList<Vector3f> intersections;
        Enemy target = null;
        for (Enemy enemy : enemies) {
            if (enemy != this) {
                if ((near = CollisionHelper.isVectorInHitbox(enemy.getLocation(), this.nearHitbox)) || (far = CollisionHelper.isVectorInHitbox(enemy.getLocation(), this.farHitbox))) {
                    // create raycast
                    Hitbox raycast = new Hitbox(0, 0);
                    raycast.addPoint(this.getLocation());
                    raycast.addPoint(enemy.getLocation());

                    // check for colliding polys
                    boolean canSeeTarget = true;
                    for (Tile tile : tileList) {
                        intersections = CollisionHelper.findIntersection(raycast, tile.getHitbox());
                        if (intersections != null && enemy.getHeight() <= tile.getHitbox().getHeight()) {
                            canSeeTarget = false;
                        }
                    }
                    if (canSeeTarget) {
                        target = enemy;
                    }
                }
            }
        }

        if (target != null) {
            this.setAlerted(true);
            this.farHitbox.setAngle(target.getLocation().getAngle(this.getLocation()));
            this.nearHitbox.setAngle(target.getLocation().getAngle(this.getLocation()));
            this.currentAngle = this.farHitbox.getAngle();
            this.currentInView = target;
            this.patrolState = PatrolState.ALERTED;
        } else {
            if (this.alerted) {
                this.setTarget(this.currentInView.getLocation(), navMesh, tileList);
                this.patrolState = PatrolState.SEEKING;
            }

            if (this.currentInView == null) {
                this.patrolState = PatrolState.NORMAL;
            }
            this.currentInView = null;
            this.setAlerted(false);
        }
    }

    public void update(List<Enemy> enemies, NavMesh navMesh, List<Tile> tileList) {
        this.velocity = new Vector3f(0, 0, 0);

        if (this.currentWaypoint != null) {

            if (this.alerted) {
                this.canSeeAnyone(enemies, navMesh, tileList);
                return;
            }

            this.setAngle(this.currentWaypoint.getAngle(this.farHitbox.getCenter()));

            // this.setAngle(0);
            //
            // double factor = 1.002d;
            // double invFactor = 1d / factor;
            // if (this.momentum > 0) {
            // if (this.viewAngle < 0) {
            // this.nearHitbox.scale((float) factor, (float) factor);
            // this.farHitbox.scale((float) factor, (float) factor);
            // } else if (this.viewAngle > 0) {
            // this.nearHitbox.scale((float) invFactor, (float) invFactor);
            // this.farHitbox.scale((float) invFactor, (float) invFactor);
            // }
            // } else {
            // if (this.viewAngle > 0) {
            // this.nearHitbox.scale((float) factor, (float) factor);
            // this.farHitbox.scale((float) factor, (float) factor);
            // } else if (this.viewAngle < 0) {
            // this.nearHitbox.scale((float) invFactor, (float) invFactor);
            // this.farHitbox.scale((float) invFactor, (float) invFactor);
            // }
            // }

            float maxVelocity = 0.02f;
            float maxForce = 0.3f;
            float mass = 1f;

            if (currentInView != null) {
                maxForce = 0.6f;
            }

            Vector3f desired = new Vector3f();
            Vector3f.sub(currentWaypoint, this.farHitbox.getCenter(), desired);
            if (desired.getX() != 0 && desired.getY() != 0) {
                Vector3f.normalize(desired).scale(maxVelocity);
            }

            float dimX = this.farHitbox.getAABB().getRight() - this.farHitbox.getAABB().getLeft();
            if (dimX < this.minX) {
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
                        this.patrolState = PatrolState.NORMAL;
                        this.findRandomGoal(navMesh, tileList);
                    } else {
                        this.currentWaypoint = this.path.getNode(this.waypointIndex);
                        this.wantedAngle = this.currentWaypoint.getAngle(this.farHitbox.getCenter());
                        this.viewAngle -= 0;
                    }
                }
            }
        } else {
            this.patrolState = PatrolState.NORMAL;
            this.findRandomGoal(navMesh, tileList);
        }

        this.canSeeAnyone(enemies, navMesh, tileList);
    }

    public void findRandomGoal(NavMesh navMesh, List<Tile> tileList) {
        Vector3f goal;
        boolean canSeeTarget = false;
        int tries = 0;
        this.waypointIndex = 0;
        this.path = null;
        this.currentWaypoint = null;
        while (!canSeeTarget && tries < 100) {
            goal = new Vector3f((float) Math.random() * GameEngine.$.VIEW_WIDTH, (float) Math.random() * GameEngine.$.VIEW_HEIGHT, 0);

            // create raycast
            Hitbox raycast = new Hitbox(0, 0);
            raycast.addPoint(this.farHitbox.getCenter());
            raycast.addPoint(Vector3f.add(this.farHitbox.getCenter(), new Vector3f(1, 1, 0)));

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
                this.path = navMesh.findPath(this.farHitbox.getCenter(), goal, tileList);
                if (this.path != null) {
                    this.waypointIndex++;
                    this.currentWaypoint = this.path.getNode(this.waypointIndex);
                    this.wantedAngle = this.currentWaypoint.getAngle(this.farHitbox.getCenter());
                    return;
                }
            }
            tries++;
        }
    }

    public void setTarget(Vector3f goal, NavMesh navMesh, List<Tile> tileList) {
        // create raycast
        Hitbox raycast = new Hitbox(0, 0);
        raycast.addPoint(this.farHitbox.getCenter());
        raycast.addPoint(Vector3f.add(this.farHitbox.getCenter(), new Vector3f(1, 1, 0)));
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
            this.path = navMesh.findPath(this.farHitbox.getCenter(), goal, tileList);
            if (this.path != null) {
                this.waypointIndex++;
                this.currentWaypoint = this.path.getNode(this.waypointIndex);
            }
        }
    }

    public void render(List<Tile> blocks, int width, int height) {
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
                Vector3f lightToCurrent = Vector3f.sub(currentVertex, this.farHitbox.getCenter());
                if (Vector3f.dot(normal, lightToCurrent) > 0) {
                    Vector3f point1 = Vector3f.add(currentVertex, Vector3f.sub(currentVertex, this.farHitbox.getCenter(), null).scale(width), null);
                    Vector3f point2 = Vector3f.add(nextVertex, Vector3f.sub(nextVertex, this.farHitbox.getCenter(), null).scale(width), null);
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

        // enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        // render viewcones
        glPushMatrix();
        {
            this.renderHitbox(this.farHitbox);
            this.renderHitbox(this.nearHitbox);
        }
        glPopMatrix();

        // disable blending and stencil
        glDisable(GL_BLEND);
        glClear(GL_STENCIL_BUFFER_BIT);
        glDisable(GL_STENCIL_TEST);

        if (this.path != null) {
            this.path.render(this.waypointIndex);
        }
        // this.renderHitbox();

        glPushMatrix();
        {
            this.height = 20;
            glColor4f(1, 1, 1, 1);
            glTranslatef(this.farHitbox.getCenter().getX(), this.farHitbox.getCenter().getZ(), this.farHitbox.getCenter().getY());
            glBegin(GL_QUADS);
            {
                int block = 3;
                glVertex3f(-block, this.height, -block);
                glVertex3f(+block, this.height, -block);
                glVertex3f(+block, this.height, +block);
                glVertex3f(-block, this.height, +block);
            }
            glEnd();

            glBegin(GL_LINES);
            {
                glVertex3f(0, 0, 0);
                glVertex3f(0, this.height, 0);
            }
            glEnd();
        }
        glPopMatrix();

    }

    private void renderHitbox(Hitbox hitbox) {
        // translate to center
        glPushMatrix();
        {
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            // glDisable(GL_LIGHTING);
            // glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            // glLineWidth(1f);

            // render hitbox
            glPushMatrix();
            {
                if (hitbox == this.nearHitbox) {
                    glColor4f(0, .7f, 0, .5f);
                    if (this.alerted) {
                        glColor4f(.7f, 0, 0, .5f);
                    }
                } else {
                    glColor4f(0, .7f, 0, .2f);
                    if (this.alerted) {
                        glColor4f(.7f, 0, 0, .2f);
                    }
                }
                glBegin(GL_POLYGON);
                for (Vector3f vector : hitbox.getPoints()) {
                    glVertex3f(vector.getX(), vector.getZ(), vector.getY());
                }
                glEnd();
            }
            glPopMatrix();

            // translate & render center
            glPushMatrix();
            {
                glEnable(GL_BLEND);
                glEnable(GL_TEXTURE_2D);
                glTranslatef(this.getLocation().getX(), this.height + 0.2f, this.getLocation().getY());
                glRotatef(90, 1, 0, 0);
                FontManager.getStandardFont().drawString(-5, -7, this.patrolState.name());
            }
            glPopMatrix();

            // render AABB
            // this.aabb.render();

            // glEnable(GL_BLEND);
            // glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();

    }

    public Hitbox getHitbox() {
        return farHitbox;
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
        this.farHitbox.move(this.velocity.getX() * 2, this.velocity.getY() * 2);
        this.nearHitbox.move(this.velocity.getX() * 2, this.velocity.getY() * 2);
    }

    private boolean isNearTarget(float radius) {
        return this.farHitbox.getCenter().distanceTo(this.currentWaypoint) < radius;
    }

    public void setAlerted(boolean alerted) {
        this.alerted = alerted;
    }

    public boolean collides(Enemy other) {
        return CollisionHelper.isVectorInHitbox(other.getLocation(), this.farHitbox);
    }

    public Vector3f getLocation() {
        return this.farHitbox.getCenter();
    }

    public float getHeight() {
        return height;
    }
}
