package de.gemo.game.polygon.navigation;

import de.gemo.game.fov.core.TimeHandler;
import de.gemo.gameengine.collision.CollisionHelper;
import de.gemo.gameengine.collision.Hitbox;
import de.gemo.gameengine.units.Vector3f;
import de.gemo.gameengine.units.quadtree.Point;

import java.util.*;

public class NavMesh {
    private List<NavNode> _navNodes;

    private List<Point<NavNode>> _allPoints = new ArrayList<>();

    private List<Hitbox> _fullExpandedObstacles;
    private List<Hitbox> _expandedObstacles;

    public NavMesh(final List<Hitbox> obstacles) {
        _navNodes = new ArrayList<NavNode>();
        createNavMesh(obstacles);
    }

    public void render() {
        for (NavNode node : _navNodes) {
            node.render();
        }
    }

    public void createNavMesh(final List<Hitbox> obstacles) {
        System.out.println("CREATING NAVMESH...");
        TimeHandler.start("CREATION");

        _fullExpandedObstacles = new ArrayList<>();
        for (final Hitbox hitbox : obstacles) {
            _fullExpandedObstacles.add(expandHitbox(hitbox, 8f));
        }

        _expandedObstacles = new ArrayList<>();
        for (final Hitbox hitbox : obstacles) {
            _expandedObstacles.add(expandHitbox(hitbox, 7.999f));
        }

        for (int i = 0; i < _fullExpandedObstacles.size(); i++) {
            final Hitbox firstHitbox = _fullExpandedObstacles.get(i);
            for (final Vector3f point : firstHitbox.getPoints()) {
                boolean valid = true;
                for (int j = i + 1; j < _fullExpandedObstacles.size(); j++) {
                    final Hitbox secondHitbox = _fullExpandedObstacles.get(j);
                    if (CollisionHelper.isVectorInHitbox(point, secondHitbox)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    _navNodes.add(new NavNode(point));
                }
            }
        }

        buildNeighborList();
        removeUnneededNodes();

        TimeHandler.end("CREATION");
    }

    private void removeUnneededNodes() {
        for (int index = _navNodes.size() - 1; index >= 0; index--) {
            if (_navNodes.get(index).getNeighbors().isEmpty()) {
                _navNodes.remove(index);
            }
        }
    }

    private void buildNeighborList() {
        System.out.println("BUILDING NEIGHBORS...");
        TimeHandler.start("NEIGHBORS");
        for (int i = 0; i < _navNodes.size(); i++) {
            final NavNode firstNode = _navNodes.get(i);
            for (int j = i + 1; j < _navNodes.size(); j++) {
                final NavNode secondNode = _navNodes.get(j);
                final Hitbox raycast = new Hitbox(0, 0);
                raycast.addPoint(firstNode.getPosition());
                raycast.addPoint(secondNode.getPosition());

                boolean canSee = true;
                for (final Hitbox hitbox : _expandedObstacles) {
                    if (CollisionHelper.isColliding(raycast, hitbox)) {
                        canSee = false;
                        break;
                    }
                }

                if (canSee) {
                    firstNode.addNeighbor(secondNode);
                    secondNode.addNeighbor(firstNode);
                }
            }
        }
        TimeHandler.end("NEIGHBORS");
    }

    public Path findPath(Vector3f start, Vector3f goal) {
        TimeHandler.start("Pathfinding");

        // create start and goal
        NavNode startNode = new NavNode(start);
        NavNode goalNode = new NavNode(goal);

        // add temporary connections
        TimeHandler.start("Init");
        findNeighborsForNode(startNode);
        findNeighborsForNode(goalNode);
        TimeHandler.end("Init");

        TimeHandler.start("Init 2");
        // create raycast from start to goal
        final Hitbox raycast = new Hitbox(0, 0);
        raycast.addPoint(startNode.getPosition());
        raycast.addPoint(goalNode.getPosition());

        boolean canSeeTarget = true;
        for (final Hitbox hitbox : _fullExpandedObstacles) {
            if (CollisionHelper.isIntersecting(hitbox, raycast)) {
                canSeeTarget = false;
                break;
            }
        }
        // direct way to the goal, no A* needed
        if (canSeeTarget) {
            goalNode.addNeighbor(startNode);
            startNode.addNeighbor(goalNode);
            goalNode.setPreviousNode(startNode);
            removeNodeFromNeighbors(startNode);
            removeNodeFromNeighbors(goalNode);
            TimeHandler.end("Init 2");
            System.out.println("Direct seen");
            return reconstructPath(goalNode, startNode);
        }

        TimeHandler.end("Init 2");

        // initialize A*
        TimeHandler.start("Path");
        Set<NavNode> closedList = new HashSet<>();
        PriorityQueue<NavNode> openList = new PriorityQueue<>();
        openList.add(startNode);
        NavNode currentNode;
        while (!openList.isEmpty()) {
            // get the first node on the openlist
            currentNode = openList.poll();

            // check if our current Node location is the goal Node. If it is, we
            // are done.
            if (currentNode.equals(goalNode)) {
                // remove temporary connections
                removeNodeFromNeighbors(startNode);
                removeNodeFromNeighbors(goalNode);

                // reconstruct path
                return reconstructPath(currentNode, startNode);
            }

            // add this node to the closedlist
            closedList.add(currentNode);
            expandNode(currentNode, goalNode, openList, closedList);
        }

        TimeHandler.end("Path");
        // remove temporary connections
        removeNodeFromNeighbors(startNode);
        removeNodeFromNeighbors(goalNode);
        TimeHandler.end("Path");
        TimeHandler.end("Pathfinding");
        System.out.println("no path found");
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

            // updatePosition the neighbor
            neighbor.setPreviousNode(currentNode);
            neighbor.setDistanceFromStart(g);

            // f = get the costs from the start to the current point + the
            // estimated costs from the neighbor to the goal
            float f = g + getEstimatedDistanceToGoal(neighbor, goalNode);
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
        TimeHandler.end("Path");
        TimeHandler.end("Pathfinding");
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

    private void findNeighborsForNode(final NavNode node) {
        // create raycast
        Hitbox raycast = new Hitbox(0, 0);
        for (NavNode other : _navNodes) {
            // create raycast
            raycast.getPoints().clear();
            raycast.addPoint(node.getPosition());
            raycast.addPoint(other.getPosition());

            // check for colliding tiles
            boolean canSeeTarget = true;
            for (final Hitbox hitbox : _expandedObstacles) {
                if (CollisionHelper.isIntersecting(hitbox, raycast)) {
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

    private Hitbox expandHitbox(final Hitbox original, final float pixel) {
        final Hitbox hitbox = original.clone();
        hitbox.scaleByPixel(pixel);
        return hitbox;
    }
}
