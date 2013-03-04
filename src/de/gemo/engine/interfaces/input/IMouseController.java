package de.gemo.engine.interfaces.input;

import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.events.mouse.MouseWheelEvent;

public interface IMouseController {

    public void onMouseClick(MouseClickEvent event);

    public void onMouseMove(MouseMoveEvent event);

    public void onMouseDrag(MouseDragEvent event);

    public void onMouseRelease(MouseReleaseEvent event);

    public void onMouseWheel(MouseWheelEvent event);

}
