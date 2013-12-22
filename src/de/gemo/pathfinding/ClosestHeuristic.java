package de.gemo.pathfinding;

import java.awt.Point;

/**
 * A heuristic that uses the tile that is closest to the target as the next best
 * tile.
 */
public class ClosestHeuristic implements AStarHeuristic {

    public float getEstimatedDistanceToGoal(Point start, Point goal) {
        float dx = goal.x - start.x;
        float dy = goal.y - start.y;

        float result = (float) (Math.sqrt((dx * dx) + (dy * dy)));

        // Optimization! Changed to distance^2 distance: (but looks more "ugly")

        // float result = (float) (dx*dx)+(dy*dy);

        return result;
    }

}