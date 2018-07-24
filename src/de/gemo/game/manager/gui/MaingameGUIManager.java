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
import de.gemo.engine.events.mouse.MouseWheelEvent;
import de.gemo.engine.gui.GUIButton;
import de.gemo.engine.gui.GUIGraphic;
import de.gemo.engine.gui.GUIImageButton;
import de.gemo.engine.manager.GUIManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.textures.Animation;
import de.gemo.engine.units.Vector;
import de.gemo.game.core.Minetown;
import de.gemo.game.events.gui.buttons.ExitButtonListener;
import de.gemo.game.events.gui.buttons.MainButtonListener;
import de.gemo.game.gamestates.GameState;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileDimension;
import de.gemo.game.tile.manager.TileManager;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public class MaingameGUIManager extends GUIManager {

    private GUIGraphic gui;
    private GUIButton btn_exit;
    private GUIImageButton btn_plant, btn_police, btn_house, btn_street, btn_powerline, btn_bulldozer;
    public boolean hotkeysActive = false;

    public final IsoMap isoMap;
    private Minetown minetown;

    public static int mouseTileX = 0, mouseTileY = 0, lastTileX = -1, lastTileY = -1;
    private boolean inDragBuild = false, updatePath = false;
    private Path path = null;
    private float tX = 0, tY = 0;

    private int downMouseX, downMouseY;

    public MaingameGUIManager(String name, Hitbox hitbox, Vector mouseVector, int z, IsoMap isoMap) {
        super(name, hitbox, mouseVector, z);
        this.isoMap = isoMap;

    }

    @Override
    protected void initGUI() {
        this.minetown = (Minetown) Engine.INSTANCE;
        try {
            // CREATE GUI
            gui = new GUIGraphic(1280 - 82, 0, TextureManager.getTexture("GUI"));
            gui.setZ(10);

            // CREATE EXIT-BUTTON
            Animation animationButton = new Animation(TextureManager.getTexture("BTN_GAME_MAIN"));

            ExitButtonListener listener = new ExitButtonListener();
            btn_exit = new GUIButton(1207, 527, animationButton);
            btn_exit.setLabel("Pause");
            btn_exit.setColor(Color.white);
            btn_exit.setHoverColor(Color.lightGray);
            btn_exit.setPressedColor(Color.gray);
            btn_exit.setMouseListener(listener);
            this.add(btn_exit);

            // CREATE PLANT BUTTON
            Animation animationButtonIcons = new Animation(TextureManager.getTexture("BTN_GAME_MAIN_ICONS"));

            MainButtonListener buttonListener = new MainButtonListener();
            btn_plant = new GUIImageButton(1207, 8, animationButton, animationButtonIcons, 0);
            btn_plant.setMouseListener(buttonListener);
            btn_plant.setLabel("Power");
            this.add(btn_plant);

            // CREATE POLICE BUTTON
            btn_police = new GUIImageButton(1207, 8 + 1 * 76, animationButton, animationButtonIcons, 1);
            btn_police.setMouseListener(buttonListener);
            btn_police.setLabel("Police");
            this.add(btn_police);

            // CREATE HOUSE BUTTON
            btn_house = new GUIImageButton(1207, 8 + 2 * 76, animationButton, animationButtonIcons, 2);
            btn_house.setMouseListener(buttonListener);
            btn_house.setLabel("House");
            this.add(btn_house);

            // CREATE STREET BUTTON
            btn_street = new GUIImageButton(1207, 8 + 3 * 76, animationButton, animationButtonIcons, 3);
            btn_street.setMouseListener(buttonListener);
            btn_street.setLabel("Streets");
            this.add(btn_street);

            // CREATE POWERLINE BUTTON
            btn_powerline = new GUIImageButton(1207, 8 + 4 * 76, animationButton, animationButtonIcons, 5);
            btn_powerline.setMouseListener(buttonListener);
            btn_powerline.setLabel("Powerline");
            this.add(btn_powerline);

            // CREATE BULLDOZER BUTTON
            btn_bulldozer = new GUIImageButton(1207, 527 - 1 * 76, animationButton, animationButtonIcons, 4);
            btn_bulldozer.setMouseListener(buttonListener);
            btn_bulldozer.setLabel("Bulldozer");
            this.add(btn_bulldozer);

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
        } else if (event.getKey() == Keyboard.KEY_ESCAPE) {
            Minetown.setGameState(GameState.GAME_PAUSED);
        }
    }

    @Override
    public void render() {
        if (minetown.getGameState().equals(GameState.GAME)) {
            glPushMatrix();
            {
                glScalef(Minetown.SCALE, Minetown.SCALE, 1);
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
            this.renderPath();
            super.render();
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        mouseTileX = this.isoMap.getTileX(this.mouseVector.getX() / Minetown.SCALE, this.mouseVector.getY() / Minetown.SCALE);
        mouseTileY = this.isoMap.getTileY(this.mouseVector.getX() / Minetown.SCALE, this.mouseVector.getY() / Minetown.SCALE);
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

        if (!(event.getX() > 0 && event.getY() > 0 && event.getX() < 1280 - 82 && event.getY() < 1024)) {
            this.inDragBuild = false;
        }
    }

    private void renderPath() {
        if (inDragBuild && TileDimension.getSelectedTile().getType().isDraggable()) {
            if (updatePath) {
                // we have a street => calculate path and place streets
                if (TileDimension.getSelectedTile().getType().equals(TileType.POWERLINE)) {
                    path = this.isoMap.getBuildingPath(downMouseX, downMouseY, mouseTileX, mouseTileY, TileDimension.getSelectedTile().getType(), TileType.STREET);
                } else {
                    path = this.isoMap.getBuildingPath(downMouseX, downMouseY, mouseTileX, mouseTileY, TileDimension.getSelectedTile().getType());
                }
            }
            updatePath = false;
            // TileDimension.place(downMouseX, downMouseY, isoMap);
            IsoTile streetTile = TileManager.getTile("white");
            if (path != null) {
                int tX = this.isoMap.getIsoX(downMouseX, downMouseY);
                int tY = this.isoMap.getIsoY(downMouseX, downMouseY);
                glPushMatrix();
                {
                    glScalef(Minetown.SCALE, Minetown.SCALE, 1);
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
                glPopMatrix();
            }
        }
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
        if (event.getX() > 0 && event.getY() > 0 && event.getX() < 1280 - 82 && event.getY() < 1024) {
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
        if (event.getX() > 0 && event.getY() > 0 && event.getX() < 1280 - 82 && event.getY() < 1024) {
            if (event.isLeftButton() && inDragBuild) {
                inDragBuild = false;
                if ((TileDimension.isFree() || (!TileDimension.isFree() && TileDimension.getSelectedTile().getType().equals(TileType.BULLDOZER))) && downMouseX > -1 && downMouseY > -1) {
                    if (!TileDimension.getSelectedTile().getType().isDraggable()) {
                        downMouseX = mouseTileX;
                        downMouseY = mouseTileY;
                        TileDimension.place(mouseTileX, mouseTileY, isoMap);
                        return;
                    } else {
                        // we have a street => calculate path and place streets
                        int upMouseX = this.isoMap.getTileX(this.mouseVector.getX() / Minetown.SCALE, this.mouseVector.getY() / Minetown.SCALE);
                        int upMouseY = this.isoMap.getTileY(this.mouseVector.getX() / Minetown.SCALE, this.mouseVector.getY() / Minetown.SCALE);
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
    public void onMouseWheel(MouseWheelEvent event) {
        if (event.isUp() && Minetown.SCALE < 1.45f) {
            Minetown.SCALE += 0.1f;
            float newOffX = event.getX() / Minetown.SCALE;
            float newOffY = event.getY() / Minetown.SCALE;
            this.isoMap.addOffset(-newOffX / 10f, -newOffY / 10f);
        } else if (event.isDown() && Minetown.SCALE > 0.55f) {
            Minetown.SCALE -= 0.1f;
            float newOffX = event.getX() / Minetown.SCALE;
            float newOffY = event.getY() / Minetown.SCALE;
            this.isoMap.addOffset(newOffX / 10f, newOffY / 10f);
        }
    }

    @Override
    public void onMouseDrag(MouseDragEvent event) {
        if (event.isRightButton()) {
            if (event.getX() > 0 && event.getY() > 0 && event.getX() < 1280 - 82 && event.getY() < 1024) {
                float offsetX = this.isoMap.getOffsetX() + event.getDifX();
                float offsetY = this.isoMap.getOffsetY() + event.getDifY();
                this.isoMap.setOffset(offsetX, offsetY);
            }
        }
    }
}
