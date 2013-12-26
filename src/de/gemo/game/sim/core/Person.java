package de.gemo.game.sim.core;

import java.awt.*;
import java.util.*;

import de.gemo.game.sim.tiles.*;
import de.gemo.gameengine.units.Vector;

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

        this.angle = this.getAngle(this.currentWaypoint);

        float mX = (float) (Math.sin(Math.toRadians(this.angle + 90)) * 5);
        float mY = (float) (-Math.cos(Math.toRadians(this.angle + 90)) * 5);

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
        float distance = (float) Math.abs(Math.sqrt(Math.pow(this.currentWaypoint.x - x, 2) + Math.pow(this.currentWaypoint.y - y, 2)));

        if (distance <= AbstractTile.TENTH_TILE_SIZE || distance < Math.max(mX, mY)) {
            this.waypointIndex++;
            if (this.waypointIndex < this.walkPath.size()) {
                this.currentWaypoint = this.walkPath.get(this.waypointIndex);
                this.angle = this.getAngle(this.currentWaypoint);
            } else {
                this.currentWaypoint = null;
                this.waypointIndex = 0;

                // for testpurposes: find a new random target

                Random random = new Random();
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
        }
    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);

            glTranslatef(x, y, 0);
            glRotatef(this.angle, 0, 0, 1);

            glColor3f(1, 0, 0);
            glBegin(GL_QUADS);
            {
                glVertex2i(-5, -5);
                glVertex2i(+5, -5);
                glVertex2i(+5, +5);
                glVertex2i(-5, +5);
            }
            glEnd();

            // viewLine
            glColor3f(0, 1, 0);
            glBegin(GL_LINES);
            {
                glVertex2i(0, 0);
                glVertex2i(10, 0);
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

    private void renderWalkPath() {
        for (int i = 0; i < this.walkPath.size(); i++) {
            glPushMatrix();
            {
                Point node = this.walkPath.get(i);
                glLineWidth(1);
                glDisable(GL_LIGHTING);
                glEnable(GL_BLEND);
                glColor4f(0, 1, 1, 1f);
                glBegin(GL_LINE_LOOP);
                {
                    glVertex2f(node.x - 1, node.y - 1);
                    glVertex2f(node.x + 1, node.y - 1);
                    glVertex2f(node.x + 1, node.y + 1);
                    glVertex2f(node.x - 1, node.y + 1);
                }
                glEnd();
            }
            glPopMatrix();
        }

        for (int i = 0; i < this.walkPath.size() - 1; i++) {
            glPushMatrix();
            {
                Point node = this.walkPath.get(i);
                Point next = this.walkPath.get(i + 1);
                glLineWidth(1);
                glDisable(GL_LIGHTING);
                glEnable(GL_BLEND);
                glColor4f(1, 1, 0, 0.2f);
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
        if (this.walkPath.size() > 0) {
            this.currentWaypoint = this.walkPath.get(0);
            this.angle = this.getAngle(this.currentWaypoint);
            return true;
        }
        return false;
    }

    private EnumDir getWalkDirection(Point p1, Point p2) {
        // top-right
        if (p1.x < p2.x && p1.y > p2.y) {
            return EnumDir.TOP_RIGHT;
        }
        // bottom-right
        if (p1.x < p2.x && p1.y < p2.y) {
            return EnumDir.BOTTOM_RIGHT;
        }
        // bottom-left
        if (p1.x > p2.x && p1.y < p2.y) {
            return EnumDir.BOTTOM_LEFT;
        }// top-left
        if (p1.x > p2.x && p1.y > p2.y) {
            return EnumDir.TOP_LEFT;
        }

        // top
        if (p1.x == p2.x && p1.y > p2.y) {
            return EnumDir.TOP;
        }
        // right
        if (p1.x < p2.x && p1.y == p2.y) {
            return EnumDir.RIGHT;
        }
        // bottom
        if (p1.x == p2.x && p1.y < p2.y) {
            return EnumDir.BOTTOM;
        }
        // left
        if (p1.x > p2.x && p1.y == p2.y) {
            return EnumDir.LEFT;
        }

        // default
        return EnumDir.UNKNOWN;
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

        // optimize the path
        this.optimizeWalkPath();

        // round the corners
        this.roundWalkPath(4);
        System.out.println("smoothed: " + this.walkPath.size());

        // TODO: round diagonals
        // this.roundDiagonalWalkPath(1);

        System.out.println("optimized: " + this.walkPath.size());

        // update the waypoint
        this.currentWaypoint = this.walkPath.get(0);
    }

    private void smoothWalkPath() {
        for (int index = 1; index < walkPath.size() - 1; index++) {
            EnumDir lastDir = this.getWalkDirection(walkPath.get(index - 1), walkPath.get(index));
            EnumDir currentDir = this.getWalkDirection(walkPath.get(index), walkPath.get(index + 1));
            boolean hadDirChange = (lastDir != currentDir);

            if (hadDirChange) {
                Point newPoint = new Point(walkPath.get(index));
                // top
                if (lastDir.equals(EnumDir.TOP)) {
                    walkPath.get(index).y += AbstractTile.HALF_TILE_SIZE;
                }
                // right
                if (lastDir.equals(EnumDir.RIGHT)) {
                    walkPath.get(index).x -= AbstractTile.HALF_TILE_SIZE;
                }
                // bottom
                if (lastDir.equals(EnumDir.BOTTOM)) {
                    walkPath.get(index).y -= AbstractTile.HALF_TILE_SIZE;
                }
                // left
                if (lastDir.equals(EnumDir.LEFT)) {
                    walkPath.get(index).x += AbstractTile.HALF_TILE_SIZE;
                }

                // top
                if (currentDir.equals(EnumDir.TOP)) {
                    newPoint.y -= AbstractTile.HALF_TILE_SIZE;
                }
                // right
                if (currentDir.equals(EnumDir.RIGHT)) {
                    newPoint.x += AbstractTile.HALF_TILE_SIZE;
                }
                // bottom
                if (currentDir.equals(EnumDir.BOTTOM)) {
                    newPoint.y += AbstractTile.HALF_TILE_SIZE;
                }
                // left
                if (currentDir.equals(EnumDir.LEFT)) {
                    newPoint.x -= AbstractTile.HALF_TILE_SIZE;
                }
                walkPath.add(index + 1, newPoint);
                index++;
            }
        }
    }

    /**
     * This method will reduce the total number of waypoints for this path. If
     * the waypoint before AND after the current waypoint are heading into the
     * same direction, we can remove it! We will also remove Waypoints who are
     * too close to each other!
     */
    private void optimizeWalkPath() {
        // we need at least 3 waypoints, if we want something to optimize...
        if (this.walkPath.size() < 3) {
            return;
        }

        for (int index = 1; index < this.walkPath.size() - 1; index++) {
            // if the path is short enough and we optimize it, it MAY occur that
            // the index is < 1. To avoid this, just set the minimum to 1...
            if (index < 1)
                index = 1;

            // get the current and the last direction
            EnumDir lastDir = this.getWalkDirection(this.walkPath.get(index - 1), this.walkPath.get(index));
            EnumDir currentDir = this.getWalkDirection(this.walkPath.get(index), this.walkPath.get(index + 1));

            // get the last and the current point, and the distance between them
            Point lastPoint = this.walkPath.get(index - 1);
            Point currentPoint = this.walkPath.get(index);
            double distance = Math.abs(Math.sqrt(Math.pow(currentPoint.x - lastPoint.x, 2) + Math.pow(currentPoint.y - lastPoint.y, 2)));

            /*
             * ---------------------------------------------------------------
             * THE HEART OF THE METHOD ---------------------------------------
             * ---------------------------------------------------------------
             * If the directions are equal or the waypoints are too close, we
             * can remove the current waypoint.
             */

            if ((lastDir == currentDir && distance >= AbstractTile.HALF_TILE_SIZE) || distance < AbstractTile.TENTH_TILE_SIZE) {
                // remove the current waypoint
                this.walkPath.remove(index);

                // decrement by 2, because we want to look at this waypoint
                // again. Decrementing by 2 means that we only need to call this
                // method once, because otherwise diagonals aren't correctly
                // optmized...
                index -= 2;
            }

            // we need at least 3 waypoints, if we want something to optimize...
            if (this.walkPath.size() < 3) {
                return;
            }
        }
    }

    private void roundWalkPath(int smoothCount) {
        // only smooth, if the count is bigger 0
        if (smoothCount < 1) {
            return;
        }

        // iterate over the points, but DON'T smooth the first and the last
        // line. (just time optimization, cause the first and the last lines are
        // always non diagonal)
        for (int index = 1; index < this.walkPath.size() - 1; index++) {
            // get the last and the current direction
            EnumDir lastDir = this.getWalkDirection(this.walkPath.get(index - 1), this.walkPath.get(index));
            EnumDir currentDir = this.getWalkDirection(this.walkPath.get(index), this.walkPath.get(index + 1));

            // we only smooth if the current direction is a diagonal and the
            // last direction isn't a diagonal
            if (currentDir.isLine() || lastDir.isDiagonal()) {
                continue;
            }

            // get the points, and the distance between them
            Point currentPoint = this.walkPath.get(index);
            Point nextPoint = this.walkPath.get(index + 1);
            double distance = Math.abs(Math.sqrt(Math.pow(currentPoint.x - nextPoint.x, 2) + Math.pow(currentPoint.y - nextPoint.y, 2)));

            // if the distance is bigger than the TILE_SIZE, the corner isn't a
            // corner, it is a diagonal over multiple tiles (which we don't
            // smooth!).
            if (distance > AbstractTile.TILE_SIZE) {
                continue;
            }

            // determine the values for the calculation of the rotational center
            float dX = (nextPoint.x - currentPoint.x);
            float dY = (nextPoint.y - currentPoint.y);
            float offX = 0f;
            float offY = 0f;
            if (lastDir.isHorizontal() && currentDir.isDiagonal()) {
                offY = dY;
            } else if (lastDir.isVertical() && currentDir.isDiagonal()) {
                offX = dX;
            }

            // create the rotational center
            Vector center = new Vector(currentPoint.x + offX, currentPoint.y + offY);

            // determine the needed values
            float startAngle = 0;
            float angleStep = 90 / (smoothCount + 1);

            if ((lastDir.isHorizontal() && currentDir.isNWToSE()) || (lastDir.isVertical() && currentDir.isNEToSW())) {
                startAngle = 90;
                angleStep = -angleStep;
            } else if ((lastDir.isHorizontal() && currentDir.isNEToSW()) || (lastDir.isVertical() && currentDir.isNWToSE())) {
                startAngle = 270;
            }

            // add new points and rotate them around the rotational center
            float currentAngle = angleStep;
            for (int i = 0; i < smoothCount; i++) {
                Vector newPoint = new Vector(center.getX() - offX, center.getY() - offY);
                newPoint.rotateAround(center, startAngle + currentAngle);
                this.walkPath.add(index + 1, new Point((int) newPoint.getX(), (int) newPoint.getY()));
                currentAngle += angleStep;
            }

            // increment the index, so the currently added points are not
            // smoothed again
            index += (1 + (smoothCount));
        }
    }

    private void roundDiagonalWalkPath(int count) {
        if (count < 1) {
            return;
        }

        float radius = AbstractTile.TILE_SIZE * 2.5f;
        float radOffX = -radius / (5 + 1 / 3);
        float radOffY = radius;

        for (int index = 1; index < this.walkPath.size() - 1; index++) {
            EnumDir lastDir = this.getWalkDirection(this.walkPath.get(index - 1), this.walkPath.get(index));
            EnumDir currentDir = this.getWalkDirection(this.walkPath.get(index), this.walkPath.get(index + 1));

            boolean startDiagonal = (currentDir.isDiagonal() && lastDir.isLine());
            boolean endDiagonal = (currentDir.isLine() && lastDir.isDiagonal());

            System.out.println("dir: " + lastDir + " - " + currentDir);

            if (!startDiagonal && !endDiagonal) {
                continue;
            }
            Point lastPoint = this.walkPath.get(index - 1);
            Point currentPoint = this.walkPath.get(index);
            Point nextPoint = this.walkPath.get(index + 1);

            double distance = Math.abs(Math.sqrt(Math.pow(currentPoint.x - nextPoint.x, 2) + Math.pow(currentPoint.y - nextPoint.y, 2)));
            if (distance < AbstractTile.TILE_SIZE) {
                continue;
            }

            Vector center = new Vector(currentPoint.x + radOffX, currentPoint.y + radOffY);
            int offX = 0;
            int offY = 0;
            if (startDiagonal) {
            } else {

            }

        }
    }
}
