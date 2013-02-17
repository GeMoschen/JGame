package de.gemo.game.manager.gui;

import java.awt.Font;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.core.Renderer;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIButton;
import de.gemo.engine.gui.GUICheckBox;
import de.gemo.engine.gui.GUIDropdownList;
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

            ExitButtonListener listener = new ExitButtonListener();

            GUIButton button = new GUIButton(1095, 975, animationButton);
            button.setLabel("Exit");
            button.setColor(normalColor);
            button.setHoverColor(hoverColor);
            button.setPressedColor(pressedColor);
            button.setMouseListener(listener);
            button.setFocusListener(listener);
            button.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            this.add(button);

            // CREATE LABEL
            GUILabel label = new GUILabel(1095, 845, "Textfeld:");
            label.setColor(Color.yellow);
            this.add(label);

            // CREATE TEXT-FIELD
            GUITextfield textfield = new GUITextfield(1095, 855, TextureManager.getTexture("EDIT_1"));
            textfield.setText("^^ Label!");
            textfield.setColor(Color.yellow);
            textfield.setFont(FontManager.getStandardFont());
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
            RadioGroup radioGroup1 = new RadioGroup();
            RadioGroup radioGroup2 = new RadioGroup();

            // ADD RADIOBUTTON 1
            GUIRadioButton radioButton1 = new GUIRadioButton(1095, 150, TextureManager.getTexture("RADIO_1"), "Radio 1.1");
            radioButton1.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            radioButton1.setGroup(radioGroup1);
            this.add(radioButton1);

            // ADD RADIOBUTTON 2
            GUIRadioButton radioButton2 = new GUIRadioButton(1095, 180, TextureManager.getTexture("RADIO_1"), "Radio 1.2");
            radioButton2.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            radioButton2.setGroup(radioGroup1);
            this.add(radioButton2);

            // ADD RADIOBUTTON 3
            GUIRadioButton radioButton3 = new GUIRadioButton(1095, 210, TextureManager.getTexture("RADIO_1"), "Radio 1.3");
            radioButton3.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            radioButton3.setGroup(radioGroup1);
            this.add(radioButton3);

            // ADD RADIOBUTTON 4
            GUIRadioButton radioButton4 = new GUIRadioButton(1095, 260, TextureManager.getTexture("RADIO_1"), "Radio 2.1");
            radioButton4.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            radioButton4.setGroup(radioGroup2);
            this.add(radioButton4);

            // ADD RADIOBUTTON 5
            GUIRadioButton radioButton5 = new GUIRadioButton(1095, 290, TextureManager.getTexture("RADIO_1"), "Radio 2.2");
            radioButton5.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            radioButton5.setGroup(radioGroup2);
            this.add(radioButton5);

            // ADD DROPDOWNLIST
            GUIDropdownList ddList = new GUIDropdownList(1095, 350, TextureManager.getTexture("DROPDOWN_1"), TextureManager.getTexture("DROPDOWN_1_ELEMENT"));
            ddList.setColor(normalColor);
            ddList.setHoverColor(hoverColor);
            ddList.setPressedColor(pressedColor);
            ddList.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            ddList.addItem("Element 1");
            ddList.addItem("Element 12345");
            ddList.addItem("Element 3");
            ddList.setSelectedItem(2);
            this.add(ddList);
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
