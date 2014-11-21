package de.gemo.game.fov.navigation;

import java.util.*;

import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class Path {
    private List<Vector3f> path;

    public Path() {
        this.path = new ArrayList<Vector3f>();
    }

    public boolean addNode(Vector3f node) {
        if (!this.path.contains(node)) {
            this.path.add(0, node);
            return true;
        }
        return false;
    }

    public List<Vector3f> getPath() {
        return path;
    }

    public Vector3f getNode(int index) {
        return this.path.get(index);
    }

    public void render(int startIndex) {
        glColor4f(0, 0, 1, 0.4f);

        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glLineWidth(1f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBegin(GL_LINES);
        {
            for (int i = startIndex; i < this.path.size(); i++) {
                Vector3f current = this.path.get(i);
                Vector3f last = this.path.get(i - 1);
                glVertex3f(last.getX(), 0, last.getY());
                glVertex3f(current.getX(), 0, current.getY());
            }
        }
        glEnd();

        glDisable(GL_BLEND);
    }

    public void render() {
        this.render(0);
    }
}
