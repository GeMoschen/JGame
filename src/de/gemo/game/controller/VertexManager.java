package de.gemo.game.controller;

import java.util.HashMap;

import org.newdawn.slick.Color;

import de.gemo.engine.core.GUIController;
import de.gemo.engine.entity.EntityVertex;
import de.gemo.game.events.gui.buttons.ButtonMoveListener;

import static org.lwjgl.opengl.GL11.*;

public class VertexManager {
    private HashMap<Integer, EntityVertex> vertexList;
    private GUIController guiController;
    private ButtonMoveListener mouseListener = new ButtonMoveListener();

    public VertexManager(GUIController guiController) {
        this.guiController = guiController;
        vertexList = new HashMap<Integer, EntityVertex>();
    }

    public void addVertex(int x, int y) {
        EntityVertex vertex = new EntityVertex(x, y);
        vertex.setMouseListener(mouseListener);
        guiController.add(vertex);
        vertexList.put(vertex.getEntityID(), vertex);
    }

    public void removeVertex(EntityVertex vertex) {
        vertexList.remove(vertex.getEntityID());
        guiController.remove(vertex);
    }

    public void render() {
        for (EntityVertex vertex : this.vertexList.values()) {
            vertex.render();
        }

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
