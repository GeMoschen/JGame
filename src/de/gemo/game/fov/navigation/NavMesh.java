package de.gemo.game.fov.navigation;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

import de.gemo.game.fov.units.*;
import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.units.*;
import de.gemo.gameengine.units.quadtree.*;
import de.gemo.gameengine.units.quadtree.Point;

public class NavMesh {
    private List<NavNode> navPoints;
    private QuadTree<NavNode> tree = new QuadTree<NavNode>(-100, -100, GameEngine.INSTANCE.VIEW_WIDTH + 100, GameEngine.INSTANCE.VIEW_HEIGHT + 100);

    public NavMesh(List<Tile> tileList) {
        this.navPoints = new ArrayList<NavNode>();
        this.createNavMesh(tileList);
    }

    public void render() {
        for (NavNode node : this.navPoints) {
            node.render();
        }

        // int xmin = (int) (MouseManager.INSTANCE.getMouseVector().getX() -
        // 250);
        // int xmax = (int) (MouseManager.INSTANCE.getMouseVector().getX() +
        // 250);
        // int ymin = (int) (MouseManager.INSTANCE.getMouseVector().getY() -
        // 250);
        // int ymax = (int) (MouseManager.INSTANCE.getMouseVector().getY() +
        // 250);
        //
        // glPushMatrix();
        // {
        // System.out.println("-------------------------");
        // long startTime = System.nanoTime();
        // List<Point<NavNode>> points = this.tree.searchIntersect(xmin, ymin,
        // xmax, ymax);
        // long duration = System.nanoTime() - startTime;
        // float ms = duration / 1000000f;
        // System.out.println("MS: " + ms);
        //
        // long startTime2 = System.nanoTime();
        // for (NavNode node : this.navPoints) {
        // if
        // (node.getPosition().getDistance(MouseManager.INSTANCE.getMouseVector())
        // > 250) {
        // continue;
        // }
        // }
        // long duration2 = System.nanoTime() - startTime2;
        // float ms2 = duration2 / 1000000f;
        // System.out.println("MS 2: " + ms2);
        //
        // glDisable(GL_LIGHTING);
        // glDisable(GL_TEXTURE_2D);
        // glLineWidth(1f);
        //
        // glEnable(GL_BLEND);
        // glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //
        // glColor4f(1, 0, 0, 1f);
        // for (Point<NavNode> point : points) {
        // glBegin(GL_LINE_LOOP);
        // {
        // glVertex2d(point.getX() - 2, point.getY() - 2);
        // glVertex2d(point.getX() - 2, point.getY() + 2);
        // glVertex2d(point.getX() + 2, point.getY() + 2);
        // glVertex2d(point.getX() + 2, point.getY() - 2);
        // }
        // glEnd();
        // }
        // }
        // glPopMatrix();
    }

    public Path path = null;

    public void createNavMesh(List<Tile> tileList) {
        System.out.println("CREATING NAVMESH...");
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
        // this.stressTest(tileList);
    }

    private void stressTest(List<Tile> tileList) {

        System.out.println("STRESSTEST....");
        float x = (float) (Math.random() * 1024);
        float y = (float) (Math.random() * 768);

        int tests = 1;
        System.out.println("POINTS: " + this.navPoints.size());
        System.out.println("Tree: " + this.tree.getKeys().size());
        NavNode node = new NavNode(new Vector3f(x, y, 0));
        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            this.findNeighborsForNode(node, tileList, false);
        }
        long duration = System.nanoTime() - start;
        float ms = duration / 1000000f;
        System.out.println("OLD: " + ms);

