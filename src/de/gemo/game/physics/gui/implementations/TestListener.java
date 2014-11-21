package de.gemo.game.physics.gui.implementations;

import org.lwjgl.input.Keyboard;

import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.gui.GUIElement;
import de.gemo.gameengine.interfaces.listener.FocusListener;
import de.gemo.gameengine.interfaces.listener.MouseListener;

public class TestListener implements MouseListener, FocusListener {

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
    }

    @Override
    public void onMouseDrag(GUIElement element, MouseDragEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && event.isRightButton()) {
            element.move(event.getDifX(), event.getDifY());
        }
    }

    @Override
    public void onFocusGained(GUIElement element) {
        System.out.println("focus gained");
    }

    @Override
    public void onFocusLost(GUIElement element) {
        System.out.println("focus lost");
    }

    @Override
    public void onHoverBegin(GUIElement element) {
        System.out.println("hover begin");

    }

    @Override
    public void onHover(GUIElement element) {
        // System.out.println("hover");
    }

    @Override
    public void onHoverEnd(GUIElement element) {
    }

    @Override
    public void onMouseWheel(GUIElement element, MouseWheelEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && event.isUp()) {
            element.setSize(element.getSize().getX() * 1.1f, element.getSize().getY() * 1.1f);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && event.isDown()) {
            element.setSize(element.getSize().getX() * 0.9f, element.getSize().getY() * 0.9f);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && event.isUp()) {
            element.setAngle(element.getAngle() - 10f);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && event.isDown()) {
            element.setAngle(element.getAngle() + 10f);
        }
    }

    @Override
    public void onMouseHold(GUIElement element, MouseHoldEvent event) {
        // TODO Auto-generated method stub
    }

}
