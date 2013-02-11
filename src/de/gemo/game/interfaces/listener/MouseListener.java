package de.gemo.game.interfaces.listener;

import de.gemo.game.events.mouse.MouseDownEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.events.mouse.MouseReleaseEvent;

public interface MouseListener {

    public void onClick(MouseDownEvent event);

    public void onRelease(MouseReleaseEvent event);

    public void onMove(MouseMoveEvent event);

    public void onDrag(MouseDragEvent event);

}
