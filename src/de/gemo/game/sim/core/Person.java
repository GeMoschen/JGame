package de.gemo.game.sim.core;

import java.awt.*;
import java.util.*;

import de.gemo.game.sim.tiles.*;
import de.gemo.gameengine.units.*;
import de.gemo.pathfinding.*;

import static org.lwjgl.opengl.GL11.*;

public class Person {

    private static PathThread[] pathThreads;
    private static int IDcount = 1;
    private static Random random = new Random();

    static {
        pathThreads = new PathThread[1];
        for (int index = 0; index < pathThreads.length; index++) {
            pathThreads[index] = new PathThread();
            new Thread(pathThreads[index]).start();
        }
    }

    private float angle = 0;
    private Vector2f position, velocity;
    private int tileX = 0, tileY = 0;
    private Vector2f goal = null;
    private Vector2f currentWaypoint = null;
    private float speed = 0;

    private final Level level;

    private int waitTicks = 0;
    private int blocked = 0;

    private int refreshPathTicks = 0;
    private int ID = 0;
    private int threadID = -1;
    private final float mass = 1f;

    private ArrayList<Point> walkPath = new ArrayList<Point>();

    private org.newdawn.slick.Color pathColor;

    public Person(Level level, float x, float y) {
        this.level = level;
        this.position = new Vector2f(x, y);
        this.velocity = new Vector2f();
        this.setPosition(x, y);
        speed = random.nextFloat() * 2.5f + 0.5f;
        this.pathColor = new org.newdawn.slick.Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.28f);
        this.pathColor = new org.newdawn.slick.Color(1, 0, 0, 0.15f);
        this.ID = IDcount++;
        this.threadID = random.nextInt(pathThreads.length);
    }

    public void setPosition(float x, float y) {
        int oldTileX = this.tileX;
        int oldTileY = this.tileY;
        this.position.set(x, y);
        this.tileX = (int) (x / (float) (AbstractTile.TILE_SIZE + 1));
        this.tileY = (int) (y / (float) (AbstractTile.TILE_SIZE + 1));
        if (oldTileX != this.tileX || oldTileY != this.tileY) {
            this.level.modifyTempBlocked(oldTileX, oldTileY, -1);
            this.level.modifyTempBlocked(tileX, tileY, 1);
        }
    }

    public void move(float x, float y) {
        this.setPosition(this.position.getX() + x, this.position.getY() + y);
    }

    public float getAngleToOtherPoint(Vector2f other) {
        double dx = other.getX() - this.position.getX();
        // Minus to correct for coord re-mapping
        double dy = -(other.getY() - this.position.getY());

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at
        // 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return (float) Math.toDegrees(inRads);
    }

    public void update(int delta) {
        // if (this.refreshPathTicks < 1) {
        this.checkPathSearch();
        // }

        this.refreshPathTicks--;
        if (this.refreshPathTicks == 0 && this.goal != null) {
            // this.updatePath();
        }

        if (this.currentWaypoint == null) {
            if (this.waitTicks < 1 || this.blocked > 5) {
                this.updatePath();
                this.blocked = 0;
                this.waitTicks = random.nextInt(30);
            } else {
                this.waitTicks--;
            }
            return;
        }

        if (this.waitTicks > 0) {
            this.waitTicks--;
            return;
        }

        if (delta < 1)
            delta = 1;

        this.speed = 2f;

        this.angle = this.getAngleToOtherPoint(this.currentWaypoint);

        // for (int iterations = 1; iterations <= 1; iterations += 1) {
        // float movedX = this.position.getX() + this.velocity.getX() *
        // iterations;
        // float movedY = this.position.getY() + this.velocity.getY() *
        // iterations;
        // int movedTX = (int) (movedX / (float) (AbstractTile.TILE_SIZE + 1));
        // int movedTY = (int) (movedY / (float) (AbstractTile.TILE_SIZE + 1));
        // if (this.level.getTile(movedTX, movedTY).isBlockingPath()) {
        // this.updatePath();
        // return;
        // }
        // if (this.tileX != movedTX || this.tileY != movedTY) {
        // if (this.blocked > 5) {
        // this.updatePath();
        // this.blocked = 0;
        // this.waitTicks = random.nextInt(30);
        // return;
        // }
        // // at least 2 persons are on the tile
        // if (this.level.getTempBlockedValue(movedTX, movedTY) > 0) {
        // this.waitTicks = random.nextInt(30);
        // this.blocked++;
        // return;
        // }
        // }
        // }

        // move the person
        // this.movement.set(0, 0);
        this.doSeek();
        for (Person person : this.level.getPersons()) {
            if (person == this) {
                continue;
            }
            this.doFlee(person);
        }
        this.velocity = Vector2f.truncate(this.velocity, this.speed);
        this.move(this.velocity.getX(), this.velocity.getY());

        float moveDistance = (float) this.velocity.getLength();
        float distanceToWP = (float) Math.sqrt(Math.pow(this.currentWaypoint.getX() - this.position.getX(), 2) + Math.pow(this.currentWaypoint.getY() - this.position.getY(), 2));
        if (distanceToWP <= AbstractTile.QUARTER_TILE_SIZE) {
            this.walkPath.remove(0);
            if (this.walkPath.size() > 0) {
                this.currentWaypoint = new Vector2f((float) this.walkPath.get(0).getX(), (float) this.walkPath.get(0).getY());
                this.angle = this.getAngleToOtherPoint(this.currentWaypoint);
                return;
            } else {
                this.currentWaypoint = null;
                this.velocity.set(0, 0);
                // for testpurposes: find a new random target
                this.findRandomTarget();
            }
        }
    }

    public void findRandomTarget() {
        int x = random.nextInt(this.level.getDimX());
        int y = random.nextInt(this.level.getDimY());
        while (this.level.getTile(x, y).isBlockingPath()) {
            x = random.nextInt(this.level.getDimX());
            y = random.nextInt(this.level.getDimY());
        }
        this.setTarget(new Point(x, y));
    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);

            glTranslatef(this.position.getX(), this.position.getY(), 0);
            glRotatef(this.angle, 0, 0, 1);

            int size = 2;
            glColor3f(1, 0, 0);
            this.drawCircle(this.position, 5, 8);

            // viewLine
            glColor3f(1, 1, 1);
            glBegin(GL_LINES);
            {
                glVertex2i(0, 0);
                glVertex2i(size + 3, 0);
            }
            glEnd();
        }
        glPopMatrix();

        this.renderWalkPath();
    }

    private void drawCircle(Vector2f center, float r, int num_segments) {
        glBegin(GL_LINE_LOOP);
        for (int ii = 0; ii < num_segments; ii++) {
            float theta = (float) ((float) 2.0f * Math.PI * (float) ii / (float) num_segments);

            float x = (float) (r * Math.cos(theta));// calculate the x component
            float y = (float) (r * Math.sin(theta));// calculate the y component

            glVertex2f(x, y);
        }
        glEnd();
    }

    private void renderWalkPath() {
        // for (int i = 0; i < this.walkPath.size(); i++) {
        // glPushMatrix();
        // {
        // Point node = this.walkPath.get(i);
        // glLineWidth(1);
        // glDisable(GL_LIGHTING);
        // glEnable(GL_BLEND);
        // glColor4f(0, 1, 1, 1f);
        // glBegin(GL_LINE_LOOP);
        // {
        // glVertex2f(node.x - 1, node.y - 1);
        // glVertex2f(node.x + 1, node.y - 1);
        // glVertex2f(node.x + 1, node.y + 1);
        // glVertex2f(node.x - 1, node.y + 1);
        // }
        // glEnd();
        // }
        // glPopMatrix();
        // }

        if (this.walkPath.size() > 0) {
            Point node = new Point((int) this.position.getX(), (int) this.position.getY());
            Point next = this.walkPath.get(0);
            glLineWidth(1);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);
            this.pathColor.bind();
            glBegin(GL_LINES);
            {
                glVertex2f(node.x, node.y);
                glVertex2f(next.x, next.y);
            }
            glEnd();
        }
        for (int i = 0; i < this.walkPath.size() - 1; i++) {
            glPushMatrix();
            {
                Point node = this.walkPath.get(i);
                Point next = this.walkPath.get(i + 1);
                glLineWidth(1);
                glDisable(GL_LIGHTING);
                glEnable(GL_BLEND);
                this.pathColor.bind();
                glBegin(GL_LINES);
                {
                    glVertex2f(node.x, node.y);
                    glVertex2f(next.x, next.y);
                }
                glEnd();
            }
            glPopMatrix();
        }
    }

    public void setTarget(Point goal) {
        this.goal = new Vector2f(goal.x, goal.y);
        this.refreshPathTicks = random.nextInt(60);
    }

    public void updatePath() {
        if (this.goal == null) {
            return;
        }

        Vector2f start = new Vector2f(this.tileX, this.tileY);
        AbstractTile startTile = this.level.getTile(this.tileX, this.tileY);
        AbstractTile goalTile = this.level.getTile((int) goal.getX(), (int) goal.getY());
        if (startTile != null && goalTile != null && !startTile.isBlockingPath() && !goalTile.isBlockingPath() && start != goal) {
            SinglePathSearch runnable = new SinglePathSearch(level.createAreaMap(), new Point((int) start.getX(), (int) start.getY()), new Point((int) goal.getX(), (int) goal.getY()), null);
            pathThreads[this.threadID].queue(this.ID, runnable);
            this.refreshPathTicks = random.nextInt(120);
        }
    }

    private void checkPathSearch() {
        SinglePathSearch runnable = pathThreads[this.threadID].poll(this.ID);
        if (runnable == null) {
            return;
        }

        if (runnable.isSearchDone()) {
            this.walkPath = runnable.getWalkPath();
            this.refreshPathTicks = random.nextInt(30);
            if (runnable.isPathFound()) {
                this.velocity.set(0, 0);
                this.currentWaypoint = new Vector2f((float) this.walkPath.get(0).getX(), (float) this.walkPath.get(0).getY());
                this.angle = this.getAngleToOtherPoint(this.currentWaypoint);
            } else {
                this.velocity.set(0, 0);
                this.currentWaypoint = null;
            }
        }
    }

    private Vector2f flee(Vector2f target, float maxVelocity) {
        if (target == null) {
            return new Vector2f(0, 0);
        }
        Vector2f desiredVelocity = Vector2f.normalize(Vector2f.sub(this.position, target)).scale(maxVelocity);
        Vector2f steering = Vector2f.sub(desiredVelocity, this.velocity);
        return steering;
    }

    private void doFlee(Person person) {
        float maxVelocity = this.speed / 4f;
        Vector2f steering = Vector2f.truncate(this.flee(person.position, (this.speed / 50f)), this.speed);
        steering = Vector2f.truncate(Vector2f.add(steering, this.velocity), maxVelocity);
        this.velocity.move(steering.getX(), steering.getY());
    }

    private Vector2f seek(Vector2f target, float maxVelocity) {
        if (target == null) {
            return new Vector2f(0, 0);
        }
        Vector2f desiredVelocity = Vector2f.normalize(Vector2f.sub(target, this.position)).scale(maxVelocity);
        Vector2f steering = Vector2f.sub(desiredVelocity, this.velocity);
        return steering;
    }

    private void doSeek() {
        if (this.currentWaypoint == null) {
            return;
        }
        float maxVelocity = this.speed;
        Vector2f steering = Vector2f.truncate(this.seek(this.currentWaypoint, this.speed / 8f), this.speed);
        steering = Vector2f.truncate(Vector2f.add(steering, this.velocity), maxVelocity);
        this.velocity.move(steering.getX(), steering.getY());
    }
}
