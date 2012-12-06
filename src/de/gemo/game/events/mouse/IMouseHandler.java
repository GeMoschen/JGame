package de.gemo.game.events.mouse;

public interface IMouseHandler {

    public void onMouseDown(MouseDownEvent event);

    public void onMouseMove(MouseMoveEvent event);

    public void onMouseDrag(MouseDragEvent event);

    public void onMouseUp(MouseUpEvent event);

}
