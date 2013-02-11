package de.gemo.game.events.gui.buttons;

import de.gemo.engine.events.mouse.MouseDownEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.interfaces.listener.FocusListener;
import de.gemo.engine.interfaces.listener.MouseListener;

public class ExitButtonListener implements MouseListener, FocusListener {

    @Override
    public void onMouseClick(MouseDownEvent event) {
        // TODO Auto-generated method stub
        System.out.println("mouse click");
    }

    @Override
    public void onMouseRelease(MouseReleaseEvent event) {
        System.out.println("mouse release");
        if (event.isLeftButton()) {
            System.exit(0);
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        System.out.println("mouse move: " + event.getX() + " / " + event.getY());
    }

    @Override
    public void onMouseDrag(MouseDragEvent event) {
        // TODO Auto-generated method stub
        System.out.println("mouse drag");
    }

    @Override
    public void onFocusGained() {
        System.out.println("focus gained");
    }

    @Override
    public void onFocusLost() {
        System.out.println("focus lost");
    }

    @Override
    public void onHoverBegin() {
        System.out.println("hover begin");
    }

    @Override
    public void onHover() {
        System.out.println("hover");
    }

    @Override
    public void onHoverEnd() {
        System.out.println("hover end!");
    }

}
