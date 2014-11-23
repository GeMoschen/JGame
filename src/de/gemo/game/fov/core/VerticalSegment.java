package de.gemo.game.fov.core;

import de.gemo.gameengine.units.*;

public class VerticalSegment {

    private final Vector3f point1, point2;

    public VerticalSegment(Vector3f point1, Vector3f point2) {
        super();
        this.point1 = point1;
        this.point2 = point2;
    }

    public Vector3f getPoint1() {
        return point1;
    }

    public Vector3f getPoint2() {
        return point2;
    }
}
