package de.gemo.game.fov.navigation;

import java.util.*;

import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class NavNode implements Comparable<NavNode> {
    private Vector3f position;
    private List<NavNode> neighbors;
    private float distanceFromStart = 0, heuristicDistanceFromGoal = 0;
    private NavNode previousNode = null;

    public NavNode(Vector3f position) {
        this.position = position;
        this.neighbors = new ArrayList<NavNode>();
    }

    public void reset() {
        this.setDistanceFromStart(0);
        this.setHeuristicDistanceFromGoal(0);
        this.setPreviousNode(null);
    }

    public Vector3f getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NavNode) {
            NavNode other = (NavNode) obj;
            return this.position.equals(other.position) && this.neighbors.equals(other.neighbors);
        }
        return false;
    }

    public boolean isNeighbor(NavNode neighbor) {
        return this.neighbors.contains(neighbor);
    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glColor4f(0, 1, 1, 0.3f);
            glBegin(GL_LINE_LOOP);
            {
                glVertex2f(this.position.getX() - 2, this.position.getY() - 2);
                glVertex2f(this.position.getX() - 2, this.position.getY() + 2);
                glVertex2f(this.position.getX() + 2, this.position.getY() + 2);
                glVertex2f(this.position.getX() + 2, this.position.getY() - 2);
            }
            glEnd();

            glColor4f(1f, 1f, 1f, 0.03f);

            glBegin(GL_LINES);
            {
                for (NavNode neighbor : this.neighbors) {
                    glVertex2f(this.position.getX(), this.position.getY());
                    glVertex2f(neighbor.position.getX(), neighbor.position.getY());
                }
            }
            glEnd();
        }
        glPopMatrix();
    }

    public void renderSpecial() {
        glPushMatrix();
        {
            glDisable(GL_LIGHTING);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glColor4f(1, 0, 0, 1f);
            glBegin(GL_LINE_LOOP);
            {
                glVertex2f(this.position.getX() - 2, this.position.getY() - 2);
                glVertex2f(this.position.getX() - 2, this.position.getY() + 2);
                glVertex2f(this.position.getX() + 2, this.position.getY() + 2);
                glVertex2f(this.position.getX() + 2, this.position.getY() - 2);
            }
            glEnd();

            glColor4f(1f, 1f, 0f, 1f);
        }
        glPopMatrix();
    }

    public void removeLastAdded() {
        this.neighbors.remove(this.neighbors.size() - 1);
    }

    public boolean removeNeighbor(NavNode neighbor) {
        if (this.isNeighbor(neighbor)) {
            this.neighbors.remove(neighbor);
            return true;
        }
        return false;
    }

    public List<NavNode> getNeighbors() {
        return neighbors;
    }

    public boolean addNeighbor(NavNode neighbor) {
        if (!this.isNeighbor(neighbor)) {
            this.neighbors.add(neighbor);
            return true;
        }
        return false;
    }

    public void setDistanceFromStart(float distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public float getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setHeuristicDistanceFromGoal(float heuristicDistanceFromGoal) {
        this.heuristicDistanceFromGoal = heuristicDistanceFromGoal;
    }

    public float getHeuristicDistanceFromGoal() {
        return heuristicDistanceFromGoal;
    }

    public void setPreviousNode(NavNode previousNode) {
        this.previousNode = previousNode;
    }

    public NavNode getPreviousNode() {
        return previousNode;
    }

    /**
     * Taken from Point2D
     */
    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(this.position.getX());
        bits ^= java.lang.Double.doubleToLongBits(this.position.getY()) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }

    public int compareTo(NavNode otherNode) {
        float thisTotalDistanceFromGoal = heuristicDistanceFromGoal + distanceFromStart;
        float otherTotalDistanceFromGoal = otherNode.heuristicDistanceFromGoal + otherNode.distanceFromStart;

        if (thisTotalDistanceFromGoal < otherTotalDistanceFromGoal) {
            return -1;
        } else if (thisTotalDistanceFromGoal > otherTotalDistanceFromGoal) {
            return 1;
        } else {
            return 0;
        }
    }
}
