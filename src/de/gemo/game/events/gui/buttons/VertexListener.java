package de.gemo.game.events.gui.buttons;

import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.interfaces.listener.MouseListener;
import de.gemo.game.manager.gui.MyGUIManager1;

public class VertexListener implements MouseListener {

    private MyGUIManager1 guiManager;

    public VertexListener(MyGUIManager1 guiManager) {
        this.guiManager = guiManager;
    }

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
        guiManager.getBtn_removeVertex().setVisible(true);
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
    }

    @Override
    public void onMouseDrag(GUIElement element, MouseDragEvent event) {
        if (event.hasMoved()) {
            element.move(event.getDifX(), event.getDifY());
            guiManager.getLbl_position().setLabel("Position: " + (int) element.getX() + " / " + (int) element.getY());
        }
    }
}
