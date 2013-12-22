package de.gemo.pathfinding;

import java.awt.*;
import java.util.*;

import de.gemo.game.physics.*;
import de.gemo.game.sim.core.*;

public class PathRunnable implements Runnable {

	private AStar star = null;
	private ArrayList<Point> path = null;
	private Point start, goal;
	private boolean pathFound = false;
	private boolean searchDone = false;
	private PathFinishListener listener = null;

	public PathRunnable(AreaMap areaMap, Point start, Point goal, PathFinishListener listener) {
		this.star = new AStar(areaMap, new DiagonalHeuristic(), false);
		this.start = start;
		this.goal = goal;
		this.listener = listener;
	}

	@Override
	public synchronized void run() {
		// init search
		this.searchDone = false;
		this.pathFound = false;
		// calculate path
		this.path = star.calcShortestPath(this.start.x, this.start.y, this.goal.x, this.goal.y);
		// end search
		this.pathFound = (this.path != null && this.path.size() > 0);
		this.searchDone = true;
		if (this.listener != null) {
			if (this.pathFound) {
				this.listener.onSearchSuccessful(start, goal, path);
			} else {
				this.listener.onSearchUnsuccessful(start, goal);
			}
		}
	}

	public synchronized boolean isSearchDone() {
		return searchDone;
	}

	public synchronized boolean isPathFound() {
		return pathFound;
	}

}
