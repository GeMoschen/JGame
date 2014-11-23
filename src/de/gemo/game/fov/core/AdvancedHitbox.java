package de.gemo.game.fov.core;

import java.util.*;

import org.newdawn.slick.*;

import static org.lwjgl.opengl.GL11.*;

import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.units.*;

public class AdvancedHitbox extends Hitbox {

    private List<VerticalSegment> segmentList;
    private float height = 0;

    public AdvancedHitbox(float x, float y, float height) {
        super(x, y);
        this.height = height;
        this.segmentList = new ArrayList<VerticalSegment>();
    }

    public AdvancedHitbox(Vector3f center, float height) {
        this(center.getX(), center.getY(), height);
    }

    @Override
    public Vector3f addPoint(float x, float y) {
        Vector3f result = super.addPoint(x, y);
        this.updateLineSegments();
        return result;
    }

    private void updateLineSegments() {
        this.segmentList.clear();
        int nextIndex = 0;
        Vector3f currentVector, nextVector;
        for (int currentIndex = 0; currentIndex < this.getPointCount(); currentIndex++) {
            if (currentIndex < this.getPointCount() - 1) {
                nextIndex = (currentIndex + 1);
            } else {
                nextIndex = 0;
            }
            if (currentIndex == nextIndex) {
                break;
            }

            currentVector = this.getPoint(currentIndex);
            nextVector = this.getPoint(nextIndex);
            this.segmentList.add(new VerticalSegment(currentVector, nextVector));
        }
    }

    @Override
    public void render() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            // render hitbox
            glPushMatrix();
            {
                Color.gray.bind();
                glBegin(GL_QUADS);
                for (Vector3f vector : this.points) {
                    glVertex3f(vector.getX(), this.height, vector.getY());
                }
                glEnd();
            }
            glPopMatrix();

            // render AABB
            // this.aabb.render();

            this.renderSegments();

            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);

            // translate & render center
            glPushMatrix();
            {

                glTranslatef(this.getCenter().getX(), this.height + 0.2f, this.getCenter().getY());
                glRotatef(90, 1, 0, 0);
                FontManager.getStandardFont().drawString(-5, -7, "" + (int) this.height);
            }
            glPopMatrix();
        }
        glPopMatrix();
    }

    @Override
    protected void renderCenter() {
        // render center
        Color.red.bind();
        glBegin(GL_LINE_LOOP);
        glVertex3i(-2, 0, -2);
        glVertex3i(2, 0, -2);
        glVertex3i(+2, 0, +2);
        glVertex3i(-2, 0, +2);
        glEnd();
    }

    private void renderSegments() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            // render hitbox
            glPushMatrix();
            {
                for (VerticalSegment segment : this.segmentList) {
                    Color.gray.bind();
                    glBegin(GL_QUADS);
                    glVertex3f(segment.getPoint1().getX(), 0, segment.getPoint1().getY());
                    glVertex3f(segment.getPoint2().getX(), 0, segment.getPoint2().getY());
                    glVertex3f(segment.getPoint2().getX(), this.height, segment.getPoint2().getY());
                    glVertex3f(segment.getPoint1().getX(), this.height, segment.getPoint1().getY());
                    glEnd();

                    Color.green.bind();
                    glBegin(GL_LINE_LOOP);
                    glVertex3f(segment.getPoint1().getX(), 0, segment.getPoint1().getY());
                    glVertex3f(segment.getPoint2().getX(), 0, segment.getPoint2().getY());
                    glVertex3f(segment.getPoint2().getX(), this.height, segment.getPoint2().getY());
                    glVertex3f(segment.getPoint1().getX(), this.height, segment.getPoint1().getY());
                    glEnd();
                }
            }
            glPopMatrix();

            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    @Override
    public Vector3f addPoint(Vector3f vector) {
        return this.addPoint(vector.getX(), vector.getY());
    }

    @Override
    public Hitbox clone() {
        Hitbox otherBox = new AdvancedHitbox(this.center.clone(), this.height);
        for (Vector3f vector : this.points) {
            otherBox.addPoint(Vector3f.sub(vector, this.center));
        }
        return otherBox;
    }

    public float getHeight() {
        return height;
    }
}
