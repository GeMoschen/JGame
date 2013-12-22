package de.gemo.pathfinding;

import java.awt.Point;

/**
 * A heuristic that uses the tile that is closest to the target as the next best
 * tile.
 */
public class ManhattanHeuristic implements AStarHeuristic {

    public float getEstimatedDistanceToGoal(Point start, Point goal) {
        float dist = Math.abs(start.x - goal.x) + Math.abs(start.y - goal.y);
        // float p = (1 / 10000);
        // dist *= (1.0 + p);
        return dist;
    }

}