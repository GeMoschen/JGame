package de.gemo.game.events.gui.buttons;

import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.gui.GUIImageButton;
import de.gemo.engine.interfaces.listener.MouseListener;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.TileManager;

public class MainButtonListener implements MouseListener {

    GUIImageButton selectedButton = null;

    @Override
    public void onMouseClick(GUIElement element, MouseClickEvent event) {
    }

    private void select(String tileName, GUIImageButton button) {
        if (selectedButton != null) {
            selectedButton.setSelected(false);
        }
        TileManager.getTile(tileName).select();
        selectedButton = button;
        selectedButton.setSelected(true);
    }

    @Override
    public void onMouseRelease(GUIElement element, MouseReleaseEvent event) {
        GUIImageButton button = (GUIImageButton) element;
        if (button.getLabel().equalsIgnoreCase("House")) {
            IsoMap.SHOW_SECURITY = false;
            IsoMap.SHOW_POWER = false;
            this.select("tile_house_small_01", button);
        } else if (button.getLabel().equalsIgnoreCase("Streets")) {
            IsoMap.SHOW_SECURITY = false;
            IsoMap.SHOW_POWER = false;
            this.select("street_nw", button);
        } else if (button.getLabel().equalsIgnoreCase("Bulldozer")) {
            IsoMap.SHOW_SECURITY = false;
            IsoMap.SHOW_POWER = false;
            this.select("bulldozer", button);
        } else if (button.getLabel().equalsIgnoreCase("Power")) {
            IsoMap.SHOW_SECURITY = false;
            IsoMap.SHOW_POWER = true;
            this.select("powerplant_01", button);
        } else if (button.getLabel().equalsIgnoreCase("Police")) {
            IsoMap.SHOW_SECURITY = true;
            IsoMap.SHOW_POWER = false;
            this.select("police_01", button);
        }
    }

    @Override
    public void onMouseMove(GUIElement element, MouseMoveEvent event) {
        // System.out.println("mouse move: " + event.getX() + " / " + event.getY());
    }

    @Override
    public void onMouseDrag(GUIElement element, MouseDragEvent event) {
    }

}
