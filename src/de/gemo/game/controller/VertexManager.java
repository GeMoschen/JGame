package de.gemo.game.controller;

import java.util.HashMap;

import org.newdawn.slick.Color;

import de.gemo.engine.entity.EntityVertex;
import de.gemo.game.events.gui.buttons.VertexListener;

import static org.lwjgl.opengl.GL11.*;

public class VertexManager {
    private HashMap<Integer, EntityVertex> vertexList;
    private SecondGUIController controller;
    private MyGUIController guiController;
    private VertexListener mouseListener;

    public VertexManager(SecondGUIController controller, MyGUIController guiController) {
        this.controller = controller;
        this.guiController = guiController;
        this.vertexList = new HashMap<Integer, EntityVertex>();
        this.mouseListener = new VertexListener(guiController);
    }

    public void addVertex(int x, int y) {
        EntityVertex vertex = new EntityVertex(x, y);
        vertex.setMouseListener(mouseListener);
        controller.unfocusElement();
        controller.add(vertex);
        guiController.getBtn_removeVertex().setVisible(false);
        guiController.getLbl_position().setLabel("Position: " + x + " / " + y);
        vertexList.put(vertex.getEntityID(), vertex);
        controller.focusElement(vertex);
    }

    public void removeVertex(EntityVertex vertex) {
        if (vertex != null) {
            vertexList.remove(vertex.getEntityID());
            controller.remove(vertex);
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
