package de.gemo.game.events.gui.buttons;

import de.gemo.game.events.mouse.MouseDownEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.events.mouse.MouseReleaseEvent;
import de.gemo.game.interfaces.listener.FocusListener;
import de.gemo.game.interfaces.listener.MouseListener;

public class ExitButtonListener implements MouseListener, FocusListener {

    @Override
    public void onClick(MouseDownEvent event) {
        // TODO Auto-generated method stub
        System.out.println("mouse click");
    }

    @Override
    public void onRelease(MouseReleaseEvent event) {
        System.out.println("mouse release");
        if (event.isLeftButton()) {
            System.exit(0);
        }
    }

    @Override
    public void onMove(MouseMoveEvent event) {
        // TODO Auto-generated method stub
        System.out.println("mouse move");
    }

    @Override
    public void onDrag(MouseDragEvent event) {
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
