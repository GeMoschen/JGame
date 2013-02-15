package de.gemo.game.manager.gui;

import java.awt.Font;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.animation.SingleTexture;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.core.Renderer;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIButton;
import de.gemo.engine.gui.GUICheckBox;
import de.gemo.engine.gui.GUIGraphic;
import de.gemo.engine.gui.GUILabel;
import de.gemo.engine.gui.GUIRadioButton;
import de.gemo.engine.gui.GUITextfield;
import de.gemo.engine.gui.RadioGroup;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.GUIManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.units.Vector;
import de.gemo.game.events.gui.buttons.AddButtonListener;
import de.gemo.game.events.gui.buttons.ExitButtonListener;
import de.gemo.game.events.gui.buttons.RemoveButtonListener;

public class MyGUIManager1 extends GUIManager {

    private GUIGraphic gui, countdown, countdown2;
    private GUILabel lbl_position;
    private GUIButton btn_removeVertex;
    public boolean hotkeysActive = false;

    private MyGUIManager2 guiManager;

    public MyGUIManager1(String name, Hitbox hitbox, Vector mouseVector, int z) {
        super(name, hitbox, mouseVector, z);
    }

    public GUIButton getBtn_removeVertex() {
        return btn_removeVertex;
    }

    public GUILabel getLbl_position() {
        return lbl_position;
    }

    @Override
    protected void initManager() {
        guiManager = (MyGUIManager2) Engine.INSTANCE.getGUIManager("GUI2");
    }

