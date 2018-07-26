package de.gemo.game.manager.gui;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.gui.GUIButton;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.GUIManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.units.Vector;
import de.gemo.game.events.gui.buttons.MainmenuButtonListener;

import static org.lwjgl.opengl.GL11.*;

public class GamePausedMenu extends GUIManager {

    public GamePausedMenu(String name, Hitbox hitbox, Vector mouseVector, int z) {
        super(name, hitbox, mouseVector, z);
    }

    @Override
    protected void initGUI() {
        int startY = 300;
        int distance = 85;

        MainmenuButtonListener listener = new MainmenuButtonListener();

        // init newgame-button
        GUIButton newGameButton = this.createButton(startY, "Fortfahren");
        newGameButton.setMouseListener(listener);
        this.add(newGameButton);

        // init savegame-button
        GUIButton saveGameButton = this.createButton(startY + 1 * distance, "Speichern");
        saveGameButton.setMouseListener(listener);
        this.add(saveGameButton);

        // init settings-button
        GUIButton loadGameButton = this.createButton(startY + 2 * distance, "Laden");
        loadGameButton.setMouseListener(listener);
        this.add(loadGameButton);

        // init settings
        GUIButton settingsButton = this.createButton(startY + 3 * distance, "Einstellungen");
        settingsButton.setMouseListener(listener);
        this.add(settingsButton);

        // init exitbutton
        GUIButton exitButton = this.createButton(startY + 4 * distance, "Beenden");
        exitButton.setMouseListener(listener);
        this.add(exitButton);
    }

    private GUIButton createButton(int y, String label) {
        TrueTypeFont font = FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 26);
        GUIButton button = new GUIButton(0, y, TextureManager.getTexture("MAINMENU_BTN"));
        button.setPositionOnScreen(1280 / 2 - button.getWidth() / 2, button.getYOnScreen());
        button.setLabel(label);
        button.setFont(font);
        button.setColor(Color.white);
        button.setHoverColor(Color.gray);
        button.setPressedColor(Color.darkGray);
        return button;
    }

    @Override
    public void render() {
        super.render();
    }
}
