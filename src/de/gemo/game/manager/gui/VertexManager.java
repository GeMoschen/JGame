package de.gemo.game.manager.gui;

import java.util.HashMap;

import org.newdawn.slick.Color;

import de.gemo.engine.entity.EntityVertex;
import de.gemo.game.events.gui.buttons.VertexListener;

import static org.lwjgl.opengl.GL11.*;

public class VertexManager {
    private HashMap<Integer, EntityVertex> vertexList;
    private MyGUIManager2 guiManager2;
    private MyGUIManager1 guiManager1;
    private VertexListener mouseListener;

    public VertexManager(MyGUIManager2 guiManager2, MyGUIManager1 guiManager1) {
        this.guiManager2 = guiManager2;
        this.guiManager1 = guiManager1;
        this.vertexList = new HashMap<Integer, EntityVertex>();
        this.mouseListener = new VertexListener(guiManager1);
    }

    public void addVertex(int x, int y) {
        EntityVertex vertex = new EntityVertex(x, y);
        vertex.setMouseListener(mouseListener);
        guiManager2.unfocusElement();
        guiManager2.add(vertex);
        guiManager1.getBtn_removeVertex().setVisible(true);
        guiManager1.getLbl_position().setLabel("Position: " + x + " / " + y);
        vertexList.put(vertex.getEntityID(), vertex);
        guiManager2.focusElement(vertex);
    }

    public void removeVertex(EntityVertex vertex) {
        if (vertex != null) {
            vertexList.remove(vertex.getEntityID());
            guiManager2.remove(vertex);
        }
    }

    public void render() {
        glPushMatrix();
        {
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            // render line
            Color.gray.bind();
            glLineWidth(0.5f);
            glBegin(GL_LINE_LOOP);
            for (EntityVertex vertex : this.vertexList.values()) {
                glVertex3f(vertex.getX(), vertex.getY(), vertex.getZ());
            }
            glEnd();

            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();

    }
}
