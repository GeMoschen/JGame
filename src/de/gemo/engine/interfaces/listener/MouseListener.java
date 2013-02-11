package de.gemo.engine.interfaces.listener;

import de.gemo.engine.events.mouse.MouseDownEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;

public interface MouseListener {

    public void onClick(MouseDownEvent event);

    public void onRelease(MouseReleaseEvent event);

    public void onMove(MouseMoveEvent event);

    public void onDrag(MouseDragEvent event);

}
