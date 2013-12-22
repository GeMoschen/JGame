package de.gemo.pathfinding;

import java.awt.Point;
import java.util.ArrayList;

public class BresenhamsLine {
    public static ArrayList<Point> getPointsOnLine(Point p1, Point p2) {

        Point a = (Point) p1.clone();
        Point b = (Point) p2.clone();

        ArrayList<Point> pointsOnLine = new ArrayList<Point>();

        boolean steep = Math.abs(b.y - a.y) > Math.abs(b.x - a.x);

        if (steep) {
            // swap(a.x, a.y)
            int temp;
            temp = a.x;
            a.x = a.y;
            a.y = temp;
            // swap(b.x, b.y)
            temp = a.x;
            b.x = b.y;
            b.y = temp;
        }
        if (a.x > b.x) {
            // swap(a.x, b.x)
            int temp;
            temp = a.x;
            a.x = b.x;
            b.x = temp;
            // swap(a.y, b.y)
            temp = a.y;
            a.y = b.y;
            b.y = temp;
        }

        int deltaX = b.x - a.x;
        int deltaY = Math.abs(b.y - a.y);
        int error = deltaX / 2;

        int yStep;
        int y = a.y;
        if (a.y < b.y)
            yStep = 1;
        else
            yStep = -1;

        for (int x = a.x; x <= b.x; x++) {
            if (steep)
                pointsOnLine.add(new Point(y, x));
            else
                pointsOnLine.add(new Point(x, y));
            error = error - deltaY;
            if (error < 0) {
                y = y + yStep;
                error = error + deltaX;
            }
        }

        return pointsOnLine;

    }

}