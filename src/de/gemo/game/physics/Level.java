package de.gemo.game.physics;

import java.awt.*;
import java.util.*;

import de.gemo.pathfinding.*;

public class Level {

	private int dimX = 40, dimY = 30;
	private PathRectangle[][] rectangles;
	private EmptyRectangle emptyRectangle;

	public Level(int dimX, int dimY) {
		this.dimX = dimX;
		this.dimY = dimY;
		this.initLevel();
	}

	private void initLevel() {
		// EMPTY RECTANGLE
		this.emptyRectangle = new EmptyRectangle(this);

		// CREATE NODES
		this.rectangles = new PathRectangle[dimX][dimY];
		Random random = new Random();
		for (int y = 0; y < dimY; y++) {
			for (int x = 0; x < dimX; x++) {
				rectangles[x][y] = new PathRectangle(this, x, y);
				rectangles[x][y].setBlocked(random.nextDouble() < 0.05);
			}
		}

		// INIT NODES
		for (int y = 0; y < dimY; y++) {
			for (int x = 0; x < dimX; x++) {
				rectangles[x][y].initNeighbours();
			}
		}
	}

	public void renderLevel() {
		for (int y = 0; y < dimY; y++) {
			for (int x = 0; x < dimX; x++) {
				rectangles[x][y].render();
			}
		}
	}

	public PathRectangle getRectangle(int x, int y) {
		if (x > -1 && x < dimX && y > -1 && y < dimY) {
			return rectangles[x][y];
		}
		return emptyRectangle;
	}

	AreaMap areaMap = null;
	AStar star = null;

	public ArrayList<PathRectangle> getPath(PathRectangle start, PathRectangle goal) {
//		if (areaMap == null) {
			areaMap = new AreaMap(this);
			star = new AStar(areaMap, new DiagonalHeuristic());
//		}
		ArrayList<Point> list = star.calcShortestPath(start.getX(), start.getY(), goal.getX(), goal.getY());
		if (list != null) {
			ArrayList<PathRectangle> path = new ArrayList<PathRectangle>();
			for (Point point : list) {
				if (point.x != goal.getX() || point.y != goal.getY()) {
					path.add(this.getRectangle(point.x, point.y));
				}
			}
			return path;
		}
		return null;
	}

	public int getDimX() {
		return dimX;
	}

	public int getDimY() {
		return dimY;
	}

}
