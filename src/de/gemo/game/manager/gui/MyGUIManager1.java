package de.gemo.game.manager.gui;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.core.Renderer;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIButton;
import de.gemo.engine.gui.GUIGraphic;
import de.gemo.engine.gui.GUIImageButton;
import de.gemo.engine.manager.GUIManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.textures.Animation;
import de.gemo.engine.units.Vector;
import de.gemo.game.events.gui.buttons.ExitButtonListener;
import de.gemo.game.events.gui.buttons.MainButtonListener;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoMap_1;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileDimension;
import de.gemo.game.tile.TileManager;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public class MyGUIManager1 extends GUIManager {

    private GUIGraphic gui;
    private GUIButton btn_exit;
    private GUIImageButton btn_plant, btn_police, btn_house, btn_street, btn_bulldozer;
    public boolean hotkeysActive = false;

    public IsoMap isoMap;

    public static int mouseTileX = 0, mouseTileY = 0, lastTileX = -1, lastTileY = -1;
    private boolean inDragBuild = false, updatePath = false;
    private Path path = null;
    private float tX = 0, tY = 0;

    private int downMouseX, downMouseY;

    public MyGUIManager1(String name, Hitbox hitbox, Vector mouseVector, int z) {
        super(name, hitbox, mouseVector, z);
    }

    @Override
    protected void initGUI() {
        try {
            // CREATE GUI
            gui = new GUIGraphic(800 - 82, 0, TextureManager.getTexture("GUI"));
            gui.setZ(10);

            // CREATE EXIT-BUTTON
            Animation animationButton = new Animation(TextureManager.getTexture("BTN_GAME_MAIN"));

            ExitButtonListener listener = new ExitButtonListener();
            btn_exit = new GUIButton(727, 527, animationButton);
            btn_exit.setLabel("Exit");
            btn_exit.setColor(Color.white);
            btn_exit.setHoverColor(Color.lightGray);
            btn_exit.setPressedColor(Color.gray);
            btn_exit.setMouseListener(listener);
            this.add(btn_exit);

            // CREATE PLANT BUTTON
            Animation animationButtonIcons = new Animation(TextureManager.getTexture("BTN_GAME_MAIN_ICONS"));

            MainButtonListener buttonListener = new MainButtonListener();
            btn_plant = new GUIImageButton(727, 8, animationButton, animationButtonIcons, 0);
            btn_plant.setMouseListener(buttonListener);
            btn_plant.setLabel("Power");
            this.add(btn_plant);

            // CREATE POLICE BUTTON
            btn_police = new GUIImageButton(727, 8 + 1 * 76, animationButton, animationButtonIcons, 1);
            btn_police.setMouseListener(buttonListener);
            btn_police.setLabel("Police");
            this.add(btn_police);

            // CREATE HOUSE BUTTON
            btn_house = new GUIImageButton(727, 8 + 2 * 76, animationButton, animationButtonIcons, 2);
            btn_house.setMouseListener(buttonListener);
            btn_house.setLabel("House");
            this.add(btn_house);

            // CREATE STREET BUTTON
            btn_street = new GUIImageButton(727, 8 + 3 * 76, animationButton, animationButtonIcons, 3);
            btn_street.setMouseListener(buttonListener);
            btn_street.setLabel("Streets");
            this.add(btn_street);

            // CREATE BULLDOZER BUTTON
            btn_bulldozer = new GUIImageButton(727, 527 - 1 * 76, animationButton, animationButtonIcons, 4);
            btn_bulldozer.setMouseListener(buttonListener);
            btn_bulldozer.setLabel("Bulldozer");
            this.add(btn_bulldozer);

            isoMap = new IsoMap_1(100, 100, 64, 32, 0, 0, 760, 630);
            TileDimension.setIsoMap(isoMap);

            TileManager.getTile("street_nw").select();
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
    public void render() {
        glPushMatrix();
        {
            this.isoMap.render();
            glTranslatef(this.isoMap.getOffsetX(), this.isoMap.getOffsetY(), 0);
            glPushMatrix();
            {
                glTranslatef(tX, tY, this.gui.getZ());
                TileDimension.render(mouseTileX, mouseTileY, isoMap);
            }
            glPopMatrix();
        }
        glPopMatrix();

        gui.setAlpha(1f);
        Renderer.render(gui);
        // Renderer.render(countdown);
        // Renderer.render(countdown2);
        this.renderPath();
        super.render();
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        mouseTileX = this.isoMap.getTileX(this.mouseVector.getX(), this.mouseVector.getY());
        mouseTileY = this.isoMap.getTileY(this.mouseVector.getX(), this.mouseVector.getY());
        tX = this.isoMap.getIsoX(mouseTileX, mouseTileY);
        tY = this.isoMap.getIsoY(mouseTileX, mouseTileY);
        TileDimension.isFree(mouseTileX, mouseTileY, isoMap);

        mouseTileX = Math.min(Math.max(0, mouseTileX), this.isoMap.getWidth() - 1);
        mouseTileY = Math.min(Math.max(0, mouseTileY), this.isoMap.getHeight() - 1);

        if (lastTileX != mouseTileX || lastTileY != mouseTileY) {
            this.updatePath = true;
        }
        lastTileX = mouseTileX;
        lastTileY = mouseTileY;

        if (!(event.getX() > 0 && event.getY() > 0 && event.getX() < 800 - 82 && event.getY() < 600)) {
            this.inDragBuild = false;
        }
    }
    private void renderPath() {
        if (inDragBuild && TileDimension.getSelectedTile().getType().isDraggable()) {
            if (updatePath) {
                // we have a street => calculate path and place streets
                path = this.isoMap.getBuildingPath(downMouseX, downMouseY, mouseTileX, mouseTileY);
            }
            updatePath = false;
            // TileDimension.place(downMouseX, downMouseY, isoMap);
            IsoTile streetTile = TileManager.getTile("white");
            if (path != null) {
                int tX = this.isoMap.getIsoX(downMouseX, downMouseY);
                int tY = this.isoMap.getIsoY(downMouseX, downMouseY);
                glPushMatrix();
                {
                    glTranslatef(isoMap.getOffsetX(), isoMap.getOffsetY() + isoMap.getHalfTileHeight(), 0);
                    glTranslatef(tX, tY, 0);
                    streetTile.render();
                }
                glPopMatrix();
                Step node;
                for (int i = 0; i < path.getLength(); i++) {
                    node = path.getStep(i);
                    tX = this.isoMap.getIsoX(node.getX(), node.getY());
                    tY = this.isoMap.getIsoY(node.getX(), node.getY());
                    glPushMatrix();
                    {
                        glTranslatef(isoMap.getOffsetX(), isoMap.getOffsetY() + isoMap.getHalfTileHeight(), 0);
                        glTranslatef(tX, tY, 0);
                        streetTile.render();
                    }
                    glPopMatrix();
                }
            }
        }
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
        if (event.getX() > 0 && event.getY() > 0 && event.getX() < 800 - 82 && event.getY() < 600) {
            if (event.isLeftButton()) {
                if (TileDimension.isFree() || (!TileDimension.isFree() && TileDimension.getSelectedTile().getType().equals(TileType.BULLDOZER))) {
                    downMouseX = mouseTileX;
                    downMouseY = mouseTileY;
                    inDragBuild = true;
                } else {
                    downMouseX = -1;
                    downMouseY = -1;
                    inDragBuild = false;
                }
            }
        }
    }

    @Override
    public void onMouseRelease(MouseReleaseEvent event) {
        if (event.getX() > 0 && event.getY() > 0 && event.getX() < 800 - 82 && event.getY() < 600) {
            if (event.isLeftButton()) {
                inDragBuild = false;
                if ((TileDimension.isFree() || (!TileDimension.isFree() && TileDimension.getSelectedTile().getType().equals(TileType.BULLDOZER))) && downMouseX > -1 && downMouseY > -1) {
                    if (!TileDimension.getSelectedTile().getType().isDraggable()) {
                        downMouseX = mouseTileX;
                        downMouseY = mouseTileY;
                        TileDimension.place(mouseTileX, mouseTileY, isoMap);
                        return;
                    } else {
                        // we have a street => calculate path and place streets
                        int upMouseX = this.isoMap.getTileX(this.mouseVector.getX(), this.mouseVector.getY());
                        int upMouseY = this.isoMap.getTileY(this.mouseVector.getX(), this.mouseVector.getY());
                        if (upMouseX == downMouseX && upMouseY == downMouseY && path == null) {
                            TileDimension.place(downMouseX, downMouseY, isoMap);
                        }
                        if (path != null) {
                            TileDimension.place(downMouseX, downMouseY, isoMap);
                            for (int i = 0; i < path.getLength(); i++) {
                                Step node = path.getStep(i);
                                TileDimension.place(node.getX(), node.getY(), isoMap);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMouseDrag(MouseDragEvent event) {
        if (event.isRightButton()) {
            if (event.getX() > 0 && event.getY() > 0 && event.getX() < 800 - 82 && event.getY() < 600) {
                float offsetX = this.isoMap.getOffsetX() + event.getDifX();
                float offsetY = this.isoMap.getOffsetY() + event.getDifY();
                this.isoMap.setOffset(offsetX, offsetY);
            }
        }
    }
}
