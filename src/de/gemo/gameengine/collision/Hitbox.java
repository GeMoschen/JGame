package de.gemo.gameengine.collision;

import de.gemo.gameengine.units.Vector2f;
import de.gemo.gameengine.units.Vector3f;
import org.newdawn.slick.Color;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Hitbox {

    protected final AABB _aabb;
    protected final List<Vector3f> _points;
    protected final List<Line> _lines;
    protected final Vector3f _center;
    protected float _angle;

    public Hitbox(final Vector3f center) {
        _center = center;
        _aabb = new AABB();
        _points = new ArrayList<Vector3f>();
        _lines = new ArrayList<>();
    }

    public Hitbox(float x, float y) {
        this(new Vector3f(x, y, 0));
    }

    public static Hitbox createRectangle(final float halfWidth, final float halfHeight) {
        final Hitbox hitbox = new Hitbox(0, 0);
        hitbox.addPoint(-halfWidth, -halfHeight);
        hitbox.addPoint(+halfWidth, -halfHeight);
        hitbox.addPoint(+halfWidth, +halfHeight);
        hitbox.addPoint(-halfWidth, +halfHeight);
        return hitbox;
    }

    public static Hitbox createRectangle(final Vector2f center, final float halfWidth, final float halfHeight) {
        return createRectangle(center.getX(), center.getY(), halfWidth, halfHeight);
    }

    public static Hitbox createRectangle(final float x, final float y, final float halfWidth, final float halfHeight) {
        final Hitbox hitbox = new Hitbox(x, y);
        hitbox.addPoint(-halfWidth, -halfHeight);
        hitbox.addPoint(+halfWidth, -halfHeight);
        hitbox.addPoint(+halfWidth, +halfHeight);
        hitbox.addPoint(-halfWidth, +halfHeight);
        return hitbox;
    }

    public static Hitbox load(Vector3f baseVector, ArrayList<Vector3f> pointList) {
        Hitbox hitbox = new Hitbox(baseVector);
        for (Vector3f vector : pointList) {
            hitbox.addPoint(vector.getX(), vector.getY());
        }
        return hitbox;
    }

    public final List<Vector3f> getPoints() {
        return _points;
    }

    public List<Line> getLines() {
        return _lines;
    }

    public final int getPointCount() {
        return _points.size();
    }

    public final Vector3f getPoint(int index) {
        return _points.get(index);
    }

    public Vector3f addPoint(Vector3f vector) {
        return addPoint(vector.getX(), vector.getY());
    }

    public Vector3f addPoint(float x, float y) {
        Vector3f vector = new Vector3f(getCenter().getX() + x, getCenter().getY() + y, 0);
        if (_points.size() > 0) {
            // remove last line
            if (_points.size() > 1) {
                _lines.remove(_lines.size() - 1);
            }
            // create new lines
            final Vector3f lastVector = _points.get(_points.size() - 1);
            _lines.add(new Line(lastVector, vector));
            _lines.add(new Line(vector, _points.get(0)));
        }
        _points.add(vector);
        _aabb.addPoint(vector.getX(), vector.getY());
        return vector;
    }

    public final Vector3f calculatePoint(float x, float y) {
        return calculatePoint(x, y, 0);
    }

    public final Vector3f calculatePoint(float x, float y, float z) {
        return new Vector3f(getCenter().getX() + x, getCenter().getY() + y, getCenter().getZ() + z);
    }

    public final Vector3f getCenter() {
        return _center;
    }

    public final void setCenter(Vector2f vector) {
        setCenter(vector.getX(), vector.getY());
    }

    public final void setCenter(Vector3f vector) {
        setCenter(vector.getX(), vector.getY());
    }

    public final void setCenter(float x, float y) {
        float difX = x - _center.getX();
        float difY = y - _center.getY();
        move(difX, difY);
    }

    public final void move(float x, float y) {
        _center.move(x, y);
        _aabb.reset();
        for (Vector3f vector : _points) {
            vector.move(x, y);
            _aabb.addPoint(vector.getX(), vector.getY());
        }
    }

    public final void rotate(float angle) {
        _angle += angle;

        if (_angle < 0f) {
            _angle += 360f;
        }
        if (_angle > 360f) {
            _angle -= 360f;
        }

        float rad = (float) Math.toRadians(angle);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        _aabb.reset();
        for (Vector3f vector : _points) {
            vector.rotateAround(getCenter(), sin, cos);
            _aabb.addPoint(vector.getX(), vector.getY());
        }
    }

    public final void rotateAround(Vector3f center, float angle) {
        _angle += angle;

        if (_angle < 0f) {
            _angle += 360f;
        }
        if (_angle > 360f) {
            _angle -= 360f;
        }

        float rad = (float) Math.toRadians(angle);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        _center.rotateAround(center, sin, cos);
        _aabb.reset();
        for (Vector3f vector : _points) {
            vector.rotateAround(center, sin, cos);
            _aabb.addPoint(vector.getX(), vector.getY());
        }
    }

    public void setAngle(Vector3f center, float angle) {
        rotateAround(center, -_angle + angle);
    }

    public float getAngle() {
        return _angle;
    }

    public void setAngle(float angle) {
        rotate(-_angle + angle);
    }

    protected void renderCenter() {
        // render _center
        Color.red.bind();
        glBegin(GL_LINE_LOOP);
        glVertex3i(-2, -2, 0);
        glVertex3i(2, -2, 0);
        glVertex3i(+2, +2, 0);
        glVertex3i(-2, +2, 0);
        glEnd();
    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            // translate & render _center
            glPushMatrix();
            {
                glTranslatef(_center.getX(), _center.getY(), 0);
                renderCenter();
            }
            glPopMatrix();

            // render hitbox
            glPushMatrix();
            {
                Color.green.bind();
                glBegin(GL_LINE_LOOP);
                for (final Line line : getLines()) {
                    line.render();
                }
                glEnd();
            }
            glPopMatrix();

            // render AABB
            // _aabb.render();
            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    public void scale(float scaleX, float scaleY) {
        float currentAngle = _angle;
        setAngle(0);
        for (Vector3f vector : _points) {
            float currentX = vector.getX() - _center.getX();
            float currentY = vector.getY() - _center.getY();
            currentX *= scaleX;
            currentY *= scaleY;
            vector.setX(_center.getX() + currentX);
            vector.setY(_center.getY() + currentY);
        }
        setAngle(currentAngle);
    }

    public void scaleByPixel(final float pixel) {
        if (pixel == 0) {
            return;
        }
        final List<Line> lines = pixel > 0 ? grow(pixel) : shrink(pixel);

        for (int index = 0; index < getLines().size(); index++) {
            final Line line = lines.get(index);
            final Line prevLine = lines.get((index + getLines().size() - 1) % getLines().size());
            Point2D.Double point = Line.getLineLineIntersection(prevLine, line);
            if (point != null) {
                _points.get(index).set((float) point.getX(), (float) point.getY(), 0);
            }
        }
    }

    private List<Line> grow(final float pixel) {
        final List<Line> lines = new ArrayList<>();
        for (int index = 0; index < getLines().size(); index++) {
            final Line line = getLines().get(index);
            final Vector3f outwardNormal = line.getOutwardNormal();
            float dX = outwardNormal.getX() * pixel;
            float dY = outwardNormal.getY() * pixel;
            lines.add(new Line(new Vector3f(line.getFirst().getX() + dX, line.getFirst().getY() + dY, line.getFirst().getZ()), new Vector3f(line.getLast().getX() + dX, line.getLast().getY() + dY, line.getLast().getZ())));
        }
        return lines;
    }

    private List<Line> shrink(final float pixel) {
        final List<Line> lines = new ArrayList<>();
        for (int index = 0; index < getLines().size(); index++) {
            final Line line = getLines().get(index);
            final Vector3f inwardNormal = line.getInwardNormal();
            float dX = inwardNormal.getX() * pixel;
            float dY = inwardNormal.getY() * pixel;
            lines.add(new Line(new Vector3f(line.getFirst().getX() - dX, line.getFirst().getY() - dY, line.getFirst().getZ()), new Vector3f(line.getLast().getX() - dX, line.getLast().getY() - dY, line.getLast().getZ())));
        }
        return lines;
    }

    public void expand(final float factor) {
        for (final Vector3f vector : _points) {
            vector.scaleRelative(getCenter(), factor);
        }
    }

    public void scaleX(float scaleX) {
        scale(scaleX, 1f);
    }

    public void scaleY(float scaleY) {
        scale(1f, scaleY);
    }

    public Hitbox clone() {
        Hitbox otherBox = new Hitbox(_center.clone());
        for (Vector3f vector : _points) {
            otherBox.addPoint(Vector3f.sub(vector, _center));
        }
        return otherBox;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " { ";
        for (int i = 0; i < _points.size(); i++) {
            result += _points.get(i).toString();
            if (i < _points.size() - 1) {
                result += " ; ";
            }
        }
        return result + " }";
    }

    public AABB getAABB() {
        return _aabb;
    }

    public void export(ObjectOutputStream outputStream) {
        try {
            for (Vector3f vector : _points) {
                outputStream.writeObject(vector);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
