package de.gemo.pathfinding;

import java.awt.*;
import java.util.*;

import de.gemo.game.sim.core.*;
import de.gemo.game.sim.tiles.*;
import de.gemo.gameengine.units.Vector;

public class SinglePathSearch {

    private AStar star = null;
    private ArrayList<Point> walkPath = new ArrayList<Point>();
    private Point start, goal;
    private boolean pathFound = false;
    private boolean searchDone = false;
    private PathFinishListener listener = null;

    public SinglePathSearch(AreaMap areaMap, Point start, Point goal, PathFinishListener listener) {
        this.star = new AStar(areaMap, new DiagonalHeuristic(), false);
        this.start = start;
        this.goal = goal;
        this.listener = listener;
    }

    public void run() {
        // init search
        this.searchDone = false;
        this.pathFound = false;
        // calculate path
        ArrayList<Point> tilePath = star.calcShortestPath(this.start.x, this.start.y, this.goal.x, this.goal.y);
        // end search
        this.pathFound = (tilePath != null && tilePath.size() > 0);

        if (this.pathFound) {
            this.updateWalkPath(start, goal, tilePath);
        }
        this.searchDone = true;

        if (this.listener != null) {
            if (this.pathFound) {
                this.listener.onSearchSuccessful(start, goal, this.walkPath);
            } else {
                this.listener.onSearchUnsuccessful(start, goal);
            }
        }
    }

    public synchronized boolean isSearchDone() {
        return searchDone;
    }

    public synchronized boolean isPathFound() {
        return pathFound;
    }

