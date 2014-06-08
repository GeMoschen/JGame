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
        List<NavNode> middleNodes = new ArrayList<NavNode>();
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
                    NavNode middleNode = new NavNode(new Vector3f(current.getPosition().getX() + (float) (other.getPosition().getX() - current.getPosition().getX()) / 2f, current.getPosition().getY() + (float) (other.getPosition().getY() - current.getPosition().getY()) / 2f, 0f));
                    middleNode.addNeighbor(current);
                    middleNode.addNeighbor(other);
                    current.addNeighbor(middleNode);
                    other.addNeighbor(middleNode);
                    middleNodes.add(middleNode);
                }
            }
        }
        // this.navPoints.addAll(middleNodes);
    }

    public Path findPath(Vector3f start, Vector3f goal, List<Tile> tileList) {
        // reset heuristics
        for (NavNode node : this.navPoints) {
            node.reset();
        }

        // create start and goal
        NavNode startNode = new NavNode(start);
        NavNode goalNode = new NavNode(goal);

        // add temporary connections
        this.findNeighborsForNode(startNode, tileList);
        this.findNeighborsForNode(goalNode, tileList);

        // create raycast from start to goal
        Hitbox raycast = new Hitbox(0, 0);
        raycast.addPoint(startNode.getPosition());
        raycast.addPoint(goalNode.getPosition());

        // check for colliding polys
        boolean canSeeTarget = true;
        for (Tile block : tileList) {
            if (CollisionHelper.findIntersection(raycast, block.getHitbox()) != null) {
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
        while (!openList.isEmpty()) {
            // get the first Node from non-searched Node list, sorted by lowest
            // distance from our goal as guessed by our heuristic
            NavNode current = openList.poll();

            // check if our current Node location is the goal Node. If it is, we
            // are done.
            if (current.equals(goalNode)) {
                // remove temporary connections
                this.removeNodeFromNeighbors(startNode);
                this.removeNodeFromNeighbors(goalNode);

                // reconstruct path
                return reconstructPath(current, startNode);
            }

            // move current Node to the closed (already searched) list
            closedList.add(current);

            // go through all the current Nodes neighbors and calculate if one
            // should be our next step
            for (NavNode neighbor : current.getNeighbors()) {
                boolean neighborIsBetter;

                // if we have already searched this Node, don't bother and
                // continue to the next one
                if (closedList.contains(neighbor))
                    continue;

                // calculate how long the path is if we choose this neighbor
                // as the next step in the path
                float neighborDistanceFromStart = (float) (current.getDistanceFromStart() + goalNode.getPosition().distanceTo(neighbor.getPosition()));

                // add neighbor to the open list if it is not there
                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                    neighborIsBetter = true;
                    // if neighbor is closer to start it could also be
                    // better
                } else if (neighborDistanceFromStart < current.getDistanceFromStart()) {
                    neighborIsBetter = true;
                } else {
                    neighborIsBetter = false;
                }
                // set neighbors parameters if it is better
                if (neighborIsBetter) {
                    // remove from openlist
                    openList.remove(neighbor);

                    // update neighbor
                    neighbor.setPreviousNode(current);
                    neighbor.setDistanceFromStart(neighborDistanceFromStart);
                    neighbor.setHeuristicDistanceFromGoal(this.getEstimatedDistanceToGoal(neighbor, goalNode));

                    // add to openlist
                    openList.add(neighbor);
                }
            }
        }

        // remove temporary connections
        this.removeNodeFromNeighbors(startNode);
        this.removeNodeFromNeighbors(goalNode);
        return null;
    }

    private Path reconstructPath(NavNode node, NavNode startNode) {
        Path path = new Path();
        while (!(node.getPreviousNode() == null)) {
            NavNode newNode = new NavNode(node.getPosition().clone());
            path.addNode(newNode);
            node = node.getPreviousNode();
        }
        path.addNode(startNode);
        return path;
    }

    private float getEstimatedDistanceToGoal(NavNode start, NavNode goal) {
        start.getPosition();
        // float dx = goal.getPosition().getX() - start.getPosition().getX();
        // float dy = goal.getPosition().getY() - start.getPosition().getY();
        //
        // float result = (float) (Math.sqrt((dx * dx) + (dy * dy)));
        return Vector3f.sub(start.getPosition(), goal.getPosition()).getLength();
        // // return (start.getPosition().getDistance(goal.getPosition()));

        // float dist = Math.abs(start.getPosition().getX() -
        // goal.getPosition().getX()) + Math.abs(start.getPosition().getY() -
        // goal.getPosition().getY());
        // float p = (1 / 10000);
        // dist *= (1.0 + p);
        // return dist;
    }

    private void removeNodeFromNeighbors(NavNode node) {
        for (int i = node.getNeighbors().size() - 1; i >= 0; i--) {
            NavNode other = node.getNeighbors().get(i);
            other.removeLastAdded();
        }
    }

    private void findNeighborsForNode(NavNode node, List<Tile> tileList) {
        // add neighbors to startNode
        for (NavNode other : this.navPoints) {
            // create raycast
            Hitbox raycast = new Hitbox(0, 0);
            raycast.addPoint(node.getPosition());
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
                node.addNeighbor(other);
                other.addNeighbor(node);
            }
        }
    }

    private Hitbox expandHitbox(Hitbox original, int pixel) {
        Hitbox hitbox = original.clone();
        hitbox.scaleByPixel(pixel);
        return hitbox;
    }
}
