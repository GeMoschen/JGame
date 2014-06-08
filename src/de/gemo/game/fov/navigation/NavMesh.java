package de.gemo.game.fov.navigation;

import java.util.*;

import de.gemo.game.fov.units.*;
import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.units.*;

public class NavMesh {
    private List<NavNode> navPoints;

    public NavMesh(List<Tile> tileList) {
        this.navPoints = new ArrayList<NavNode>();
        this.createNavMesh(tileList);
    }

    public void render(Vector3f goal) {
        for (NavNode node : this.navPoints) {
            node.render();
        }
    }

    public Path path = null;

    public void createNavMesh(List<Tile> tileList) {
        this.navPoints.clear();
        // add all available points
        for (Tile tile : tileList) {
            Hitbox hitbox = this.expandHitbox(tile.getHitbox(), 10);
            for (Vector3f vector : hitbox.getPoints()) {

                boolean found = false;
                for (Tile checkTile : tileList) {
                    if (tile == checkTile) {
                        continue;
                    }

                    Hitbox otherHitbox = this.expandHitbox(checkTile.getHitbox(), 9);
                    if (CollisionHelper.isVectorInHitbox(vector, otherHitbox)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.navPoints.add(new NavNode(vector));
                }
            }
        }

        this.findNeighbours(tileList);
    }

    private void findNeighbours(List<Tile> tileList) {
        NavNode current, other;
        for (int i = 0; i < this.navPoints.size(); i++) {
            current = this.navPoints.get(i);
            for (int j = i + 1; j < this.navPoints.size(); j++) {
                other = this.navPoints.get(j);

                // create raycast
                Hitbox raycast = new Hitbox(0, 0);
                raycast.addPoint(current.getPosition());
                raycast.addPoint(other.getPosition());

                // check for colliding polys
                boolean canSeeTarget = true;
                for (Tile block : tileList) {
                    if (CollisionHelper.findIntersection(raycast, block.getHitbox()) != null) {
                        canSeeTarget = false;
                        break;
                    }
                }
                if (canSeeTarget) {
                    current.addNeighbor(other);
                    other.addNeighbor(current);
                }
            }
        }
    }

    public Path findPath(Vector3f start, Vector3f goal, List<Tile> tileList) {
        long startTime = System.nanoTime();

        // reset heuristics
        for (NavNode node : this.navPoints) {
            node.reset();
        }

        // create start and goal
        NavNode startNode = new NavNode(start);
        NavNode goalNode = new NavNode(goal);

        // add temporary connections
        this.findNeighborsForNode(startNode, tileList, 0);
        this.findNeighborsForNode(goalNode, tileList, 9);

        System.out.println();
        long duration = System.nanoTime() - startTime;
        float dur = duration / 1000000f;
        System.out.println("took 1: " + dur);

        // create raycast from start to goal
        Hitbox raycast = new Hitbox(0, 0);
        raycast.addPoint(startNode.getPosition());
        raycast.addPoint(goalNode.getPosition());

        // check for colliding polys
        boolean canSeeTarget = true;
        for (Tile block : tileList) {
            if (CollisionHelper.findIntersection(raycast, this.expandHitbox(block.getHitbox(), 10)) != null) {
                canSeeTarget = false;
                break;
            }
        }
        if (canSeeTarget) {
            goalNode.addNeighbor(startNode);
            startNode.addNeighbor(goalNode);
        }

        // initialize A*
        Set<NavNode> closedList = new HashSet<NavNode>();
        PriorityQueue<NavNode> openList = new PriorityQueue<NavNode>();
        openList.add(startNode);
        NavNode currentNode;
        while (!openList.isEmpty()) {
            currentNode = openList.poll();

            // check if our current Node location is the goal Node. If it is, we
            // are done.
            if (currentNode.equals(goalNode)) {
                // remove temporary connections
                this.removeNodeFromNeighbors(startNode);
                this.removeNodeFromNeighbors(goalNode);

                duration = System.nanoTime() - startTime;
                dur = duration / 1000000f;
                System.out.println("took: " + dur);

                // reconstruct path
                return reconstructPath(currentNode, startNode);
            }

            closedList.add(currentNode);
            this.expandNode(currentNode, goalNode, openList, closedList);
        }

        // remove temporary connections
        this.removeNodeFromNeighbors(startNode);
        this.removeNodeFromNeighbors(goalNode);

        duration = System.nanoTime() - startTime;
        dur = duration / 1000000f;
        System.out.println("took: " + dur);

        return null;
    }

    private void expandNode(NavNode currentNode, NavNode goalNode, PriorityQueue<NavNode> openList, Set<NavNode> closedList) {
        for (NavNode neighbor : currentNode.getNeighbors()) {
            if (closedList.contains(neighbor)) {
                continue;
            }

            float g = (float) (currentNode.getDistanceFromStart() + neighbor.getPosition().distanceTo(currentNode.getPosition()));
            if (openList.contains(neighbor) && g > neighbor.getDistanceFromStart()) {
                continue;
            }

            neighbor.setPreviousNode(currentNode);
            neighbor.setDistanceFromStart(g);

            float f = g + this.getEstimatedDistanceToGoal(neighbor, goalNode);
            if (openList.contains(neighbor)) {
                neighbor.setHeuristicDistanceFromGoal(f);
            } else {
                neighbor.setHeuristicDistanceFromGoal(f);
                openList.add(neighbor);
            }
        }
    }

    private Path reconstructPath(NavNode node, NavNode startNode) {
        Path path = new Path();
        while (!(node.getPreviousNode() == null)) {
            path.addNode(node.getPosition());
            node = node.getPreviousNode();
        }
        path.addNode(startNode.getPosition());
        return path;
    }

    private float getEstimatedDistanceToGoal(NavNode start, NavNode goal) {
        return (float) start.getPosition().distanceTo(goal.getPosition());
    }

    private void removeNodeFromNeighbors(NavNode node) {
        for (int i = node.getNeighbors().size() - 1; i >= 0; i--) {
            NavNode other = node.getNeighbors().get(i);
            other.removeLastAdded();
        }
    }

    private void findNeighborsForNode(NavNode node, List<Tile> tileList, float pixel) {
        // add neighbors to startNode
        for (NavNode other : this.navPoints) {
            // create raycast
            Hitbox raycast = new Hitbox(0, 0);
            raycast.addPoint(node.getPosition());
            raycast.addPoint(other.getPosition());

            // check for colliding polys
            boolean canSeeTarget = true;
            for (Tile block : tileList) {
                if (CollisionHelper.findIntersection(raycast, this.expandHitbox(block.getHitbox(), pixel)) != null) {
                    canSeeTarget = false;
                    break;
                }
            }
            if (canSeeTarget) {
                node.addNeighbor(other);
                other.addNeighbor(node);
            }
        }
    }

    private Hitbox expandHitbox(Hitbox original, float pixel) {
        Hitbox hitbox = original.clone();
        hitbox.scaleByPixel(pixel);
        return hitbox;
    }
}
