package de.gemo.game.jbox2d.tests;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

class RayCastAnyCallback implements RayCastCallback {
    boolean m_hit;
    Vec2 startPoint, endPoint;
    Vec2 m_normal;

    public void init(Vec2 startPoint, Vec2 endPoint) {
        this.m_hit = false;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
        this.m_hit = true;
        this.m_normal = normal;
        this.endPoint = point;
        return fraction;
    }
}