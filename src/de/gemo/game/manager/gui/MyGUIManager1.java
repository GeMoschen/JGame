package de.gemo.game.manager.gui;

import java.awt.Font;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.core.Renderer;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.gui.GUIButton;
import de.gemo.engine.gui.GUIButtonScalable;
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
import de.gemo.engine.textures.Animation;
import de.gemo.engine.units.Vector;
import de.gemo.game.events.gui.buttons.ExitButtonListener;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoMap_1;
import de.gemo.game.tile.StreetManager;
import de.gemo.game.tile.TileDimension;
import de.gemo.game.tile.TileManager;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public class MyGUIManager1 extends GUIManager {

    private GUIGraphic gui, countdown, countdown2;
    private GUILabel lbl_position;
    private GUIButtonScalable btn_exit, btn_removeVertex, btn_add;
    public boolean hotkeysActive = false;

    public static IsoMap isoMap;

    public static int mouseTileX = 0, mouseTileY = 0;
    private float tX = 0, tY = 0;

    private int minShowX = 0, maxShowX = 0, minShowY = 0, maxShowY = 0;

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
    protected void initGUI() {
        try {
            // CREATE GUI
            gui = new GUIGraphic(0, 0, TextureManager.getTexture("GUI"));
            gui.setZ(10);

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
            btn_exit = new GUIButtonScalable(685, 565, animationButton);
            btn_exit.setLabel("Exit");
            btn_exit.setPressedColor(Color.gray);
            btn_exit.setMouseListener(listener);
            btn_exit.setFocusListener(listener);
            this.add(btn_exit);

            // CREATE LABEL
            GUILabel label = new GUILabel(1095, 845, "Textfeld:");
            label.setColor(Color.yellow);
            this.add(label);

            // CREATE TEXT-FIELD
            GUITextfield textfield = new GUITextfield(1000, 565, TextureManager.getTexture("EDIT_1"));
            textfield.setText("^^ Label!");
            textfield.setColor(Color.yellow);
            textfield.setFont(FontManager.getStandardFont());
            this.add(textfield);

            // ADD "Delete Vertex"-Button
            btn_removeVertex = new GUIButtonScalable(14 + 300 + 5, 7, animationButton);
            btn_removeVertex.setLabel("Small button");
            btn_removeVertex.setWidth(150);
            btn_removeVertex.setPressedColor(Color.gray);
            this.add(btn_removeVertex);

            // ADD "Add Vertex"-Button
            btn_add = new GUIButtonScalable(14, 7, animationButton);
            btn_add.setWidth(300);
            btn_add.setLabel("Large Button");
            btn_add.setPressedColor(Color.gray);
            this.add(btn_add);

            // ADD VERTEX LABELS
            this.lbl_position = new GUILabel(1095, 72, "Position: N/A");
            this.lbl_position.setFont(FontManager.getFont(FontManager.ANALOG, Font.PLAIN, 20));
            this.add(lbl_position);

            // ADD CHECKBOX
            GUICheckBox checkbox = new GUICheckBox(685, 60, TextureManager.getTexture("CB_1"), "Checkbox 1");
            this.add(checkbox);
            checkbox = new GUICheckBox(685, 80, TextureManager.getTexture("CB_1"), "Checkbox 2");
            this.add(checkbox);
            checkbox = new GUICheckBox(685, 100, TextureManager.getTexture("CB_1"), "Checkbox 3");
            this.add(checkbox);

            // CREATE RADIO-GROUP
            RadioGroup radioGroup1 = new RadioGroup();
            RadioGroup radioGroup2 = new RadioGroup();

            // ADD RADIOBUTTON 1
            GUIRadioButton radioButton1 = new GUIRadioButton(685, 140, TextureManager.getTexture("RADIO_1"), "Radio 1.1");
            radioButton1.setGroup(radioGroup1);
            this.add(radioButton1);

            // ADD RADIOBUTTON 2
            GUIRadioButton radioButton2 = new GUIRadioButton(685, 160, TextureManager.getTexture("RADIO_1"), "Radio 1.2");
            radioButton2.setGroup(radioGroup1);
            this.add(radioButton2);

            // ADD RADIOBUTTON 3
            GUIRadioButton radioButton3 = new GUIRadioButton(685, 180, TextureManager.getTexture("RADIO_1"), "Radio 1.3");
            radioButton3.setGroup(radioGroup1);
            this.add(radioButton3);

            // ADD RADIOBUTTON 4
            GUIRadioButton radioButton4 = new GUIRadioButton(685, 220, TextureManager.getTexture("RADIO_1"), "Radio 2.1");
            radioButton4.setGroup(radioGroup2);
            this.add(radioButton4);

            // ADD RADIOBUTTON 5
            GUIRadioButton radioButton5 = new GUIRadioButton(685, 240, TextureManager.getTexture("RADIO_1"), "Radio 2.2");
            radioButton5.setGroup(radioGroup2);
            this.add(radioButton5);

            // ADD DROPDOWNLIST
            GUIDropdownList ddList = new GUIDropdownList(1095, 350, TextureManager.getTexture("DROPDOWN_1"), TextureManager.getTexture("DROPDOWN_1_ELEMENT"));
            ddList.setColor(normalColor);
            ddList.setHoverColor(hoverColor);
            ddList.setPressedColor(pressedColor);
            ddList.addItem("Element 1");
            ddList.addItem("Element 12345");
            ddList.addItem("Element 3");
            ddList.addItem("Element 4");
            ddList.setSelectedItem(2);
            this.add(ddList);

            isoMap = new IsoMap_1(100, 100, 64, 32, 20, 34, 650, 550);
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
        } else if (event.getKey() == Keyboard.KEY_1) {
            TileManager.getTile("quarder_1").select();
        } else if (event.getKey() == Keyboard.KEY_2) {
            TileManager.getTile("street_nw").select();
        } else if (event.getKey() == Keyboard.KEY_3) {
            TileManager.getTile("bulldozer").select();
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
        glPushMatrix();
        {
            this.isoMap.render(this.minShowX, this.maxShowX, this.minShowY, this.maxShowY);
            glTranslatef(this.isoMap.getOffsetX(), this.isoMap.getOffsetY() + this.isoMap.getHalfTileHeight(), 0);
            glTranslatef(tX, tY, this.gui.getZ());
            TileDimension.render(mouseTileX, mouseTileY, isoMap);
        }
        glPopMatrix();

        gui.setAlpha(1f);
        Renderer.render(gui);
        // Renderer.render(countdown);
        // Renderer.render(countdown2);
        super.render();
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        mouseTileX = this.isoMap.getTileX(this.mouseVector.getX(), this.mouseVector.getY(), false);
        mouseTileY = this.isoMap.getTileY(this.mouseVector.getX(), this.mouseVector.getY(), false);
        tX = this.isoMap.getIsoX(mouseTileX, mouseTileY);
        tY = this.isoMap.getIsoY(mouseTileX, mouseTileY);
        TileDimension.isFree(mouseTileX, mouseTileY, isoMap);
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
        if (event.getX() > 12 && event.getY() > 36 && event.getX() < 675 && event.getY() < 589) {
            if (event.isLeftButton() && (TileDimension.isFree)) {
                TileDimension.place(mouseTileX, mouseTileY, isoMap);
            }
        }
    }

    @Override
    public void onMouseDrag(MouseDragEvent event) {
        if (event.isRightButton()) {
            if (event.getX() > 12 && event.getY() > 36 && event.getX() < 675 && event.getY() < 589) {
                float offsetX = this.isoMap.getOffsetX() + event.getDifX();
                float offsetY = this.isoMap.getOffsetY() + event.getDifY();
                this.isoMap.setOffset(offsetX, offsetY);
            }
        } else if (event.isLeftButton() && TileDimension.isFree) {
            if (event.getX() > 12 && event.getY() > 36 && event.getX() < 675 && event.getY() < 589) {
                TileDimension.place(mouseTileX, mouseTileY, isoMap);
            }
        }
    }
}
