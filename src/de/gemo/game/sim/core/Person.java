package de.gemo.game.sim.core;

import java.awt.*;
import java.util.*;

import de.gemo.game.sim.tiles.*;

import static org.lwjgl.opengl.GL11.*;

public class Person {

    private float angle = 0;
    private float x = 0, y = 0;
    private int tileX = 0, tileY = 0;
    private Point goal = null;
    private Point currentWaypoint = null;
    private int waypointIndex = 0;

    private final Level level;

    private ArrayList<Point> walkPath = new ArrayList<Point>();

    public Person(Level level, float x, float y) {
        this.level = level;
        this.setPosition(x, y);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.tileX = (int) (x / (float) (AbstractTile.TILE_SIZE + 1));
        this.tileY = (int) (y / (float) (AbstractTile.TILE_SIZE + 1));
    }

    public void move(float x, float y) {
        this.setPosition(this.x + x, this.y + y);
    }

    public void update() {
        if (this.currentWaypoint == null) {
            return;
        }

        int wpX = (int) (this.currentWaypoint.x * AbstractTile.TILE_SIZE + this.currentWaypoint.x + AbstractTile.TILE_SIZE / 2f);
        int wpY = (int) (this.currentWaypoint.y * AbstractTile.TILE_SIZE + this.currentWaypoint.y + AbstractTile.TILE_SIZE / 2f);

        float mX = 0;
        float mY = 0;
        if (wpX < this.x)
            mX = -0.25f;
        if (wpX > this.x)
            mX = +0.25f;
        if (wpY < this.y)
            mY = -0.25f;
        if (wpY > this.y)
            mY = +0.25f;

        float movedX = this.x + mX;
        float movedY = this.y + mY;
        int movedTX = (int) (movedX / (float) (AbstractTile.TILE_SIZE + 1));
        int movedTY = (int) (movedY / (float) (AbstractTile.TILE_SIZE + 1));
        if (this.level.getTile(movedTX, movedTY).isBlockingPath()) {
            this.updatePath();
            return;
        }
        this.move(mX, mY);

        // reached current waypoint, so update
        if (this.x == wpX && this.y == wpY) {
            this.waypointIndex++;
            if (this.waypointIndex < this.walkPath.size()) {
                this.currentWaypoint = this.walkPath.get(this.waypointIndex);
            } else {
                this.currentWaypoint = null;
                this.waypointIndex = 0;

                // for testpurposes: find a new random target
                Random random = new Random();
                int x = random.nextInt(this.level.getDimX());
                int y = random.nextInt(this.level.getDimY());
                while (this.level.getTile(x, y).isBlockingPath()) {
                    x = random.nextInt(this.level.getDimX());
                    y = random.nextInt(this.level.getDimY());
                }
                this.setTarget(new Point(x, y));
            }
        }
    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);

            glTranslatef(x, y, 0);
            glColor3f(1, 0, 0);
            glBegin(GL_QUADS);
            {
                glVertex2i(-2, -2);
                glVertex2i(+2, -2);
                glVertex2i(+2, +2);
                glVertex2i(-2, +2);
            }
            glEnd();

