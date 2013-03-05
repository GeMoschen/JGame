package de.gemo.game.events.gui.buttons;

import de.gemo.engine.core.Engine;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIButton;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.interfaces.listener.MouseListener;
import de.gemo.game.core.Minetown;
import de.gemo.game.gamestates.GameState;

public class MainmenuButtonListener implements MouseListener {

    private Minetown minetown;

    public MainmenuButtonListener() {
        this.minetown = (Minetown) Engine.INSTANCE;
    }

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
        if (!event.isLeftButton()) {
            return;
        }

        GUIButton button = (GUIButton) element;
        if (button.getLabel().equals("Beenden")) {
            if (this.minetown.getGameState().equals(GameState.MAIN_MENU)) {
                Engine.close();
            } else if (this.minetown.getGameState().equals(GameState.GAME_PAUSED)) {
                Minetown.setGameState(GameState.MAIN_MENU);
            }
        } else if (button.getLabel().equals("Neues Spiel") || button.getLabel().equalsIgnoreCase("Fortfahren")) {
            Minetown.setGameState(GameState.GAME);
        }
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
    }

    @Override
    public void onMouseDrag(GUIElement element, MouseDragEvent event) {
    }

}
