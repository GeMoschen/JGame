package de.gemo.game.events.gui.buttons;

import de.gemo.engine.entity.EntityVertex;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.interfaces.listener.MouseListener;
import de.gemo.game.manager.gui.MyGUIManager2;
import de.gemo.game.manager.gui.VertexManager;

public class RemoveButtonListener implements MouseListener {

    private VertexManager vertexManager;
    private MyGUIManager2 guiManager;

    public RemoveButtonListener(VertexManager vertexManager, MyGUIManager2 guiManager) {
        this.vertexManager = vertexManager;
        this.guiManager = guiManager;
    }

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
        if (event.isLeftButton()) {
            vertexManager.removeVertex((EntityVertex) guiManager.getFocusedElement());
            guiManager.setSelectedVertex(null);
        }
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
    }

    @Override
    public void onMouseDrag(GUIElement element, MouseDragEvent event) {
    }

}
