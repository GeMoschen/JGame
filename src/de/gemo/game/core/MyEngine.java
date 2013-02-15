package de.gemo.game.core;

import java.awt.Font;

import org.newdawn.slick.font.effects.GradientEffect;
import org.newdawn.slick.font.effects.OutlineEffect;
import org.newdawn.slick.font.effects.ShadowEffect;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.MouseManager;
import de.gemo.game.manager.gui.MyGUIManager1;
import de.gemo.game.manager.gui.MyGUIManager2;

public class MyEngine extends Engine {

    public MyEngine() {
        super("My Enginetest", 1280, 1024, false);
    }

    @Override
    protected void loadFonts() {
        FontManager.loadFont(FontManager.ANALOG, Font.PLAIN, 20, new OutlineEffect(2, java.awt.Color.black), new ShadowEffect(java.awt.Color.black, 2, 2, 0.5f), new GradientEffect(new java.awt.Color(255, 255, 255), new java.awt.Color(150, 150, 150), 1f));
        FontManager.loadFont(FontManager.ANALOG, Font.PLAIN, 24);
    }

    @Override
    protected void createManager() {
        this.setDebugMonitor(new ExtendedDebugMonitor());
    }

    @Override
    protected final void createGUI() {
        Hitbox hitbox = new Hitbox(550, 535);
        hitbox.addPoint(-530, -470);
        hitbox.addPoint(530, -470);
        hitbox.addPoint(530, 470);
        hitbox.addPoint(-530, 470);
        MyGUIManager2 manager = new MyGUIManager2("GUI2", hitbox, MouseManager.INSTANCE.getMouseVector(), -1);
        this.registerGUIManager(manager);

        hitbox = new Hitbox(1185, 512);
        hitbox.addPoint(-95, -447);
        hitbox.addPoint(95, -447);
        hitbox.addPoint(95, 512);
        hitbox.addPoint(-95, 512);
        this.registerGUIManager(new MyGUIManager1("GUI", hitbox, MouseManager.INSTANCE.getMouseVector(), 0));

        this.initGUIManager(this.getGUIManager("GUI2"));
        this.initGUIManager(this.getGUIManager("GUI"));
    }
}
