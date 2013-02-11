package de.gemo.engine.interfaces.input;

import de.gemo.engine.events.mouse.MouseDownEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;

public interface IMouseController {

    public void onMouseDown(MouseDownEvent event);

    public void onMouseMove(MouseMoveEvent event);

    public void onMouseDrag(MouseDragEvent event);

    public void onMouseUp(MouseReleaseEvent event);

}
