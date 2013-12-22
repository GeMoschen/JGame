package de.gemo.pathfinding;

import java.awt.*;
import java.util.*;

public class Node implements Comparable<Node> {
    /* Nodes that this is connected to */
    AreaMap map;
    boolean visited;
    float distanceFromStart;
    float heuristicDistanceFromGoal;
    Node previousNode;
    int x;
    int y;
    public boolean isObstacle;
    public boolean isStart;
    public boolean isGoal;
    public boolean isPath;

    Node(int x, int y, AreaMap map) {
        this.x = x;
        this.y = y;
        this.map = map;
        this.visited = false;
        this.distanceFromStart = Integer.MAX_VALUE;
        this.isObstacle = false;
        this.isStart = false;
        this.isGoal = false;
    }

    Node(int x, int y, AreaMap map, boolean visited, int distanceFromStart, boolean isObstical, boolean isStart, boolean isGoal) {
        this.x = x;
        this.y = y;
        this.map = map;
        this.visited = visited;
        this.distanceFromStart = distanceFromStart;
        this.isObstacle = isObstical;
        this.isStart = isStart;
        this.isGoal = isGoal;
    }

    public ArrayList<Node> getNeighborList(boolean allowDiagonal) {
        ArrayList<Node> neighborList = new ArrayList<Node>();

        if (!(y == 0)) {
            neighborList.add(map.getNode(x, (y - 1)));
        }
        if (allowDiagonal) {
            if (!(y == 0) && !(x == (map.getMapWith() - 1))) {
                neighborList.add(map.getNode(x + 1, y - 1));
            }
        }
        if (!(x == (map.getMapWith() - 1))) {
            neighborList.add(map.getNode(x + 1, y));
        }
        if (allowDiagonal) {
            if (!(x == (map.getMapWith() - 1)) && !(y == (map.getMapHeight() - 1))) {
                neighborList.add(map.getNode(x + 1, y + 1));
            }
        }
        if (!(y == (map.getMapHeight() - 1))) {
            neighborList.add(map.getNode(x, y + 1));
        }
        if (allowDiagonal) {
            if (!(x == 0) && !(y == (map.getMapHeight() - 1))) {
                neighborList.add(map.getNode(x - 1, y + 1));
            }
        }
        if (!(x == 0)) {
            neighborList.add(map.getNode(x - 1, y));
        }
        if (allowDiagonal) {
            if (!(x == 0) && !(y == 0)) {
                neighborList.add(map.getNode(x - 1, y - 1));
            }
        }

        return neighborList;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public float getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(float f) {
        this.distanceFromStart = f;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    public float getHeuristicDistanceFromGoal() {
        return heuristicDistanceFromGoal;
    }

    public void setHeuristicDistanceFromGoal(float heuristicDistanceFromGoal) {
        this.heuristicDistanceFromGoal = heuristicDistanceFromGoal;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    public boolean isObstical() {
        return isObstacle;
    }

    public void setObstical(boolean isObstical) {
        this.isObstacle = isObstical;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public boolean isGoal() {
        return isGoal;
    }

    public void setGoal(boolean isGoal) {
        this.isGoal = isGoal;
    }

    public boolean isPath() {
        return isPath;
    }

    public void setPath(boolean isPath) {
        this.isPath = isPath;
    }

    public boolean equals(Node node) {
        return (node.x == x) && (node.y == y);
    }

    /**
     * Taken from Point2D
     */
    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getX());
        bits ^= java.lang.Double.doubleToLongBits(getY()) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }

    public int compareTo(Node otherNode) {
        float thisTotalDistanceFromGoal = heuristicDistanceFromGoal + distanceFromStart;
        float otherTotalDistanceFromGoal = otherNode.getHeuristicDistanceFromGoal() + otherNode.getDistanceFromStart();

        if (thisTotalDistanceFromGoal < otherTotalDistanceFromGoal) {
            return -1;
        } else if (thisTotalDistanceFromGoal > otherTotalDistanceFromGoal) {
            return 1;
        } else {
            return 0;
        }
    }
}