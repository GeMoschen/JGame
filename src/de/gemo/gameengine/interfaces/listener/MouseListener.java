package de.gemo.gameengine.interfaces.listener;

import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.gui.*;

public interface MouseListener {

    public void onMouseClick(GUIElement element, MouseClickEvent event);

    public void onMouseRelease(GUIElement element, MouseReleaseEvent event);

    public void onMouseMove(GUIElement element, MouseMoveEvent event);

    public void onMouseDrag(GUIElement element, MouseDragEvent event);

    public void onMouseHold(GUIElement element, MouseHoldEvent event);

    public void onMouseWheel(GUIElement element, MouseWheelEvent event);
}
