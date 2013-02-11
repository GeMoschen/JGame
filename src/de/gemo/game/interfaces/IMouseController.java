package de.gemo.game.interfaces;

import de.gemo.game.events.mouse.MouseDownEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.events.mouse.MouseReleaseEvent;

public interface IMouseController {

    public void onMouseDown(MouseDownEvent event);

    public void onMouseMove(MouseMoveEvent event);

    public void onMouseDrag(MouseDragEvent event);

    public void onMouseUp(MouseReleaseEvent event);

}
