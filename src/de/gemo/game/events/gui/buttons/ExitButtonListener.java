package de.gemo.game.events.gui.buttons;

import de.gemo.engine.core.Engine;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.gui.GUILabel;
import de.gemo.engine.interfaces.listener.FocusListener;

public class ExitButtonListener extends ButtonMoveListener implements FocusListener {

    private int counter = 0;
    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
        System.out.println("mouse click");
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
        System.out.println("mouse release");
        if (event.isLeftButton()) {
            Engine.close();
        } else {
            if (element instanceof GUILabel) {
                GUILabel label = (GUILabel) element;
                label.setLabel(label.getLabel() + counter);
                counter++;
            }
        }
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
        // System.out.println("mouse move: " + event.getX() + " / " + event.getY());
    }

    @Override
    public void onFocusGained(GUIElement element) {
        // System.out.println("focus gained");
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
        System.out.println("hover end!");
    }

}
