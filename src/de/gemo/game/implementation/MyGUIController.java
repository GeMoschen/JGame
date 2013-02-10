package de.gemo.game.implementation;

import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.gemo.game.animation.Animation;
import de.gemo.game.animation.MultiTexture;
import de.gemo.game.animation.SingleTexture;
import de.gemo.game.collision.Hitbox;
import de.gemo.game.core.Engine;
import de.gemo.game.core.FontManager;
import de.gemo.game.core.GUIController;
import de.gemo.game.entity.GUIButton;
import de.gemo.game.entity.GUITextfield;
import de.gemo.game.events.gui.buttons.ExitButtonListener;
import de.gemo.game.events.keyboard.KeyEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.interfaces.Vector;

public class MyGUIController extends GUIController {

    public MyGUIController(String name, Hitbox hitbox, Vector mouseVector) {
        super(name, hitbox, mouseVector);
    }

    public boolean hotkeysActive = false;

    @Override
    protected void init() {
        Texture buttonTexture;

        try {
            buttonTexture = TextureLoader.getTexture("JPG", new FileInputStream("test.jpg"));

            Color normalColor = new Color(162, 162, 162);
            Color hoverColor = new Color(215, 165, 0);
            Color pressedColor = new Color(64, 64, 64);

            MultiTexture multiTexture = new MultiTexture(175, 34);
            multiTexture.addTextures(new SingleTexture(buttonTexture, 0, 0, 175, 34), new SingleTexture(buttonTexture, 0, 0, 175, 34), new SingleTexture(buttonTexture, 0, 2 * 34, 175, 34));
            Animation animation = new Animation(multiTexture);

            GUIButton button = new GUIButton(20, Engine.INSTANCE.getWindowHeight() - 80, animation);
            button.setZ(-8);
            button.setLabel("Button 1");
            button.setColor(normalColor);
            button.setHoverColor(hoverColor);
            button.setPressedColor(pressedColor);
            button.setFont(FontManager.getFont(FontManager.DIN, Font.PLAIN, 20));
            this.add(button);

            button = new GUIButton(200, Engine.INSTANCE.getWindowHeight() - 80, animation);
            button.setZ(-2);
            button.setLabel("Button 2");
            button.setColor(normalColor);
            button.setHoverColor(hoverColor);
            button.setPressedColor(pressedColor);
            button.setFont(FontManager.getFont(FontManager.DIN, Font.PLAIN, 20));
            this.add(button);

            button = new GUIButton(380, Engine.INSTANCE.getWindowHeight() - 80, animation);
            button.setZ(-4);
            button.setLabel("Exit");
            button.setColor(normalColor);
            button.setHoverColor(hoverColor);
            button.setPressedColor(pressedColor);
            button.setActionListener(new ExitButtonListener());
            button.setFont(FontManager.getFont(FontManager.DIN, Font.PLAIN, 20));
            this.add(button);

            // textfield
            buttonTexture = TextureLoader.getTexture("JPG", new FileInputStream("edit_normal.jpg"));
            SingleTexture singleTexture = new SingleTexture(buttonTexture, 0, 0, 256, 34);
            GUITextfield textfield = new GUITextfield(200, 250, singleTexture);
            textfield.setFont(FontManager.getStandardFont());
            this.add(textfield);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
    }

    @Override
    public void onMouseDrag(MouseDragEvent event) {
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
    public void onKeyPressed(KeyEvent event) {
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_F8) {
            this.hotkeysActive = !this.hotkeysActive;
        }
    }
}
