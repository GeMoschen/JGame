package de.gemo.game.fov.navigation;

import de.gemo.game.fov.core.TimeHandler;
import de.gemo.game.fov.units.Tile;
import de.gemo.gameengine.collision.CollisionHelper;
import de.gemo.gameengine.collision.Hitbox;
import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.units.Vector3f;
import de.gemo.gameengine.units.quadtree.Point;
import de.gemo.gameengine.units.quadtree.QuadTree;

import java.util.*;

public class NavMesh {
    private List<NavNode> navPoints;

    private List<Point<NavNode>> allPoints = new ArrayList<Point<NavNode>>();
    private QuadTree<NavNode> pointTree = new QuadTree<NavNode>(-100, -100, GameEngine.$.VIEW_WIDTH + 100, GameEngine.$.VIEW_HEIGHT + 100);
    private QuadTree<Tile> tileTree = new QuadTree<Tile>(-100, -100, GameEngine.$.VIEW_WIDTH + 100, GameEngine.$.VIEW_HEIGHT + 100);

    public NavMesh(List<Tile> tileList) {
        this.navPoints = new ArrayList<NavNode>();
        this.createNavMesh(tileList);
    }

    public void render() {
        for (NavNode node : this.navPoints) {
            node.render();
        }
    }

    public void createNavMesh(List<Tile> tileList) {
        System.out.println("CREATING NAVMESH...");
        // clear old navpoints
        this.navPoints.clear();

        // add tiles to tree
        for (Tile tile : tileList) {
            this.tileTree.set(tile.getHitbox().getCenter().getX(), tile.getHitbox().getCenter().getY(), tile);
        }

        // add all available _points
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
        this.buildNeighborList(tileList);
    }

    private void buildNeighborList(List<Tile> tileList) {
        System.out.println("BUILDING NEIGHBORS...");
        NavNode current, other;
        for (int i = 0; i < this.navPoints.size(); i++) {
            current = this.navPoints.get(i);

            this.pointTree.set(current.getPosition().getX(), current.getPosition().getY(), current);
            this.allPoints.add(new Point<NavNode>(current.getPosition().getX(), current.getPosition().getY(), current));

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
        TimeHandler.start("Pathfinding");

        // create start and goal
        NavNode startNode = new NavNode(start);
        NavNode goalNode = new NavNode(goal);

        // add temporary connections
        TimeHandler.start("Init");
        this.findNeighborsForNode(startNode, tileList, true);
        this.findNeighborsForNode(goalNode, tileList, true);
        TimeHandler.end("Init");

        TimeHandler.start("Init 2");
        // create raycast from start to goal
        Hitbox raycast = new Hitbox(0, 0);
        raycast.addPoint(startNode.getPosition());
        raycast.addPoint(goalNode.getPosition());

        // search tiles in boundingbox from the raycast
        List<Point<Tile>> tilesInRect = this.tileTree.searchWithin(raycast.getAABB().getLeft() - 20, raycast.getAABB().getTop() - 20, raycast.getAABB().getRight() + 20, raycast.getAABB().getBottom() + 20);
        boolean canSeeTarget = true;
        for (Point<Tile> b : tilesInRect) {
            Tile block = b.getValue();
            if (CollisionHelper.isIntersecting(block.expanded, raycast)) {
                canSeeTarget = false;
                break;
            }
        }
        // direct way to the goal, no A* needed
        if (canSeeTarget) {
            goalNode.addNeighbor(startNode);
            startNode.addNeighbor(goalNode);
            goalNode.setPreviousNode(startNode);
            this.removeNodeFromNeighbors(startNode);
            this.removeNodeFromNeighbors(goalNode);
            TimeHandler.end("Init 2");
            return this.reconstructPath(goalNode, startNode);
        }

        TimeHandler.end("Init 2");

        // initialize A*
        TimeHandler.start("Path");
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

                // reconstruct path
                return reconstructPath(currentNode, startNode);
            }

            // add this node to the closedlist
            closedList.add(currentNode);
            this.expandNode(currentNode, goalNode, openList, closedList);
        }

        TimeHandler.end("Path");
        // remove temporary connections
        this.removeNodeFromNeighbors(startNode);
        this.removeNodeFromNeighbors(goalNode);
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

    private void findNeighborsForNode(NavNode node, List<Tile> tileList, boolean useExpanded) {
        // init vars
        int minPointsToFind = 5;
        List<Point<NavNode>> points = new ArrayList<Point<NavNode>>();

        // find at least "minPointsToFind" Points in a radius around the current
        // node
        for (int size = 250; size < GameEngine.$.VIEW_WIDTH; size = size + 250) {
            int xmin = (int) (node.getPosition().getX() - 250);
            int xmax = (int) (node.getPosition().getX() + 250);
            int ymin = (int) (node.getPosition().getY() - 250);
            int ymax = (int) (node.getPosition().getY() + 250);
            points = this.pointTree.searchWithin(xmin, ymin, xmax, ymax);
            if (points.size() >= minPointsToFind) {
                break;
            }
        }

        // if we have too less _points, we simply add all _points
        if (points.size() < minPointsToFind) {
            points = this.allPoints;
        }

        // create raycast
        Hitbox raycast = new Hitbox(0, 0);
        for (Point<NavNode> o : points) {
            NavNode other = o.getValue();

            // create raycast
            // if (raycast.getPointCount() > 1) {
            raycast.getPoints().clear();
            // }
            raycast.addPoint(node.getPosition());
            raycast.addPoint(other.getPosition());

            // search tiles in boundingbox from the raycast
            List<Point<Tile>> tilesInRect = this.tileTree.searchWithin(raycast.getAABB().getLeft() - 20, raycast.getAABB().getTop() - 20, raycast.getAABB().getRight() + 20, raycast.getAABB().getBottom() + 20);

            // check for colliding tiles
            boolean canSeeTarget = true;
            for (Point<Tile> b : tilesInRect) {
                Tile block = b.getValue();
                if (useExpanded) {
                    if (CollisionHelper.isIntersecting(block.expanded, raycast)) {
                        canSeeTarget = false;
                        break;
                    }
                } else {
                    if (CollisionHelper.isIntersecting(block.getHitbox(), raycast)) {
                        canSeeTarget = false;
                        break;
                    }
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
