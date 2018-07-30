package de.gemo.gameengine.collision;

import java.awt.geom.*;

import de.gemo.gameengine.units.*;

public class Line {

    public static Point2D.Double getLineLineIntersection(final Line first, final Line other) {
        return getLineLineIntersection(first.getFirst().getX(), first.getFirst().getY(), first.getLast().getX(), first.getLast().getY(), other.getFirst().getX(), other.getFirst().getY(), other.getLast().getX(), other.getLast().getY());
    }

    public static Point2D.Double getLineLineIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double det1And2 = det(x1, y1, x2, y2);
        double det3And4 = det(x3, y3, x4, y4);
        double x1LessX2 = x1 - x2;
        double y1LessY2 = y1 - y2;
        double x3LessX4 = x3 - x4;
        double y3LessY4 = y3 - y4;
        double det1Less2And3Less4 = det(x1LessX2, y1LessY2, x3LessX4, y3LessY4);
        if (det1Less2And3Less4 == 0) {
            // the denominator is zero so the lines are parallel and there's
            // either no solution (or multiple solutions if the lines overlap)
            // so return null.
            return null;
        }
        double x = (det(det1And2, x1LessX2, det3And4, x3LessX4) / det1Less2And3Less4);
        double y = (det(det1And2, y1LessY2, det3And4, y3LessY4) / det1Less2And3Less4);
        return new Point2D.Double(x, y);
    }

    protected static double det(double a, double b, double c, double d) {
        return a * d - b * c;
    }

    public static boolean intersect(final Line first, final Line other) {
        return intersect(first.getFirst().getX(), first.getFirst().getY(), first.getLast().getX(), first.getLast().getY(), other.getFirst().getX(), other.getFirst().getY(), other.getLast().getX(), other.getLast().getY());
    }

    public static boolean intersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        // Return false if either of the lines have zero length
        if (x1 == x2 && y1 == y2 || x3 == x4 && y3 == y4) {
            return false;
        }
        // Fastest method, based on Franklin Antonio's
        // "Faster Line Segment Intersection" topic "in Graphics Gems III" book
        // (http://www.graphicsgems.org/)
        double ax = x2 - x1;
        double ay = y2 - y1;
        double bx = x3 - x4;
        double by = y3 - y4;
        double cx = x1 - x3;
        double cy = y1 - y3;

        double alphaNumerator = by * cx - bx * cy;
        double commonDenominator = ay * bx - ax * by;
        if (commonDenominator > 0) {
            if (alphaNumerator < 0 || alphaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (alphaNumerator > 0 || alphaNumerator < commonDenominator) {
                return false;
            }
        }
        double betaNumerator = ax * cy - ay * cx;
        if (commonDenominator > 0) {
            if (betaNumerator < 0 || betaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (betaNumerator > 0 || betaNumerator < commonDenominator) {
                return false;
            }
        }
        if (commonDenominator == 0) {
            // This code wasn't in Franklin Antonio's method. It was added by
            // Keith Woodward.
            // The lines are parallel.
            // Check if they're collinear.
            double y3LessY1 = y3 - y1;
            double collinearityTestForP3 = x1 * (y2 - y3) + x2 * (y3LessY1) + x3 * (y1 - y2); // see
                                                                                              // http://mathworld.wolfram.com/Collinear.html
            // If p3 is collinear with p1 and p2 then p4 will also be collinear,
            // since p1-p2 is parallel with p3-p4
            if (collinearityTestForP3 == 0) {
                // The lines are collinear. Now check if they overlap.
                if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4 || x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4 || x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2) {
                    if (y1 >= y3 && y1 <= y4 || y1 <= y3 && y1 >= y4 || y2 >= y3 && y2 <= y4 || y2 <= y3 && y2 >= y4 || y3 >= y1 && y3 <= y2 || y3 <= y1 && y3 >= y2) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    protected Vector3f _first;
    protected Vector3f _last;

    public Line(Vector3f first, Vector3f last) {
        _first = first;
        _last = last;
    }

    public boolean intersects(final Line line) {
        return intersect(this, line);
    }

    public Vector3f getFirst() {
        return _first;
    }

    public Line setFirst(Vector3f first) {
        _first = first;
        return this;
    }

    public Vector3f getLast() {
        return _last;
    }

    public Line setLast(Vector3f last) {
        _last = last;
        return this;
    }

    public Vector3f getDirectionVector() {
        return Vector3f.sub(_last, _first);
    }

    public void reverse() {
        final Vector3f clone = _first.clone();
        _first.set(_last.getX(), _last.getY(), _last.getZ());
        _last.set(clone.getX(), clone.getY(), clone.getZ());
    }

    public boolean isInFront(final Vector3f vector) {
        return ((_last.getX() - _first.getX()) * (vector.getY() - _first.getY()) - (_last.getY() - _first.getY()) * (vector.getX() - _first.getX())) < 0;
    }

    public boolean isBehind(final Vector3f vector) {
        return ((_last.getX() - _first.getX()) * (vector.getY() - _first.getY()) - (_last.getY() - _first.getY()) * (vector.getX() - _first.getX())) > 0;
    }

    public Vector3f getInwardNormal() {
        final float dX = _last.getX() - _first.getX();
        final float dY = _last.getY() - _first.getY();
        final float edgeLength = (float) Math.sqrt(dX * dX + dY * dY);
        return new Vector3f(-dY / edgeLength, dX / edgeLength, 0);
    }

    public Vector3f getOutwardNormal() {
        final Vector3f inward = getInwardNormal();
        inward.set(-inward.getX(), -inward.getY(), -inward.getZ());
        return inward;
    }

    public void render() {
        _first.render();
        _last.render();
    }

    public double getLength() {
        return Math.abs(_first.distanceTo(_last));
    }
}