package de.gemo.game.collision.core;

import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class Hitbox3D {
    private Vector3f center;
    private Vector3f[] vectors;
    private Vector3f[] normals;
    private Vector3f[] normalsPos;
    private AABB aabb;

    public Hitbox3D(Vector3f center, float halfWidth, float halfHeight, float halfDepth) {
        this.center = center.clone();
        this.createBox(halfWidth, halfHeight, halfDepth);
        this.createAABB();
        this.normals = new Vector3f[6];
        this.normalsPos = new Vector3f[6];
        this.createNormals();
    }

    private void createBox(float halfWidth, float halfHeight, float halfDepth) {
        this.vectors = new Vector3f[8];

        this.vectors[0] = new Vector3f(this.center.getX() - halfWidth, this.center.getY() - halfHeight, this.center.getZ() - halfDepth);
        this.vectors[1] = new Vector3f(this.center.getX() + halfWidth, this.center.getY() - halfHeight, this.center.getZ() - halfDepth);
        this.vectors[2] = new Vector3f(this.center.getX() + halfWidth, this.center.getY() - halfHeight, this.center.getZ() + halfDepth);
        this.vectors[3] = new Vector3f(this.center.getX() - halfWidth, this.center.getY() - halfHeight, this.center.getZ() + halfDepth);

        this.vectors[4] = new Vector3f(this.center.getX() - halfWidth, this.center.getY() + halfHeight, this.center.getZ() - halfDepth);
        this.vectors[5] = new Vector3f(this.center.getX() + halfWidth, this.center.getY() + halfHeight, this.center.getZ() - halfDepth);
        this.vectors[6] = new Vector3f(this.center.getX() + halfWidth, this.center.getY() + halfHeight, this.center.getZ() + halfDepth);
        this.vectors[7] = new Vector3f(this.center.getX() - halfWidth, this.center.getY() + halfHeight, this.center.getZ() + halfDepth);
    }

    public Vector3f getVector(int index) {
        return vectors[index];
    }

    public AABB getAABB() {
        return aabb;
    }

    private void createAABB() {
        this.aabb = new AABB();
        for (Vector3f vector : this.vectors) {
            this.aabb.addPoint(this.center.getX() + vector.getX(), this.center.getY() + vector.getY(), this.center.getZ() + vector.getZ());
        }
    }

    public void roll(float roll) {
        this.aabb.reset();
        for (Vector3f vector : this.vectors) {
            vector.roll(this.center, roll);
            this.aabb.addPoint(vector.getX(), vector.getY(), vector.getZ());
        }
        this.createNormals();
    }

    public void yaw(float yaw) {
        this.aabb.reset();
        for (Vector3f vector : this.vectors) {
            vector.yaw(this.center, yaw);
            this.aabb.addPoint(vector.getX(), vector.getY(), vector.getZ());
        }
        this.createNormals();
    }

    public void pitch(float pitch) {
        this.aabb.reset();
        for (Vector3f vector : this.vectors) {
            vector.pitch(this.center, pitch);
            this.aabb.addPoint(vector.getX(), vector.getY(), vector.getZ());
        }
        this.createNormals();
    }

    private void createNormals() {
        float scale = 5f;
        this.normals[0] = Vector3f.cross(Vector3f.sub(this.getVector(1), this.getVector(0)), Vector3f.sub(this.getVector(2), this.getVector(0))).normalize().scale(scale);
        this.normals[1] = Vector3f.cross(Vector3f.sub(this.getVector(6), this.getVector(4)), Vector3f.sub(this.getVector(5), this.getVector(4))).normalize().scale(scale);
        this.normals[2] = Vector3f.cross(Vector3f.sub(this.getVector(2), this.getVector(6)), Vector3f.sub(this.getVector(3), this.getVector(6))).normalize().scale(scale);
        this.normals[3] = Vector3f.cross(Vector3f.sub(this.getVector(0), this.getVector(4)), Vector3f.sub(this.getVector(1), this.getVector(4))).normalize().scale(scale);
        this.normals[4] = Vector3f.cross(Vector3f.sub(this.getVector(5), this.getVector(1)), Vector3f.sub(this.getVector(2), this.getVector(1))).normalize().scale(scale);
        this.normals[5] = Vector3f.cross(Vector3f.sub(this.getVector(3), this.getVector(0)), Vector3f.sub(this.getVector(4), this.getVector(0))).normalize().scale(scale);
        this.normalsPos[0] = Vector3f.add(this.getVector(0), Vector3f.sub(this.getVector(2), this.getVector(0)).scale(0.5f));
        this.normalsPos[1] = Vector3f.add(this.getVector(4), Vector3f.sub(this.getVector(6), this.getVector(4)).scale(0.5f));
        this.normalsPos[2] = Vector3f.add(this.getVector(0), Vector3f.sub(this.getVector(5), this.getVector(0)).scale(0.5f));
        this.normalsPos[3] = Vector3f.add(this.getVector(3), Vector3f.sub(this.getVector(6), this.getVector(3)).scale(0.5f));
        this.normalsPos[4] = Vector3f.add(this.getVector(1), Vector3f.sub(this.getVector(6), this.getVector(1)).scale(0.5f));
        this.normalsPos[5] = Vector3f.add(this.getVector(0), Vector3f.sub(this.getVector(7), this.getVector(0)).scale(0.5f));
    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            // tranlaste
            glTranslatef(this.center.getX(), this.center.getY(), this.center.getZ());

            // set color
            glColor4f(1, 0, 0, 0.5f);

            // DOWN & UP
            this.renderPlane(this.vectors[0], this.vectors[1], this.vectors[2], this.vectors[3]);
            this.renderPlane(this.vectors[4], this.vectors[5], this.vectors[6], this.vectors[7]);

            // set color
            glColor4f(1, 0, 0, 0.5f);

            // SIDES
            glBegin(GL_LINES);
            {
                this.renderLine(this.vectors[0], this.vectors[4]);
                this.renderLine(this.vectors[1], this.vectors[5]);
                this.renderLine(this.vectors[3], this.vectors[7]);
                this.renderLine(this.vectors[2], this.vectors[6]);
            }
            glEnd();

            this.renderNormals();
        }
        glPopMatrix();
        
        

        // this.aabb.render();
    }

    private void renderNormals() {
        // set color
        glColor4f(1, 1, 1, 0.5f);

        // SIDES
        glBegin(GL_LINES);
        {
            for (int index = 0; index < 6; index++) {
                this.renderLine(this.normalsPos[index], Vector3f.add(this.normalsPos[index], this.normals[index]));
            }
        }
        glEnd();
    }

    public void move(int x, int y, int z) {
        this.aabb.reset();
        this.center.move(x, y, z);
        for (Vector3f vector : this.vectors) {
            vector.move(x, y, z);
            this.aabb.addPoint(vector.getX(), vector.getY(), vector.getZ());
        }
        this.createNormals();
    }

    private void renderPlane(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        glBegin(GL_LINE_LOOP);
        {
            glVertex3f(v1.getX(), v1.getY(), v1.getZ());
            glVertex3f(v2.getX(), v2.getY(), v2.getZ());
            glVertex3f(v3.getX(), v3.getY(), v3.getZ());
            glVertex3f(v4.getX(), v4.getY(), v4.getZ());
        }
        glEnd();
    }

    private void renderLine(Vector3f v1, Vector3f v2) {
        glVertex3f(v1.getX(), v1.getY(), v1.getZ());
        glVertex3f(v2.getX(), v2.getY(), v2.getZ());
    }
}
