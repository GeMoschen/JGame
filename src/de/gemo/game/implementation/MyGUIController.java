package de.gemo.game.implementation;

import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.gemo.game.collision.Hitbox;
import de.gemo.game.core.Engine;
import de.gemo.game.core.FontManager;
import de.gemo.game.core.GUIController;
import de.gemo.game.entity.GUIButton;
import de.gemo.game.events.gui.buttons.ExitButtonListener;
import de.gemo.game.events.keyboard.KeyEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.interfaces.Vector;

public class MyGUIController extends GUIController {

    public MyGUIController(String name, Hitbox hitbox, Vector mouseVector) {
        super(name, hitbox, mouseVector);
    }

    @Override
    protected void init() {
        Texture buttonTexture;

        try {
            buttonTexture = TextureLoader.getTexture("JPG", new FileInputStream("test.jpg"));

            GUIButton button = new GUIButton(50 + 32, Engine.INSTANCE.getWindowHeight() - 32, buttonTexture);
            button.setZ(-3);
            button.setLabel("Button 1");
            button.setColor(Color.orange);
            button.setAlpha(0.1f);
            button.scale(0.25f);
            this.add(button);

            buttonTexture = TextureLoader.getTexture("JPG", new FileInputStream("test.jpg"));
            button = new GUIButton(180 + 32, Engine.INSTANCE.getWindowHeight() - 32, buttonTexture);
            button.setZ(-3);
            button.setLabel("Button 2");
            button.setColor(Color.orange);
            button.setAlpha(0.75f);
            button.scale(0.25f);
            this.add(button);

            button = new GUIButton(310 + 32, Engine.INSTANCE.getWindowHeight() - 32, buttonTexture);
            button.setZ(-3);
            button.setLabel("Testbutton with a text which is fucking too long! :{");
            button.setColor(Color.orange);
            button.setAlpha(1f);
            button.setFont(FontManager.getFont("Verdana", Font.BOLD, 14));
            button.scale(0.25f);
            this.add(button);

            button = new GUIButton(Engine.INSTANCE.getWindowWidth() - 80, Engine.INSTANCE.getWindowHeight() - 32, buttonTexture);
            button.setZ(-4);
            button.setLabel("Exit");
            button.setColor(Color.orange);
            button.setActionListener(new ExitButtonListener());
            button.setAlpha(0.25f);
            button.scale(0.25f);

            button.setFont(FontManager.getFont("Verdana", Font.BOLD, 14));

            this.add(button);

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
        switch (event.getKey()) {
            case Keyboard.KEY_LEFT : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().move(-0.1f * Engine.INSTANCE.getCurrentDelta(), 0);
                }
                break;
            }
            case Keyboard.KEY_RIGHT : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().move(0.1f * Engine.INSTANCE.getCurrentDelta(), 0);
                }
                break;
            }
            case Keyboard.KEY_UP : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().move(0, -0.1f * Engine.INSTANCE.getCurrentDelta());
                }
                break;
            }
            case Keyboard.KEY_DOWN : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().move(0, 0.1f * Engine.INSTANCE.getCurrentDelta());
                }
                break;
            }
            case Keyboard.KEY_W : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().setAlpha(this.getActiveElement().getAlpha() + 0.001f * Engine.INSTANCE.getCurrentDelta());
                }
                break;
            }
            case Keyboard.KEY_S : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().setAlpha(this.getActiveElement().getAlpha() - 0.001f * Engine.INSTANCE.getCurrentDelta());
                }
                break;
            }
            case Keyboard.KEY_1 : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().scale(1f - 0.002f * Engine.INSTANCE.getCurrentDelta());
                }
                break;
            }
            case Keyboard.KEY_2 : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().scale(1f + 0.002f * Engine.INSTANCE.getCurrentDelta());
                }
                break;
            }
        }
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        switch (event.getKey()) {
            case Keyboard.KEY_A : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().setAngle(this.getActiveElement().getAngle() - 45f);
                }
                break;
            }
            case Keyboard.KEY_D : {
                if (this.getActiveElement() != null) {
                    this.getActiveElement().setAngle(this.getActiveElement().getAngle() + 45f);
                }
                break;
            }
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        // TODO Auto-generated method stub

    }
}
