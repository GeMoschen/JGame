package de.gemo.game.controller;

import java.awt.Font;
import java.io.FileInputStream;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.animation.SingleTexture;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.core.FontManager;
import de.gemo.engine.core.GUIController;
import de.gemo.engine.core.Renderer;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.gui.GUIButton;
import de.gemo.engine.gui.GUIGraphic;
import de.gemo.engine.units.Vector;
import de.gemo.game.events.gui.buttons.ExitButtonListener;

public class MyGUIController extends GUIController {

    private GUIGraphic gui, countdown, countdown2;

    public MyGUIController(String name, Hitbox hitbox, Vector mouseVector) {
        super(name, hitbox, mouseVector);
    }

    public boolean hotkeysActive = false;

    @Override
    protected void init() {
        try {
            Texture guiTexture = TextureLoader.getTexture("PNG", new FileInputStream("GUI_INGAME.png"));
            SingleTexture singleTexture = new SingleTexture(guiTexture, 0, 0, 1280, 1024);
            gui = new GUIGraphic(640, 512, singleTexture);
            gui.setZ(0);

            Texture buttonTexture = TextureLoader.getTexture("JPG", new FileInputStream("test.jpg"));

            Color normalColor = new Color(162, 162, 162);
            Color hoverColor = new Color(215, 165, 0);
            Color pressedColor = new Color(64, 64, 64);

            MultiTexture multiTexture = new MultiTexture(175, 34);
            multiTexture.addTextures(new SingleTexture(buttonTexture, 0, 0, 175, 34), new SingleTexture(buttonTexture, 0, 0, 175, 34), new SingleTexture(buttonTexture, 0, 2 * 34, 175, 34));
            Animation animation = new Animation(multiTexture);

            GUIButton button = new GUIButton(1181, 990, animation);
            button.setLabel("Exit");
            button.setColor(normalColor);
            button.setHoverColor(hoverColor);
            button.setPressedColor(pressedColor);
            ExitButtonListener listener = new ExitButtonListener();
            button.setMouseListener(listener);
            button.setFocusListener(listener);
            button.setFont(FontManager.getFont(FontManager.VERDANA, Font.PLAIN, 20));
            this.add(button);

            // LOAD ANIMATION
            multiTexture = new MultiTexture(72, 104);
            int y = 0;
            int x = 0;
            for (int i = 9; i >= 0; i--) {
                if (i == 4) {
                    y += multiTexture.getHeight();
                    x = 0;
                }
                multiTexture.addTextures(new SingleTexture(guiTexture, 1280 + x, y, 72, 104));
                x += multiTexture.getWidth();
            }
            animation = new Animation(multiTexture);
            countdown = new GUIGraphic(1181 + 36, 900, animation);
            countdown.getAnimation().setWantedFPS(10);
            this.add(countdown);

            countdown2 = new GUIGraphic(1181 - 36, 900, animation);
            this.add(countdown2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onKeyHold(KeyEvent event) {
        if (hotkeysActive) {
            switch (event.getKey()) {
                case Keyboard.KEY_LEFT : {
                    if (this.getHoveredElement() != null) {
                        this.getHoveredElement().rotate(-0.2f * Engine.INSTANCE.getCurrentDelta());
                    }
                    break;
                }
                case Keyboard.KEY_RIGHT : {
                    if (this.getHoveredElement() != null) {
                        this.getHoveredElement().rotate(+0.2f * Engine.INSTANCE.getCurrentDelta());
                    }
                    break;
                }
                case Keyboard.KEY_UP : {
                    if (this.getHoveredElement() != null) {
                        float rad = (float) Math.toRadians(this.getHoveredElement().getAngle() - 90);
                        float x = (float) Math.cos(rad) / 6f;
                        float y = (float) Math.sin(rad) / 6f;
                        this.getHoveredElement().move(x * Engine.INSTANCE.getCurrentDelta(), y * Engine.INSTANCE.getCurrentDelta());
                    }
                    break;
                }
                case Keyboard.KEY_DOWN : {
                    if (this.getHoveredElement() != null) {
                        float rad = (float) Math.toRadians(this.getHoveredElement().getAngle() - 90);
                        float x = -(float) Math.cos(rad) / 6f;
                        float y = -(float) Math.sin(rad) / 6f;
                        this.getHoveredElement().move(x * Engine.INSTANCE.getCurrentDelta(), y * Engine.INSTANCE.getCurrentDelta());
                    }
                    break;
                }
                case Keyboard.KEY_W : {
                    if (this.getHoveredElement() != null) {
                        this.getHoveredElement().setAlpha(this.getHoveredElement().getAlpha() + 0.001f * Engine.INSTANCE.getCurrentDelta());
                    }
                    break;
                }
                case Keyboard.KEY_S : {
                    if (this.getHoveredElement() != null) {
                        this.getHoveredElement().setAlpha(this.getHoveredElement().getAlpha() - 0.001f * Engine.INSTANCE.getCurrentDelta());
                    }
                    break;
                }
                case Keyboard.KEY_1 : {
                    if (this.getHoveredElement() != null) {
                        this.getHoveredElement().scale(1f - 0.002f * Engine.INSTANCE.getCurrentDelta(), 1f - 0.002f * Engine.INSTANCE.getCurrentDelta());
                    }
                    break;
                }
                case Keyboard.KEY_2 : {
                    if (this.getHoveredElement() != null) {
                        this.getHoveredElement().scale(1f + 0.002f * Engine.INSTANCE.getCurrentDelta(), 1f + 0.002f * Engine.INSTANCE.getCurrentDelta());
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_F8) {
            this.hotkeysActive = !this.hotkeysActive;
        }
    }

    @Override
    public void doTick(float delta) {
        if (countdown.getAnimation().step(delta)) {
            countdown2.getAnimation().nextFrame();
        }
    }

    @Override
    public void render() {
        Renderer.render(gui);
        // for (int i = 0; i < 1000; i++)
        super.render();
    }

}