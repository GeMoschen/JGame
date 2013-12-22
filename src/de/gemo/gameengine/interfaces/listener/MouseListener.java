package de.gemo.gameengine.interfaces.listener;

import de.gemo.gameengine.events.mouse.MouseClickEvent;
import de.gemo.gameengine.events.mouse.MouseDragEvent;
import de.gemo.gameengine.events.mouse.MouseMoveEvent;
import de.gemo.gameengine.events.mouse.MouseReleaseEvent;
import de.gemo.gameengine.events.mouse.MouseWheelEvent;
import de.gemo.gameengine.gui.GUIElement;

public interface MouseListener {

    public void onMouseClick(GUIElement element, MouseClickEvent event);

    public void onMouseRelease(GUIElement element, MouseReleaseEvent event);

    public void onMouseMove(GUIElement element, MouseMoveEvent event);

    public void onMouseDrag(GUIElement element, MouseDragEvent event);

    public void onMouseWheel(GUIElement element, MouseWheelEvent event);
}