        long start2 = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            this.findNeighborsForNode2(node, tileList, false);
        }
        long duration2 = System.nanoTime() - start2;
        float ms2 = duration2 / 1000000f;
        System.out.println("NEW: " + ms2);
        System.exit(0);
    }

    private void findNeighbours(List<Tile> tileList) {
        System.out.println("FINDING NEIGHBOURS...");
        NavNode current, other;
        for (int i = 0; i < this.navPoints.size(); i++) {
            current = this.navPoints.get(i);

            this.tree.set(current.getPosition().getX(), current.getPosition().getY(), current);

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
        this.findNeighborsForNode2(startNode, tileList, false);
        long duration_1 = System.nanoTime() - startTime_1;
        float dur_1 = duration_1 / 1000000f;

        long startTime_2 = System.nanoTime();
        this.findNeighborsForNode2(goalNode, tileList, true);
        long duration_2 = System.nanoTime() - startTime_2;
        float dur_2 = duration_2 / 1000000f;

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

    private void findNeighborsForNode2(NavNode node, List<Tile> tileList, boolean useExpanded) {
        // add neighbors to startNode
        // List<Hitbox> expanded = new ArrayList<Hitbox>();
        // for (Tile block : tileList) {
        // expanded.add(block.expanded);
        // }

        int xmin = (int) (node.getPosition().getX() - 250);
        int xmax = (int) (node.getPosition().getX() + 250);
        int ymin = (int) (node.getPosition().getY() - 250);
        int ymax = (int) (node.getPosition().getY() + 250);

        // List<NavNode> points = new ArrayList<NavNode>();
        long start = System.nanoTime();
        List<Point<NavNode>> points = this.tree.searchWithin(xmin, ymin, xmax, ymax);
        long d = System.nanoTime() - start;
        float search = d / 1000000f;
        System.out.println("MS: " + search);

        // create raycast
        Hitbox raycast = new Hitbox(0, 0);
        raycast.addPoint(node.getPosition());
        float total = 0;
        for (Point<NavNode> o : points) {
            NavNode other = o.getValue();

            // create raycast
            if (raycast.getPointCount() > 1) {
                raycast.getPoints().remove(1);
            }
            raycast.addPoint(other.getPosition());

            // check for colliding polys
            boolean canSeeTarget = true;
            start = System.nanoTime();

            for (Tile block : tileList) {
                if (CollisionHelper.isIntersecting(block.expanded, raycast)) {
                    canSeeTarget = false;
                    break;
                }
            }
            d = System.nanoTime() - start;
            float m = d / 1000000f;
            total += m;
            if (canSeeTarget) {
                node.addNeighbor(other);
                other.addNeighbor(node);
            }
        }
        System.out.println("Raycast: " + total);
        System.out.println("All: " + (total + search));
    }

    public boolean testIntersect(Hitbox box, Hitbox raycast) {
        Polygon p = new Polygon();
        for (Vector3f v : box.getPoints()) {
            p.addPoint((int) v.getX(), (int) v.getY());
        }

        final Line2D.Double line = new Line2D.Double(raycast.getPoint(0).getX(), raycast.getPoint(0).getY(), raycast.getPoint(1).getX(), raycast.getPoint(1).getY());
        Set<Point2D> intersections;
        try {
            intersections = getIntersections(p, line);
            return intersections.size() > 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static Set<Point2D> getIntersections(final Polygon poly, final Line2D.Double line) throws Exception {

        final PathIterator polyIt = poly.getPathIterator(null); // Getting an
                                                                // iterator
                                                                // along the
                                                                // polygon path
        final double[] coords = new double[6]; // Double array with length 6
                                               // needed by iterator
        final double[] firstCoords = new double[2]; // First point (needed for
                                                    // closing polygon path)
        final double[] lastCoords = new double[2]; // Previously visited point
        final Set<Point2D> intersections = new HashSet<Point2D>(); // List to
                                                                   // hold found
                                                                   // intersections
        polyIt.currentSegment(firstCoords); // Getting the first coordinate pair
        lastCoords[0] = firstCoords[0]; // Priming the previous coordinate pair
        lastCoords[1] = firstCoords[1];
        polyIt.next();
        while (!polyIt.isDone()) {
            final int type = polyIt.currentSegment(coords);
            switch (type) {
            case PathIterator.SEG_LINETO: {
                final Line2D.Double currentLine = new Line2D.Double(lastCoords[0], lastCoords[1], coords[0], coords[1]);
                if (currentLine.intersectsLine(line))
                    intersections.add(getIntersection(currentLine, line));
                lastCoords[0] = coords[0];
                lastCoords[1] = coords[1];
                break;
            }
            case PathIterator.SEG_CLOSE: {
                final Line2D.Double currentLine = new Line2D.Double(coords[0], coords[1], firstCoords[0], firstCoords[1]);
                if (currentLine.intersectsLine(line))
                    intersections.add(getIntersection(currentLine, line));
                break;
            }
            default: {
                throw new Exception("Unsupported PathIterator segment type.");
            }
            }
            polyIt.next();
        }
        return intersections;

    }

    public static Point2D getIntersection(final Line2D.Double line1, final Line2D.Double line2) {

        final double x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = line1.x1;
        y1 = line1.y1;
        x2 = line1.x2;
        y2 = line1.y2;
        x3 = line2.x1;
        y3 = line2.y1;
        x4 = line2.x2;
        y4 = line2.y2;
        final double x = ((x2 - x1) * (x3 * y4 - x4 * y3) - (x4 - x3) * (x1 * y2 - x2 * y1)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        final double y = ((y3 - y4) * (x1 * y2 - x2 * y1) - (y1 - y2) * (x3 * y4 - x4 * y3)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));

        return new Point2D.Double(x, y);

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
            if (other.getPosition().getDistance(node.getPosition()) > 240) {
                continue;
            }

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
