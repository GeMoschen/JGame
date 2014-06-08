package de.gemo.game.fov.navigation;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class Path {
    private List<NavNode> path;

    public Path() {
        this.path = new ArrayList<NavNode>();
    }

    public boolean addNode(NavNode node) {
        if (!this.path.contains(node)) {
            this.path.add(0, node);
            return true;
        }
        return false;
    }

    public List<NavNode> getPath() {
        return path;
    }

    public NavNode getNode(int index) {
        return this.path.get(index);
    }

    public void render() {
        glColor4f(1, 0, 1, 1);

        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glLineWidth(1f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBegin(GL_LINES);
        {
            for (int i = 1; i < this.path.size(); i++) {
                NavNode current = this.path.get(i);
                NavNode last = this.path.get(i - 1);
                glVertex2f(last.getPosition().getX(), last.getPosition().getY());
                glVertex2f(current.getPosition().getX(), current.getPosition().getY());
            }
        }
        glEnd();

        glDisable(GL_BLEND);
    }
}
