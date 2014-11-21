package de.gemo.game.fov.units;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import de.gemo.gameengine.units.*;

public class ShadowFin {
    public List<Vector3f> vectorList;

    public ShadowFin(Vector3f... vectors) {
        this.vectorList = new ArrayList<Vector3f>();
        for (Vector3f vector : vectors) {
            this.vectorList.add(vector);
        }
    }

    public ShadowFin(Vector2f... vectors) {
        this.vectorList = new ArrayList<Vector3f>();
        for (Vector2f vector : vectors) {
            this.vectorList.add(new Vector3f(vector.x, 0, vector.y));
        }
    }

    public void render() {
        this.render(1f, 1f, 1f, 1f);
    }

    public void render(float r, float g, float b, float alpha) {
        glBegin(GL_QUADS);
        {
            glColor4f(r, g, b, alpha);
            for (Vector3f vector : this.vectorList) {
                glVertex3f(vector.getX(), 0, vector.getY());
            }
        }
        glEnd();
    }
}
