package de.gemo.game.events.gui.buttons;

import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.interfaces.listener.MouseListener;
import de.gemo.game.controller.MyGUIController;

public class VertexListener implements MouseListener {

    private MyGUIController controller;

    public VertexListener(MyGUIController controller) {
        this.controller = controller;
    }

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
        controller.getBtn_removeVertex().setVisible(true);
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
            controller.getLbl_position().setLabel("Position: " + (int) element.getX() + " / " + (int) element.getY());
        }
    }
}
