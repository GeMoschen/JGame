package de.gemo.game.events.gui.buttons;

import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.interfaces.listener.MouseListener;
import de.gemo.game.controller.MyGUIController;
import de.gemo.game.controller.VertexManager;

public class RemoveButtonListener implements MouseListener {

    private VertexManager vertexManager;
    private MyGUIController guiController;

    public RemoveButtonListener(VertexManager vertexManager, MyGUIController guiController) {
        this.vertexManager = vertexManager;
        this.guiController = guiController;
    }

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
        vertexManager.removeVertex(guiController.getSelectedVertex());
        guiController.setSelectedVertex(null);
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
    }

    @Override
    public void onMouseDrag(GUIElement element, MouseDragEvent event) {
    }

}
