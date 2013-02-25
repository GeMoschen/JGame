package de.gemo.game.events.gui.buttons;

import de.gemo.engine.core.Engine;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.interfaces.listener.MouseListener;

public class ExitButtonListener implements MouseListener {

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
        if (event.isLeftButton()) {
            Engine.close();
        }
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
    }

    @Override
    public void onMouseDrag(GUIElement element, MouseDragEvent event) {
    }

}
