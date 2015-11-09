package de.gemo.game.polygon.entities;

import clipper.*;
import clipper.internal.PolyType;
import de.gemo.gameengine.collision.CollisionHelper;
import de.gemo.gameengine.collision.Hitbox;
import de.gemo.gameengine.collision.Line;
import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.units.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class ViewField {

    private final static int NEAR = 100;
    private final static int FAR = 300;

    private final Hitbox _nearView;
    private final Hitbox _farView;
    private Hitbox _constructedHitbox;
    private List<Vector3f[]> _obstaclePolygons;

    private boolean _canSeeTarget = false;

    public ViewField(final Vector3f position) {
        _nearView = createHitbox(position.clone(), NEAR);
        _farView = createHitbox(position.clone(), FAR);
        _obstaclePolygons = new ArrayList<>();
        _constructedHitbox = constructHitbox();
    }

    public Vector3f getLocation() {
        return _farView.getCenter();
    }

    private Hitbox createHitbox(Vector3f location, final int distance) {
        final Hitbox hitbox = new Hitbox(location);
        int points = 9;
        float maxAngle = 42f;
        float stepAngle = maxAngle / (points - 1);
        float halfAngle = maxAngle / 2f;

        hitbox.rotate(-halfAngle);
        hitbox.addPoint(0, 0);
        hitbox.addPoint(0, -distance);
        for (int i = 0; i < points - 1; i++) {
            hitbox.rotate(stepAngle);
            hitbox.addPoint(0, -distance);
        }
        return hitbox;
    }


    public void updatePosition(final int delta) {
        if (!_canSeeTarget) {
            _nearView.rotate(0.25f);
            _farView.setAngle(_nearView.getAngle());
        }
    }

    public void updateCollisions(final List<Hitbox> obstacles) {
        // calculate obstacles for rendering
        _obstaclePolygons = calculateObstacles(_farView, obstacles);

        // construct hitbox
        _constructedHitbox = constructHitbox();

        _canSeeTarget = canSeeTarget(GameEngine.$.getMouseManager().getHitBox());

        // center on target
        if (_canSeeTarget) {
            _nearView.setAngle(GameEngine.$.getMouseManager().getMouseVector().getAngle(_farView.getCenter()));
            _farView.setAngle(_nearView.getAngle());
        }
    }


    private boolean canSeeTarget(final Hitbox hitbox) {
        for (final Vector3f vector : hitbox.getPoints()) {
            if (canSeeTarget(vector)) {
                return true;
            }
        }
        return false;
    }

    private boolean canSeeTarget(final Vector3f vector) {
        if (Math.abs(vector.distanceTo(_farView.getCenter())) > FAR || !CollisionHelper.isVectorInHitbox(vector, _constructedHitbox)) {
            return false;
        }
        return true;
    }

    private Hitbox constructHitbox() {
        final float scaleFactor = 10000f;
        final Hitbox hitbox = new Hitbox(0, 0);

        // create original polygon
        List<Polygon> originalPolygon = new ArrayList<>();
        final Polygon poly = new Polygon();
        for (final Vector3f vector : _farView.getPoints()) {
            poly.add((int) (vector.getX() * scaleFactor), (int) (vector.getY() * scaleFactor));
        }
        originalPolygon.add(poly);

        // create clipping polygons
        final List<Polygon> polygonsToClip = new ArrayList<>();
        for (final Vector3f[] vectors : _obstaclePolygons) {
            final Polygon clipPolygon = new Polygon();
            for (final Vector3f vector : vectors) {
                clipPolygon.add((int) (vector.getX() * scaleFactor), (int) (vector.getY() * scaleFactor));
            }
            polygonsToClip.add(clipPolygon);
        }

        // calculate difference
        final List<Polygon> differencePolygons = new ArrayList<>();
        Clipper c = new Clipper();
        c.addPolygons(originalPolygon, PolyType.ptSubject);
        c.addPolygons(polygonsToClip, PolyType.ptClip);
        try {
            c.execute(ClipType.DIFFERENCE, differencePolygons, PolyFillType.NEGATIVE, PolyFillType.POSITIVE);
            // create hitbox
            for (final Polygon polygon : differencePolygons) {
                for (final IntPoint point : polygon.getPoints()) {
                    hitbox.addPoint((int) point.x / scaleFactor, (int) point.y / scaleFactor);
                }
            }
        }catch(final Exception e) {
e.printStackTrace();
        }



        return hitbox;
    }

    public List<Vector3f[]> calculateObstacles(final Hitbox hitbox, final List<Hitbox> obstacles) {
        final List<Vector3f[]> result = new ArrayList<>();
        for (final Hitbox obstacle : obstacles) {
            if (CollisionHelper.isColliding(hitbox, obstacle)) {
                for (final Line line : obstacle.getLines()) {
                    if (line.isInFront(hitbox.getCenter())) {
                        final double scaleFactor = 1d / (Math.min(Math.abs(hitbox.getCenter().getDistance(line.getFirst())), Math.abs(hitbox.getCenter().getDistance(line.getLast()))) / (FAR * 5d));
                        result.add(new Vector3f[]{line.getFirst(), line.getLast(), scaleRelative(line.getLast(), hitbox.getCenter(), (float) scaleFactor), scaleRelative(line.getFirst(), hitbox.getCenter(), (float) scaleFactor)});
                    }
                }
            }
        }
        return result;
    }

    public void render() {
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);

        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glStencilFunc(GL_NEVER, 1, 0xFF);
        glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP);  // draw 1s on test fail (always)

        // initial: render stencil pattern
        glStencilMask(0xFF);
        glClear(GL_STENCIL_BUFFER_BIT);  // needs mask=0xFF

        glColor4f(1, 0, 0, 0.5f);
        for (final Vector3f[] vectors : _obstaclePolygons) {
            glBegin(GL_POLYGON);
            for (final Vector3f vector : vectors) {
                vector.render();
            }
            glEnd();
        }
        glStencilMask(0x00);

        // first: render the near-view
        glStencilFunc(GL_EQUAL, 0, 0xFF);
        renderHitbox(_nearView, 0.75f);

        // second: add the near-view to the stencil, so it will be removed from the far-view
        glStencilMask(0xFF);
        glStencilFunc(GL_NEVER, 1, 0xFF);
        renderHitbox(_nearView, 1f);

        // third: render the far-view
        glStencilFunc(GL_EQUAL, 0, 0xFF);
        glStencilMask(0x00);
        renderHitbox(_farView, 0.35f);

        // last: render outside of the mask
        glStencilFunc(GL_EQUAL, 1, 0xFF);

        glDisable(GL_STENCIL_TEST);

        // last: normal render -> outline
        renderOutline();
    }

    private final void renderOutline() {
        if (_canSeeTarget) {
            glColor4f(1, 0, 0, 0.5f);
        } else {
            glColor4f(0, 1, 0, 0.5f);
        }
        glBegin(GL_LINE_LOOP);
        for (final Vector3f vector : _constructedHitbox.getPoints()) {
            vector.render();
        }
        glEnd();
    }

    private final void renderHitbox(final Hitbox hitbox, final float colorValue) {
        if (_canSeeTarget) {
            glColor4f(colorValue, 0, 0, 0.5f);
        } else {
            glColor4f(0, colorValue, 0, 0.5f);
        }
        glBegin(GL_POLYGON);
        for (final Vector3f vector : hitbox.getPoints()) {
            vector.render();
        }
        glEnd();
    }

    private Vector3f scaleRelative(final Vector3f vector, final Vector3f relativeVector, final float factor) {
        float dX = vector.getX() - relativeVector.getX();
        float dY = vector.getY() - relativeVector.getY();
        float dZ = vector.getZ() - relativeVector.getZ();
        return new Vector3f(relativeVector.getX() + dX * factor, relativeVector.getY() + dY * factor, relativeVector.getZ() + dZ * factor);
    }

    public void move(float x, float y) {
        moveHitbox(_nearView, x, y);
        moveHitbox(_farView, x, y);
    }

    private void moveHitbox(final Hitbox hitbox, final float x, final float y) {
        hitbox.move(x, y);
    }

    public void setAngle(final float angle) {
        _nearView.setAngle(angle);
        _farView.setAngle(_nearView.getAngle());
    }
}
