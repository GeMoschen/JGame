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

        int mX = 0;
        int mY = 0;
        if (wpX < this.x)
            mX = -1;
        if (wpX > this.x)
            mX = +1;
        if (wpY < this.y)
            mY = -1;
        if (wpY > this.y)
            mY = +1;

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
                this.setTarget(new Point(random.nextInt(this.level.getDimX()), random.nextInt(this.level.getDimY())));
                while (!this.setTarget(new Point(random.nextInt(this.level.getDimX()), random.nextInt(this.level.getDimY()))))
                    ;
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

        tilePath.add(0, start);
        Point currentPoint;
        Point twoAheadPoint;

        this.walkPath.add(start);
        for (int index = 0; index < tilePath.size() - 2; index++) {
            currentPoint = tilePath.get(index);
            twoAheadPoint = tilePath.get(index + 2);
            if ((currentPoint.x != twoAheadPoint.x && currentPoint.y != twoAheadPoint.y)) {
                if (!this.walkPath.contains(currentPoint)) {
                    this.walkPath.add(currentPoint);
                }
                if (!this.walkPath.contains(twoAheadPoint)) {
                    this.walkPath.add(twoAheadPoint);
                }
                index++;
            }
        }

        if (this.walkPath.size() < 2 || !this.walkPath.get(this.walkPath.size() - 1).equals(goal)) {
            this.walkPath.add(goal);
        }

        // update the waypoint
        this.currentWaypoint = this.walkPath.get(1);
    }

    private void renderWalkPath() {
        for (int i = 0; i < this.walkPath.size() - 1; i++) {
            glPushMatrix();
            {
                Point node = this.walkPath.get(i);
                Point next = this.walkPath.get(i + 1);
                glLineWidth(1);
                glDisable(GL_LIGHTING);
                glEnable(GL_BLEND);
                glColor4f(1, 1, 1, 0.5f);
                glBegin(GL_LINES);
                {
                    glVertex2f(node.x * AbstractTile.TILE_SIZE + node.x + AbstractTile.TILE_SIZE / 2f, node.y * AbstractTile.TILE_SIZE + node.y + AbstractTile.TILE_SIZE / 2f);
                    glVertex2f(next.x * AbstractTile.TILE_SIZE + next.x + AbstractTile.TILE_SIZE / 2f, next.y * AbstractTile.TILE_SIZE + next.y + AbstractTile.TILE_SIZE / 2f);
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
