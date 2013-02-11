package de.gemo.engine.interfaces.listener;

import de.gemo.engine.events.mouse.MouseDownEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;

public interface MouseListener {

    public void onMouseClick(GUIElement element, MouseDownEvent event);

    public void onMouseRelease(GUIElement element, MouseReleaseEvent event);

    public void onMouseMove(GUIElement element, MouseMoveEvent event);

    public void onMouseDrag(GUIElement element, MouseDragEvent event);

}
