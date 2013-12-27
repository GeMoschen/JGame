package de.gemo.game.sim.core;

import java.awt.*;
import java.util.*;

import de.gemo.game.sim.tiles.*;
import de.gemo.pathfinding.*;

import static org.lwjgl.opengl.GL11.*;

public class Person {

    private static Random random = new Random();

    private float angle = 0;
    private float x = 0, y = 0;
    private int tileX = 0, tileY = 0;
    private Point goal = null;
    private Point currentWaypoint = null;
    private float speed = 0;

    private final Level level;

    private int waitTicks = 0;
    private int blocked = 0;

    private int refreshPathTicks = 0;

    private ArrayList<Point> walkPath = new ArrayList<Point>();

    private org.newdawn.slick.Color pathColor;

    public Person(Level level, float x, float y) {
        this.level = level;
        this.setPosition(x, y);
        speed = random.nextFloat() * 1f + 0.5f;
        this.pathColor = new org.newdawn.slick.Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.08f);
    }

    public float getAngle(Point other) {
        double dx = other.getX() - x;
        // Minus to correct for coord re-mapping
        double dy = -(other.getY() - y);

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at
        // 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return (float) Math.toDegrees(inRads);
    }

    public void setPosition(float x, float y) {
        int oldTileX = this.tileX;
        int oldTileY = this.tileY;
        this.x = x;
        this.y = y;
        this.tileX = (int) (x / (float) (AbstractTile.TILE_SIZE + 1));
        this.tileY = (int) (y / (float) (AbstractTile.TILE_SIZE + 1));
        if (oldTileX != this.tileX || oldTileY != this.tileY) {
            this.level.modifyTempBlocked(oldTileX, oldTileY, -1);
            this.level.modifyTempBlocked(tileX, tileY, 1);
        }
    }

    public void move(float x, float y) {
        this.setPosition(this.x + x, this.y + y);
    }

    public void update(int delta) {
        this.refreshPathTicks--;
        if (this.refreshPathTicks < 1) {
            this.updatePath();
        }

        if (this.currentWaypoint == null) {
            if (this.waitTicks < 5 || this.blocked > 0) {
                this.updatePath();
                this.blocked = 0;
                this.waitTicks = random.nextInt(10);
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

        this.angle = this.getAngle(this.currentWaypoint);

        float mX = (float) (Math.sin(Math.toRadians(this.angle + 90)) * speed);
        float mY = (float) (-Math.cos(Math.toRadians(this.angle + 90)) * speed);

        for (int iterations = 1; iterations <= 1; iterations += 1) {
            float movedX = this.x + mX * iterations;
            float movedY = this.y + mY * iterations;
            int movedTX = (int) (movedX / (float) (AbstractTile.TILE_SIZE + 1));
            int movedTY = (int) (movedY / (float) (AbstractTile.TILE_SIZE + 1));
            if (this.level.getTile(movedTX, movedTY).isBlockingPath()) {
                this.updatePath();
                return;
            }
            if (this.tileX != movedTX || this.tileY != movedTY) {
                if (this.blocked > 2) {
                    this.updatePath();
                    this.blocked = 0;
                    this.waitTicks = random.nextInt(10);
                    return;
                }
                // at least 2 persons are on the tile
                if (this.level.getTempBlockedValue(movedTX, movedTY) > 0) {
                    this.waitTicks = random.nextInt(10);
                    this.blocked++;
                    return;
                }
            }
        }

        // move the person
        this.move(mX, mY);

        float moveDistance = (float) Math.sqrt(Math.pow(mX, 2) + Math.pow(mY, 2));
        float distanceToWP = (float) Math.sqrt(Math.pow(this.currentWaypoint.x - x, 2) + Math.pow(this.currentWaypoint.y - y, 2));
        if (distanceToWP <= AbstractTile.TENTH_TILE_SIZE || distanceToWP <= moveDistance * 1.2f || goal.x == tileX && goal.y == tileY) {
            this.walkPath.remove(0);
            if (this.walkPath.size() > 0) {
                this.currentWaypoint = this.walkPath.get(0);
                this.angle = this.getAngle(this.currentWaypoint);
                return;
            } else {
                this.currentWaypoint = null;
                // for testpurposes: find a new random target
                this.findRandomTarget();
            }
        }
    }

    public void findRandomTarget() {
        int x = random.nextInt(this.level.getDimX());
        int y = random.nextInt(this.level.getDimY());
        do {
            x = random.nextInt(this.level.getDimX());
            y = random.nextInt(this.level.getDimY());
            while (this.level.getTile(x, y).isBlockingPath()) {
                x = random.nextInt(this.level.getDimX());
                y = random.nextInt(this.level.getDimY());
            }
        } while (!this.setTarget(new Point(x, y)));
    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);

            glTranslatef(x, y, 0);
            glRotatef(this.angle, 0, 0, 1);

            int size = 2;
            glColor3f(1, 0, 0);
            // if (this.waitTicks > 0) {
            // glColor4f(1f, 1f, 1f, 1f / this.blocked);
            // }
            glBegin(GL_QUADS);
            {
                glVertex2i(-size, -size);
                glVertex2i(+size, -size);
                glVertex2i(+size, +size);
                glVertex2i(-size, +size);
            }
            glEnd();

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

        // render walkpath
        this.renderWalkPath();

        // this.renderSingle(tileX, tileY, 1, 1, 0, 0.2f);
        // if (this.goal != null) {
        // this.renderSingle(goal.x, goal.y, 0, 1, 0, 0.2f);
        // }
    }

    private void renderSingle(int x, int y, float r, float g, float b, float a) {
        glPushMatrix();
        {
            glTranslatef(x * AbstractTile.TILE_SIZE + x, y * AbstractTile.TILE_SIZE + y, 0);
            glLineWidth(1);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);
            glColor4f(r, g, b, a);
            glBegin(GL_LINE_LOOP);
            {
                glVertex2i(0, 0);
                glVertex2i(AbstractTile.TILE_SIZE, 0);
                glVertex2i(AbstractTile.TILE_SIZE, AbstractTile.TILE_SIZE);
                glVertex2i(0, AbstractTile.TILE_SIZE);
            }
            glEnd();
        }
        glPopMatrix();
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
            Point node = new Point((int) x, (int) y);
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

    public boolean setTarget(Point goal) {
        this.goal = goal;
        if (this.updatePath()) {
            this.currentWaypoint = this.walkPath.get(0);
            this.angle = this.getAngle(this.currentWaypoint);
            return true;
        }
        return false;
    }

    public boolean updatePath() {
        this.currentWaypoint = null;

        if (this.goal == null) {
            return false;
        }

        Point start = new Point(this.tileX, this.tileY);
        AbstractTile startTile = this.level.getTile(this.tileX, this.tileY);
        AbstractTile goalTile = this.level.getTile(goal.x, goal.y);
        if (startTile != null && goalTile != null && !startTile.isBlockingPath() && !goalTile.isBlockingPath() && start != goal) {
            PathRunnable runnable = new PathRunnable(level.createAreaMap(), start, goal, null);
            runnable.run();
            this.walkPath = runnable.getWalkPath();
            this.refreshPathTicks = random.nextInt(60) + 60;
        }
        if (this.walkPath.size() > 0) {
            this.currentWaypoint = this.walkPath.get(0);
            this.angle = this.getAngle(this.currentWaypoint);
            return true;
        }
        return false;
    }

}
