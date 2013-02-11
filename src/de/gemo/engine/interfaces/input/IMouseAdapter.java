package de.gemo.engine.interfaces.input;

import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;

public interface IMouseAdapter {

    public boolean handleMouseMove(MouseMoveEvent event);

    public boolean handleMouseClick(MouseClickEvent event);

    public boolean handleMouseRelease(MouseReleaseEvent event);

    public boolean handleMouseDrag(MouseDragEvent event);
}
