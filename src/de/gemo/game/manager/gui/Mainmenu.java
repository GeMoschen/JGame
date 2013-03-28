package de.gemo.game.manager.gui;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Renderer;
import de.gemo.engine.gui.GUIButton;
import de.gemo.engine.gui.GUIGraphic;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.GUIManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.units.Vector;
import de.gemo.game.events.gui.buttons.MainmenuButtonListener;

public class Mainmenu extends GUIManager {

    private GUIGraphic background;
    public Mainmenu(String name, Hitbox hitbox, Vector mouseVector, int z) {
        super(name, hitbox, mouseVector, z);
    }

    @Override
    protected void initGUI() {
        this.background = new GUIGraphic(0, 0, TextureManager.getTexture("MAINMENU_BG"));

        int startY = 250;
        int distance = 65;

        MainmenuButtonListener listener = new MainmenuButtonListener();

        // init newgame-button
        GUIButton newGameButton = this.createButton(startY, "Neues Spiel");
        newGameButton.setMouseListener(listener);
        this.add(newGameButton);

        // init loadgame-button
        GUIButton loadGameButton = this.createButton(startY + 1 * distance, "Spiel laden");
        loadGameButton.setMouseListener(listener);
        this.add(loadGameButton);

        // init settings-button
        GUIButton settingsButton = this.createButton(startY + 2 * distance, "Einstellungen");
        settingsButton.setMouseListener(listener);
        this.add(settingsButton);

        // init exitbutton
        GUIButton exitButton = this.createButton(startY + 4 * distance - (distance / 2), "Beenden");
        exitButton.setMouseListener(listener);
        this.add(exitButton);
    }

    private GUIButton createButton(int y, String label) {
        TrueTypeFont font = FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 26);
        GUIButton button = new GUIButton(262, y, TextureManager.getTexture("MAINMENU_BTN"));
        button.setLabel(label);
        button.setFont(font);
        button.setColor(Color.white);
        button.setHoverColor(Color.gray);
        button.setPressedColor(Color.darkGray);
        return button;
    }

    @Override
    public void render() {
        Renderer.render(this.background);
        super.render();
    }
}
