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
        int startY = 160;
        int distance = 65;

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
        float color = 0f;
        float color2 = 0.2f;
        glDisable(GL_TEXTURE_2D);
        // first quad
        glBegin(GL_QUADS);
        glColor4f(color, color, color, 1f);
        glVertex2i(0, 0);
        glColor4f(color2, color2, color2, 0.7f);
        glVertex2i(400, 0);
        glColor4f(color2, color2, color2, 0.4f);
        glVertex2i(400, 300);
        glColor4f(color2, color2, color2, 0.7f);
        glVertex2i(0, 300);
        glEnd();

        // second quad
        glBegin(GL_QUADS);
        glColor4f(color, color, color, 1f);
        glVertex2i(800, 0);
        glColor4f(color2, color2, color2, 0.7f);
        glVertex2i(400, 0);
        glColor4f(color2, color2, color2, 0.4f);
        glVertex2i(400, 300);
        glColor4f(color2, color2, color2, 0.7f);
        glVertex2i(800, 300);
        glEnd();

        // third quad
        glBegin(GL_QUADS);
        glColor4f(color, color, color, 1f);
        glVertex2i(0, 600);
        glColor4f(color2, color2, color2, 0.7f);
        glVertex2i(400, 600);
        glColor4f(color2, color2, color2, 0.4f);
        glVertex2i(400, 300);
        glColor4f(color2, color2, color2, 0.7f);
        glVertex2i(0, 300);
        glEnd();

        // fourth quad
        glBegin(GL_QUADS);
        glColor4f(color, color, color, 1f);
        glVertex2i(800, 600);
        glColor4f(color2, color2, color2, 0.7f);
        glVertex2i(400, 600);
        glColor4f(color2, color2, color2, 0.4f);
        glVertex2i(400, 300);
        glColor4f(color2, color2, color2, 0.7f);
        glVertex2i(800, 300);
        glEnd();
        super.render();
    }
}
