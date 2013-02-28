package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.PowerlineManager;

import static org.lwjgl.opengl.GL11.*;

public abstract class Tile_Powerline extends IsoTile {

    public Tile_Powerline(int frame) {
        super(TileType.POWERLINE, TextureManager.getTexture("tile_powerline").toAnimation(), true, 0, -16);
        this.animation.goToFrame(frame);
    }

    @Override
    public void select() {
        super.select();
    }

    @Override
    public void renderBuildPlace(int tileX, int tileY, IsoMap isoMap) {
        glPushMatrix();
        {
            IsoTile tile = PowerlineManager.getTile(tileX, tileY, isoMap);
            tile.setAlpha(0.5f);
            tile.render();
            tile.setAlpha(1f);
        }
        glPopMatrix();
    }

    @Override
    public void onRemove(int tileX, int tileY, IsoMap isoMap) {
        isoMap.getTileInformation(tileX, tileY).setPowered(false);
        this.informAllNeighboursAboutPowerchange(tileX, tileY, isoMap);
    }

    @Override
    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
        if (!isoMap.getTile(tileX, tileY).getType().equals(TileType.STREET)) {
            isoMap.setTile(tileX, tileY, PowerlineManager.getTile(tileX, tileY, isoMap), true);
        } else {
            isoMap.setOverlay(tileX, tileY, PowerlineManager.getTileForOverlay(tileX, tileY, isoMap));
        }
        if (isoMap.isTileConnectedToPowersource(tileX, tileY)) {
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            this.informAllNeighboursAboutPowerchange(tileX, tileY, isoMap);
        }
        this.informAllNeighbours(tileX, tileY, isoMap);
    }

    @Override
    public void onNeighbourChange(int tileX, int tileY, int neighbourX, int neighbourY, IsoMap isoMap) {
        if (isoMap.hasOverlay(tileX, tileY)) {
            isoMap.setOverlay(tileX, tileY, PowerlineManager.getTileForOverlay(tileX, tileY, isoMap));
        } else {
            isoMap.setTile(tileX, tileY, PowerlineManager.getTile(tileX, tileY, isoMap), true);
        }

        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        boolean wasPowered = isoMap.getTileInformation(tileX, tileY).isPowered();
        if (isNowPowered) {
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            // neighbours will only get informed, if the power wasn't there but now is
            if (!wasPowered) {
                this.informAllNeighboursAboutPowerchange(tileX, tileY, isoMap);
            }
        } else {
            isoMap.getTileInformation(tileX, tileY).setPowered(false);
            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                this.informAllNeighboursAboutPowerchange(tileX, tileY, isoMap);
            }
        }
    }

    @Override
    public void onNeighbourPowerChange(int tileX, int tileY, int neighbourX, int neighbourY, IsoMap isoMap) {
        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        boolean wasPowered = isoMap.getTileInformation(tileX, tileY).isPowered();
        if (isNowPowered) {
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            // neighbours will only get informed, if the power wasn't there but now is
            if (!wasPowered) {
                this.informAllNeighboursAboutPowerchange(tileX, tileY, isoMap);
            }
        } else {
            isoMap.getTileInformation(tileX, tileY).setPowered(false);
            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                this.informAllNeighboursAboutPowerchange(tileX, tileY, isoMap);
            }
        }
    }

    @Override
    public boolean canBePlacedAt(int tileX, int tileY, IsoMap isoMap) {
        return (super.canBePlacedAt(tileX, tileY, isoMap)) || (isoMap.getTile(tileX, tileY).getType().canHaveOverlay() && isoMap.getOverlay(tileX, tileY) == null && PowerlineManager.canOverlayBePlaced(tileX, tileY, isoMap));
    }

}