    @Override
    protected void loadTextures() {
        try {
            // LOAD GUI TEXTURE
            SingleTexture guiTexture = TextureManager.loadSingleTexture("GUI_INGAME.png");
            TextureManager.addTexture("GUI_1", TextureManager.SingleToMultiTexture(guiTexture.crop(0, 0, 1280, 1024)));

            // LOAD COUNTDOWN TEXTURE
            MultiTexture countdownMultiTexture = new MultiTexture(72, 104);
            int y = 0;
            int x = 0;
            for (int i = 9; i >= 0; i--) {
                if (i == 4) {
                    y += countdownMultiTexture.getHeight();
                    x = 0;
                }
                countdownMultiTexture.addTextures(guiTexture.crop(1280 + x, y, countdownMultiTexture.getWidth(), countdownMultiTexture.getHeight()));
                x += countdownMultiTexture.getWidth();
            }
            TextureManager.addTexture("countdown", countdownMultiTexture);

            // LOAD TEXTURES FOR BUTTON
            SingleTexture buttonCompleteTexture = TextureManager.loadSingleTexture("test.jpg");
            SingleTexture buttonNormalTexture = buttonCompleteTexture.crop(0, 0, 175, 34);
            SingleTexture buttonHoverTexture = buttonCompleteTexture.crop(0, 0, 175, 34);
            SingleTexture buttonPressedTexture = buttonCompleteTexture.crop(0, 2 * 34, 175, 34);
            MultiTexture buttonMultiTexture = new MultiTexture(buttonNormalTexture.getWidth(), buttonNormalTexture.getHeight(), buttonNormalTexture, buttonHoverTexture, buttonPressedTexture);
            TextureManager.addTexture("BTN_1", buttonMultiTexture);

            // LOAD TEXTFIELD TEXTURE
            SingleTexture editTexture = TextureManager.loadSingleTexture("edit_normal.jpg", 0, 0, 175, 34);
            TextureManager.addTexture("EDIT_1", TextureManager.SingleToMultiTexture(editTexture));

            // LOAD CHECKBOX TEXTURE
            SingleTexture checkBoxRadioTexture = TextureManager.loadSingleTexture("gui_checkboxradio.png");
            SingleTexture checkBoxTextureOff = checkBoxRadioTexture.crop(0, 0, 21, 21);
            SingleTexture checkBoxTextureOn = checkBoxRadioTexture.crop(21, 0, 21, 21);
            MultiTexture checkBoxMultiTexture = new MultiTexture(21, 21, checkBoxTextureOff, checkBoxTextureOn);
            TextureManager.addTexture("CB_1", checkBoxMultiTexture);

            // LOAD RADIOBUTTON TEXTURE
            SingleTexture radioButtonTextureOff = checkBoxRadioTexture.crop(0, 21, 20, 20);
            SingleTexture radioButtonTextureOn = checkBoxRadioTexture.crop(20, 21, 20, 20);
            MultiTexture radioButtonMultiTexture = new MultiTexture(21, 21, radioButtonTextureOff, radioButtonTextureOn);
            TextureManager.addTexture("RADIO_1", radioButtonMultiTexture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initGUI() {
        try {
            // CREATE GUI
            gui = new GUIGraphic(0, 0, TextureManager.getTexture("GUI_1"));
            gui.setZ(0);

            // CREATE COUNTDOWNS
            countdown = new GUIGraphic(1110 + TextureManager.getTexture("countdown").getWidth(), 710, TextureManager.getTexture("countdown"));
            countdown.getAnimation().setWantedFPS(10);
            countdown2 = new GUIGraphic(1110, 710, TextureManager.getTexture("countdown"));

            // CREATE EXIT-BUTTON
            Animation animationButton = new Animation(TextureManager.getTexture("BTN_1"));
            Color normalColor = new Color(162, 162, 162);
            Color hoverColor = new Color(215, 165, 0);
            Color pressedColor = new Color(64, 64, 64);

            GUIButton button = new GUIButton(1095, 975, animationButton);
            button.setLabel("Exit");
            button.setColor(normalColor);
            button.setHoverColor(hoverColor);
            button.setPressedColor(pressedColor);
            ExitButtonListener listener = new ExitButtonListener();
            button.setMouseListener(listener);
            button.setFocusListener(listener);
            button.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            this.add(button);

            // CREATE LABEL
            GUILabel label = new GUILabel(1095, 845, "Textfeld:");
            label.setColor(Color.yellow);
            label.setMouseListener(listener);
            this.add(label);

            // CREATE TEXT-FIELD
            GUITextfield textfield = new GUITextfield(1095, 855, TextureManager.getTexture("EDIT_1"));
            textfield.setText("^^ Label!");
            textfield.setColor(Color.yellow);
            textfield.setFont(FontManager.getStandardFont());
            textfield.setMouseListener(listener);
            this.add(textfield);

            // ADD "Add Vertex"-Button
            AddButtonListener addListener = new AddButtonListener(guiManager.getVertexManager());
            GUIButton addButton = new GUIButton(1095, 935, animationButton);
            addButton.setLabel("Add Vertex");
            addButton.setColor(normalColor);
            addButton.setHoverColor(hoverColor);
            addButton.setPressedColor(pressedColor);
            addButton.setMouseListener(addListener);
            addButton.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            this.add(addButton);

            // ADD "Delete Vertex"-Button
            RemoveButtonListener removeListener = new RemoveButtonListener(guiManager.getVertexManager(), guiManager);
            btn_removeVertex = new GUIButton(1095, 895, animationButton);
            btn_removeVertex.setLabel("Delete Vertex");
            btn_removeVertex.setColor(normalColor);
            btn_removeVertex.setHoverColor(hoverColor);
            btn_removeVertex.setPressedColor(pressedColor);
            btn_removeVertex.setMouseListener(removeListener);
            btn_removeVertex.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            btn_removeVertex.setVisible(false);
            this.add(btn_removeVertex);

            // ADD VERTEX LABELS
            this.lbl_position = new GUILabel(1095, 72, "Position: N/A");
            this.lbl_position.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            this.add(lbl_position);

            // ADD CHECKBOX
            GUICheckBox checkbox = new GUICheckBox(1095, 100, TextureManager.getTexture("CB_1"), "Checkbox");
            checkbox.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            this.add(checkbox);

            // CREATE RADIO-GROUP
            RadioGroup radioGroup = new RadioGroup();

            // ADD RADIOBUTTON 1
            GUIRadioButton radioButton1 = new GUIRadioButton(1095, 150, TextureManager.getTexture("RADIO_1"), "Radio 1");
            radioButton1.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            radioButton1.setGroup(radioGroup);
            this.add(radioButton1);

            // ADD RADIOBUTTON 2
            GUIRadioButton radioButton2 = new GUIRadioButton(1095, 180, TextureManager.getTexture("RADIO_1"), "Radio 2");
            radioButton2.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            radioButton2.setGroup(radioGroup);
            this.add(radioButton2);

            // ADD RADIOBUTTON 3
            GUIRadioButton radioButton3 = new GUIRadioButton(1095, 210, TextureManager.getTexture("RADIO_1"), "Radio 3");
            radioButton3.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            radioButton3.setGroup(radioGroup);
            this.add(radioButton3);
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
    public void onMouseRelease(MouseReleaseEvent event) {
        if (!this.hasHoveredElement()) {
            guiManager.setSelectedVertex(null);
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
        Renderer.render(countdown);
        Renderer.render(countdown2);
        super.render();
    }
}