    private void updateWalkPath(Point start, Point goal, ArrayList<Point> tilePath) {
        this.walkPath.clear();

        if (tilePath == null) {
            return;
        }

        this.walkPath.add(start);
        this.walkPath.addAll(tilePath);

        // get corrected walkpath-infos
        for (Point node : this.walkPath) {
            node.x = (int) (node.x * AbstractTile.TILE_SIZE + node.x + AbstractTile.TILE_SIZE / 2);
            node.y = (int) (node.y * AbstractTile.TILE_SIZE + node.y + AbstractTile.TILE_SIZE / 2);
        }

        // smooth the corners
        this.smoothWalkPath();

        // optimize the path
        this.optimizeWalkPath();

        // round the corners
        this.roundWalkPath(1);

        // round diagonals
        this.roundDiagonalWalkPath();
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

            if ((lastDir == currentDir && distance >= AbstractTile.HALF_TILE_SIZE) || distance < AbstractTile.QUARTER_TILE_SIZE) {
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

    private void roundDiagonalWalkPath() {
        for (int index = 1; index < this.walkPath.size() - 2; index++) {
            EnumDir currentDir = this.getWalkDirection(this.walkPath.get(index), this.walkPath.get(index + 1));

            if (!currentDir.isDiagonal()) {
                continue;
            }

            Point currentPoint = this.walkPath.get(index);
            Point nextPoint = this.walkPath.get(index + 1);

            double distance = Math.abs(Math.sqrt(Math.pow(currentPoint.x - nextPoint.x, 2) + Math.pow(currentPoint.y - nextPoint.y, 2)));
            if (distance <= AbstractTile.HALF_TILE_SIZE) {
                continue;
            }

            EnumDir lastDir = this.getWalkDirection(this.walkPath.get(index - 1), this.walkPath.get(index));
            EnumDir nextDir = this.getWalkDirection(this.walkPath.get(index + 1), this.walkPath.get(index + 2));

            int newPoints = 0;
            int MOVEMENT = AbstractTile.HALF_TILE_SIZE;
            Point actualCurrent = null, actualNext = null;
            int dXActual = 0, dYActual = 0;
            int dXNext = 0, dYNext = 0;
            if (lastDir.isLine()) {
                actualCurrent = new Point(currentPoint);
                if (lastDir.equals(EnumDir.TOP)) {
                    dYActual += MOVEMENT;
                } else if (lastDir.equals(EnumDir.BOTTOM)) {
                    dYActual -= MOVEMENT;
                } else if (lastDir.equals(EnumDir.LEFT)) {
                    dXActual += MOVEMENT;
                } else if (lastDir.equals(EnumDir.RIGHT)) {
                    dXActual -= MOVEMENT;
                }
                actualCurrent.x += dXActual;
                actualCurrent.y += dYActual;
                this.walkPath.add(index, actualCurrent);
                newPoints += 1;
            }

            if (nextDir.isLine()) {
                actualNext = new Point(nextPoint);
                if (nextDir.equals(EnumDir.TOP)) {
                    dYNext -= MOVEMENT;
                } else if (nextDir.equals(EnumDir.BOTTOM)) {
                    dYNext += MOVEMENT;
                } else if (nextDir.equals(EnumDir.LEFT)) {
                    dXNext -= MOVEMENT;
                } else if (nextDir.equals(EnumDir.RIGHT)) {
                    dXNext += MOVEMENT;
                }
                actualNext.x += dXNext;
                actualNext.y += dYNext;
                this.walkPath.add(index + 3, actualNext);
                newPoints++;
            }

            if (currentDir.equals(EnumDir.TOP_RIGHT)) {
                currentPoint.x += AbstractTile.QUARTER_TILE_SIZE;
                currentPoint.y -= AbstractTile.QUARTER_TILE_SIZE;
                nextPoint.x -= AbstractTile.QUARTER_TILE_SIZE;
                nextPoint.y += AbstractTile.QUARTER_TILE_SIZE;
            } else if (currentDir.equals(EnumDir.BOTTOM_RIGHT)) {
                currentPoint.x += AbstractTile.QUARTER_TILE_SIZE;
                currentPoint.y += AbstractTile.QUARTER_TILE_SIZE;
                nextPoint.x -= AbstractTile.QUARTER_TILE_SIZE;
                nextPoint.y -= AbstractTile.QUARTER_TILE_SIZE;
            } else if (currentDir.equals(EnumDir.TOP_LEFT)) {
                currentPoint.x -= AbstractTile.QUARTER_TILE_SIZE;
                currentPoint.y -= AbstractTile.QUARTER_TILE_SIZE;
                nextPoint.x += AbstractTile.QUARTER_TILE_SIZE;
                nextPoint.y += AbstractTile.QUARTER_TILE_SIZE;
            } else if (currentDir.equals(EnumDir.BOTTOM_LEFT)) {
                currentPoint.x -= AbstractTile.QUARTER_TILE_SIZE;
                currentPoint.y += AbstractTile.QUARTER_TILE_SIZE;
                nextPoint.x += AbstractTile.QUARTER_TILE_SIZE;
                nextPoint.y -= AbstractTile.QUARTER_TILE_SIZE;
            }

            if (lastDir.isLine()) {
                Point betweenPoint = new Point(actualCurrent);
                Point afterPoint = new Point(currentPoint);
                betweenPoint.x += ((afterPoint.x - betweenPoint.x) / 2) - (dXActual / 3);
                betweenPoint.y += ((afterPoint.y - betweenPoint.y) / 2) - (dYActual / 3);
                this.walkPath.add(index + 1, betweenPoint);
                newPoints++;
            }
            if (nextDir.isLine()) {
                Point betweenPoint = new Point(actualNext);
                Point afterPoint = new Point(nextPoint);
                betweenPoint.x += ((afterPoint.x - betweenPoint.x) / 2) - (dXNext / 3);
                betweenPoint.y += ((afterPoint.y - betweenPoint.y) / 2) - (dYNext / 3);
                this.walkPath.add(index + 4, betweenPoint);
                newPoints++;
            }

            index += 1 + newPoints;
        }
    }

    public synchronized ArrayList<Point> getWalkPath() {
        return walkPath;
    }

}
