package de.gemo.game.polygon.navigation;

import de.gemo.gameengine.units.Vector3f;

import java.util.ArrayList;
import java.util.List;

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

    public int getLength() {
        return path.size();
    }

    public List<Vector3f> getPath() {
        return path;
    }

    public Vector3f getNode(int index) {
        return this.path.get(index);
    }

    public void render(int startIndex) {
        glColor4f(1, 1, 1, 1f);

        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glLineWidth(1f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBegin(GL_LINES);
        {
            for (int i = 1; i < this.path.size(); i++) {
                Vector3f current = this.path.get(i - 1);
                Vector3f last = this.path.get(i);
                glVertex3f(last.getX(), last.getY(), 0);
                glVertex3f(current.getX(), current.getY(), 0);
            }
        }
        glEnd();

        glDisable(GL_BLEND);
    }

    public void render() {
        this.render(1);
    }
}
