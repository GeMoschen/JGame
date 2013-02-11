package de.gemo.engine.interfaces.listener;

import de.gemo.engine.events.mouse.MouseDownEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;

public interface MouseListener {

    public void onMouseClick(MouseDownEvent event);

    public void onMouseRelease(MouseReleaseEvent event);

    public void onMouseMove(MouseMoveEvent event);

    public void onMouseDrag(MouseDragEvent event);

}
