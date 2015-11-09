package de.gemo.game.polygon.navigation;

import de.gemo.gameengine.units.Vector3f;

import java.util.ArrayList;
import java.util.List;

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
        if (obj == this) {
            return true;
        }
        if (obj instanceof NavNode) {
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

            glColor4f(0, 1, 1, 1f);
            glBegin(GL_POLYGON);
            {
                glVertex3f(this.position.getX() - 2, this.position.getY() - 2, 0);
                glVertex3f(this.position.getX() - 2, this.position.getY() + 2, 0);
                glVertex3f(this.position.getX() + 2, this.position.getY() + 2, 0);
                glVertex3f(this.position.getX() + 2, this.position.getY() - 2, 0);
            }
            glEnd();

            glColor4f(1f, 1f, 1f, 0.03f);

            glBegin(GL_LINES);
            {
                for (NavNode neighbor : this.neighbors) {
                    glVertex3f(this.position.getX(), this.position.getY(), 0);
                    glVertex3f(neighbor.position.getX(), neighbor.position.getY(), 0);
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
                glVertex3f(this.position.getX() - 2, 0, this.position.getY() - 2);
                glVertex3f(this.position.getX() - 2, 0, this.position.getY() + 2);
                glVertex3f(this.position.getX() + 2, 0, this.position.getY() + 2);
                glVertex3f(this.position.getX() + 2, 0, this.position.getY() - 2);
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
        if (neighbor.equals(this)) {
            return false;
        }
        if (!this.isNeighbor(neighbor)) {
            this.neighbors.add(neighbor);
            return true;
        }
        return false;
    }

    public float getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(float distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public float getHeuristicDistanceFromGoal() {
        return heuristicDistanceFromGoal;
    }

    public void setHeuristicDistanceFromGoal(float heuristicDistanceFromGoal) {
        this.heuristicDistanceFromGoal = heuristicDistanceFromGoal;
    }

    public NavNode getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(NavNode previousNode) {
        this.previousNode = previousNode;
    }

    /**
     * Taken from Point2D
     */
    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(this.position.getX());
        bits ^= Double.doubleToLongBits(this.position.getY()) * 31;
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
