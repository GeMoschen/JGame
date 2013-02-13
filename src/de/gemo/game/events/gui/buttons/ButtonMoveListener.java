package de.gemo.game.events.gui.buttons;

import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.interfaces.listener.MouseListener;

public class ButtonMoveListener implements MouseListener {

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
    }

    @Override
    public void onMouseDrag(GUIElement element, MouseDragEvent event) {
        if (event.isRightButton() && event.hasMoved()) {
            float newX = event.getX() + element.getXOnScreen();
            int tX = (int) (newX / 2);
            float newY = event.getY() + element.getYOnScreen();
            int tY = (int) (newY / 2);
            element.setCenter(tX * 2, tY * 2);
        }
    }
}