            // viewLine
            glRotatef(this.angle, 0, 0, 1);
            glColor3f(0, 1, 0);
            glBegin(GL_LINE);
            {
                glVertex2i(0, 0);
                glVertex2i(4, 0);
            }
            glEnd();

        }
        glPopMatrix();

        // render walkpath
        this.renderWalkPath();

        this.renderSingle(tileX, tileY, 1, 1, 0, 0.2f);
        if (this.goal != null) {
            this.renderSingle(goal.x, goal.y, 0, 1, 0, 0.2f);
        }
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

    public boolean setTarget(Point goal) {
        this.goal = goal;
        return this.updatePath();
    }

    private void updateWalkPath(Point start, Point goal, ArrayList<Point> tilePath) {
        this.walkPath.clear();

        if (tilePath == null) {
            return;
        }

        this.walkPath.add(start);
        this.walkPath.addAll(tilePath);

        // get corrected walkpath-infos
        ArrayList<Point> newWalkPath = new ArrayList<Point>();
        for (Point node : this.walkPath) {
            newWalkPath.add(new Point(node.x * AbstractTile.TILE_SIZE + node.x + AbstractTile.TILE_SIZE / 2, node.y * AbstractTile.TILE_SIZE + node.y + AbstractTile.TILE_SIZE / 2));
        }
        this.walkPath = newWalkPath;

        // smooth the corners
        System.out.println("-------------");
        System.out.println("before smooth: " + this.walkPath.size());
        this.smoothWalkPath();
        System.out.println("after smooth: " + this.walkPath.size());

        // optimize the path 2 times
        this.optimizeWalkPath();
        this.optimizeWalkPath();
        System.out.println("optimized: " + this.walkPath.size());

        // round the corners
        this.roundCorners();
        System.out.println("smoothed: " + this.walkPath.size());

        // update the waypoint
        this.currentWaypoint = this.walkPath.get(0);
    }

    private void roundCorners() {
        for (int index = 1; index < this.walkPath.size() - 1; index++) {
            int lastDir = this.getWalkDirection(this.walkPath.get(index - 1), this.walkPath.get(index));
            int currentDir = this.getWalkDirection(this.walkPath.get(index), this.walkPath.get(index + 1));
            if (currentDir % 2 == 0) {
                continue;
            }
            Point currentPoint = this.walkPath.get(index);
            Point nextPoint = this.walkPath.get(index + 1);
            double distance = Math.abs(Math.sqrt(Math.pow(currentPoint.x - nextPoint.x, 2) + Math.pow(currentPoint.y - nextPoint.y, 2)));
            if (distance > AbstractTile.TILE_SIZE) {
                continue;
            }

            float dX = (nextPoint.x - currentPoint.x) / 2f;
            float dY = (nextPoint.y - currentPoint.y) / 2f;
            Point newPoint = new Point(currentPoint);
            if (lastDir == 6 || lastDir == 8) {
                newPoint.x += dX + dX / 3f;
                newPoint.y += dY - dY / 3f;
            }
            if (lastDir == 2 || lastDir == 0) {
                newPoint.x += dX - dX / 3f;
                newPoint.y += dY + dY / 3f;
            }
            this.walkPath.add(index + 1, newPoint);
            index += 2;
        }
    }

    private void smoothWalkPath() {
        for (int index = 1; index < walkPath.size() - 1; index++) {
            final int lastDir = this.getWalkDirection(walkPath.get(index - 1), walkPath.get(index));
            final int currentDir = this.getWalkDirection(walkPath.get(index), walkPath.get(index + 1));
            boolean hadDirChange = (lastDir != currentDir);

            if (hadDirChange) {
                Point newPoint = new Point(walkPath.get(index));
                // top
                if (lastDir == 0) {
                    walkPath.get(index).y += (AbstractTile.TILE_SIZE / 2);
                }
                // right
                if (lastDir == 6) {
                    walkPath.get(index).x -= (AbstractTile.TILE_SIZE / 2);
                }
                // bottom
                if (lastDir == 2) {
                    walkPath.get(index).y -= (AbstractTile.TILE_SIZE / 2);
                }
                // left
                if (lastDir == 8) {
                    walkPath.get(index).x += (AbstractTile.TILE_SIZE / 2);
                }

                // top
                if (currentDir == 0) {
                    newPoint.y -= (AbstractTile.TILE_SIZE / 2);
                }
                // right
                if (currentDir == 6) {
                    newPoint.x += (AbstractTile.TILE_SIZE / 2);
                }
                // bottom
                if (currentDir == 2) {
                    newPoint.y += (AbstractTile.TILE_SIZE / 2);
                }
                // left
                if (currentDir == 8) {
                    newPoint.x -= (AbstractTile.TILE_SIZE / 2);
                }
                walkPath.add(index + 1, newPoint);
                index += 1;
            }
        }
    }

    private int getWalkDirection(Point p1, Point p2) {
        // top-right
        if (p1.x < p2.x && p1.y > p2.y) {
            return 1;
        }
        // top-left
        if (p1.x > p2.x && p1.y > p2.y) {
            return 9;
        }
        // bottom-right
        if (p1.x < p2.x && p1.y < p2.y) {
            return 7;
        }
        // bottom-left
        if (p1.x > p2.x && p1.y < p2.y) {
            return 3;
        }

        // top
        if (p1.x == p2.x && p1.y > p2.y) {
            return 0;
        }
        // right
        if (p1.x < p2.x && p1.y == p2.y) {
            return 6;
        }
        // bottom
        if (p1.x == p2.x && p1.y < p2.y) {
            return 2;
        }
        // left
        if (p1.x > p2.x && p1.y == p2.y) {
            return 8;
        }

        // default
        return -21;
    }

    private int getDirDifference(int directionA, int directionB) {
        return Math.max(directionA, directionB) - Math.min(directionA, directionB);
    }

    private void optimizeWalkPath() {
        for (int index = 1; index < this.walkPath.size() - 1; index++) {
            int lastDir = this.getWalkDirection(this.walkPath.get(index - 1), this.walkPath.get(index));
            int currentDir = this.getWalkDirection(this.walkPath.get(index), this.walkPath.get(index + 1));
            Point lastPoint = this.walkPath.get(index - 1);
            Point currentPoint = this.walkPath.get(index);
            double distance = Math.abs(Math.sqrt(Math.pow(currentPoint.x - lastPoint.x, 2) + Math.pow(currentPoint.y - lastPoint.y, 2)));
            if (lastDir == currentDir || this.getDirDifference(lastDir, currentDir) == 2 || distance < AbstractTile.TILE_SIZE / 10d) {
                this.walkPath.remove(index);
                index--;
            }
        }
    }

    private void renderWalkPath() {
        for (int i = 0; i < this.walkPath.size(); i++) {
            glPushMatrix();
            {
                Point node = this.walkPath.get(i);
                glLineWidth(1);
                glDisable(GL_LIGHTING);
                glEnable(GL_BLEND);
                glColor4f(1, 1, 1, 0.5f);
                glBegin(GL_LINE_LOOP);
                {
                    glVertex2f(node.x - 2, node.y - 2);
                    glVertex2f(node.x + 2, node.y - 2);
                    glVertex2f(node.x + 2, node.y + 2);
                    glVertex2f(node.x - 2, node.y + 2);
                }
                glEnd();
            }
            glPopMatrix();
        }
        //
        // glPushMatrix();
        // {
        // Point node = this.walkPath.get(1);
        // glLineWidth(1);
        // glDisable(GL_LIGHTING);
        // glEnable(GL_BLEND);
        // glColor4f(1, 1, 1, 0.5f);
        // glBegin(GL_LINE_LOOP);
        // {
        // glVertex2f(node.x - 2, node.y - 2);
        // glVertex2f(node.x + 2, node.y - 2);
        // glVertex2f(node.x + 2, node.y + 2);
        // glVertex2f(node.x - 2, node.y + 2);
        // }
        // glEnd();
        // }
        // glPopMatrix();

        for (int i = 0; i < this.walkPath.size() - 1; i++) {
            glPushMatrix();
            {
                Point node = this.walkPath.get(i);
                Point next = this.walkPath.get(i + 1);
                glLineWidth(1);
                glDisable(GL_LIGHTING);
                glEnable(GL_BLEND);
                glColor4f(1, 1, 0, 0.5f);
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

    public boolean updatePath() {
        this.currentWaypoint = null;
        this.waypointIndex = 0;
        if (this.goal == null) {
            return false;
        }

        Point start = new Point(this.tileX, this.tileY);
        AbstractTile startTile = this.level.getTile(this.tileX, this.tileY);
        AbstractTile goalTile = this.level.getTile(goal.x, goal.y);
        if (startTile != null && goalTile != null && !startTile.isBlockingPath() && !goalTile.isBlockingPath() && start != goal) {
            this.updateWalkPath(start, goal, level.getPath(start, goal));
        }
        return this.walkPath.size() > 0;
    }

}
