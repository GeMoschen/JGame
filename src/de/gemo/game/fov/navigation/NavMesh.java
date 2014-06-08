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

    public void render() {
        for (NavNode node : this.navPoints) {
            node.render();
        }
    }

    public Path path = null;

    public void createNavMesh(List<Tile> tileList) {
        // clear old navpoints
        this.navPoints.clear();

        // add all available points
        for (Tile tile : tileList) {
            Hitbox hitbox = this.expandHitbox(tile.getHitbox(), 10);
            for (Vector3f vector : hitbox.getPoints()) {
                boolean isInsidePolygon = false;
                for (Tile checkTile : tileList) {
                    if (tile == checkTile) {
                        continue;
                    }

                    Hitbox otherHitbox = checkTile.expanded;
                    if (CollisionHelper.isVectorInHitbox(vector, otherHitbox)) {
                        isInsidePolygon = true;
                        break;
                    }
                }
                if (!isInsidePolygon) {
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
                    if (CollisionHelper.isIntersecting(block.expanded, raycast)) {
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

        System.out.println();
        System.out.println("SEARCH STARTED");
        long startTime = System.nanoTime();

        // reset heuristics
        for (NavNode node : this.navPoints) {
            node.reset();
        }

        // create start and goal
        NavNode startNode = new NavNode(start);
        NavNode goalNode = new NavNode(goal);

        // add temporary connections
        long startTime_1 = System.nanoTime();
        this.findNeighborsForNode(startNode, tileList, false);
        long duration_1 = System.nanoTime() - startTime_1;
        float dur_1 = duration_1 / 1000000f;
        System.out.println("Initialize 1: " + dur_1);

        long startTime_2 = System.nanoTime();
        this.findNeighborsForNode(goalNode, tileList, true);
        long duration_2 = System.nanoTime() - startTime_2;
        float dur_2 = duration_2 / 1000000f;
        System.out.println("Initialize 2: " + dur_2);

        System.out.println("Initialize: " + (dur_1 + dur_2));

        // create raycast from start to goal
        Hitbox raycast = new Hitbox(0, 0);
        raycast.addPoint(startNode.getPosition());
        raycast.addPoint(goalNode.getPosition());

        long startTime_3 = System.nanoTime();
        // check for colliding polys
        boolean canSeeTarget = true;
        for (Tile block : tileList) {
            if (CollisionHelper.isIntersecting(this.expandHitbox(block.getHitbox(), 10), raycast)) {
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
            // get the first node on the openlist
            currentNode = openList.poll();

            // check if our current Node location is the goal Node. If it is, we
            // are done.
            if (currentNode.equals(goalNode)) {
                // remove temporary connections
                this.removeNodeFromNeighbors(startNode);
                this.removeNodeFromNeighbors(goalNode);

                long duration_3 = System.nanoTime() - startTime_3;
                float dur_3 = duration_3 / 1000000f;
                System.out.println("Search: " + dur_3);

                long duration = System.nanoTime() - startTime;
                float dur = duration / 1000000f;
                System.out.println("Complete Pathfinding: " + dur);

                // reconstruct path
                return reconstructPath(currentNode, startNode);
            }

            // add this node to the closedlist
            closedList.add(currentNode);
            this.expandNode(currentNode, goalNode, openList, closedList);
        }

        long duration_3 = System.nanoTime() - startTime_3;
        float dur_3 = duration_3 / 1000000f;
        System.out.println("Search (NOT): " + dur_3);

        // remove temporary connections
        this.removeNodeFromNeighbors(startNode);
        this.removeNodeFromNeighbors(goalNode);

        return null;
    }

    private void expandNode(NavNode currentNode, NavNode goalNode, PriorityQueue<NavNode> openList, Set<NavNode> closedList) {

        for (NavNode neighbor : currentNode.getNeighbors()) {
            // continue, if we have already visited this node
            if (closedList.contains(neighbor)) {
                continue;
            }
            // g = get the cost from the start to the next node
            float g = (float) (currentNode.getDistanceFromStart() + neighbor.getPosition().distanceTo(currentNode.getPosition()));

            // if the neighbor is on the openlist AND the new costs are bigger
            // that the costs from the neighbor to the start: ignore this node
            if (openList.contains(neighbor) && g > neighbor.getDistanceFromStart()) {
                continue;
            }

            // update the neighbor
            neighbor.setPreviousNode(currentNode);
            neighbor.setDistanceFromStart(g);

            // f = get the costs from the start to the current point + the
            // estimated costs from the neighbor to the goal
            float f = g + this.getEstimatedDistanceToGoal(neighbor, goalNode);
            neighbor.setHeuristicDistanceFromGoal(f);
            if (!openList.contains(neighbor)) {
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

    private void findNeighborsForNode(NavNode node, List<Tile> tileList, boolean useExpanded) {
        // add neighbors to startNode
        List<Hitbox> expanded = new ArrayList<Hitbox>();
        // if (useExpanded) {
        for (Tile block : tileList) {
            expanded.add(block.expanded);
        }
        // } else {
        // for (Tile block : tileList) {
        // Hitbox clone = block.getHitbox().clone();
        // clone.scaleByPixel(7);
        // expanded.add(block.getHitbox());
        // }
        // }

        // create raycast
        Hitbox raycast = new Hitbox(0, 0);
        raycast.addPoint(node.getPosition());
        for (NavNode other : this.navPoints) {
            // if (other.getPosition().getDistance(node.getPosition()) > 240) {
            // continue;
            // }

            // create raycast
            if (raycast.getPointCount() > 1) {
                raycast.getPoints().remove(1);
            }
            raycast.addPoint(other.getPosition());

            // check for colliding polys
            boolean canSeeTarget = true;
            for (Hitbox exp : expanded) {
                if (CollisionHelper.isIntersecting(exp, raycast)) {
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
