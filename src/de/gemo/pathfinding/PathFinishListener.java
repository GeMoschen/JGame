package de.gemo.pathfinding;

import java.awt.*;
import java.util.*;

public interface PathFinishListener {

	public void onSearchUnsuccessful(Point start, Point goal);

	public void onSearchSuccessful(Point start, Point goal, ArrayList<Point> path);
}
