package de.gemo.game.tile.set.powerline;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.manager.PowerlineManager;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public abstract class Tile_Powerline extends IsoTile {

    public Tile_Powerline(int frame) {
        super(TileType.POWERLINE, TextureManager.getTexture("tile_powerline").toAnimation(), true, 0, -16);
        this.animation.goToFrame(frame);
        this.buildPrice = 75;
        this.removalPrice = 50;
    }

    @Override
    public void select() {
        super.select();
    }

    @Override
    public void renderBuildPlace(IsoMap isoMap, int tileX, int tileY) {
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
    public void onRemove(IsoMap isoMap, int tileX, int tileY) {
        if (isoMap.getTileInformation(tileX, tileY).setPowered(false)) {
            this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
        }
    }

    @Override
    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        if (!isoMap.getTile(tileX, tileY).getType().equals(TileType.STREET)) {
            isoMap.setTile(tileX, tileY, PowerlineManager.getTile(tileX, tileY, isoMap), true);
        } else {
            isoMap.setOverlay(tileX, tileY, PowerlineManager.getTileForOverlay(tileX, tileY, isoMap));
        }
        if (isoMap.isTileConnectedToPowersource(tileX, tileY)) {
            if (isoMap.getTileInformation(tileX, tileY).setPowered(true)) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        }
        this.informAllNeighbours(isoMap, tileX, tileY);
    }

    @Override
    public void onNeighbourChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
        if (isoMap.hasOverlay(tileX, tileY)) {
            isoMap.setOverlay(tileX, tileY, PowerlineManager.getTileForOverlay(tileX, tileY, isoMap));
        } else {
            isoMap.setTileWithoutInformation(tileX, tileY, PowerlineManager.getTile(tileX, tileY, isoMap), true);
        }
        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        if (isNowPowered) {
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
        } else {
            isoMap.getTileInformation(tileX, tileY).setPowered(false);
        }
    }

    @Override
    public void onNeighbourPowerChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        if (isNowPowered) {
            // neighbours will only get informed, if the power wasn't there but now is
            if (isoMap.getTileInformation(tileX, tileY).setPowered(true)) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        } else {
            // neighbours will only get informed, if the power was there but isn't anymore
            if (isoMap.getTileInformation(tileX, tileY).setPowered(false)) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        }
    }

    @Override
    public boolean canBePlacedAt(IsoMap isoMap, int tileX, int tileY) {
        return (super.canBePlacedAt(isoMap, tileX, tileY)) || (isoMap.getTile(tileX, tileY).getType().canHaveOverlay() && isoMap.getOverlay(tileX, tileY) == null && PowerlineManager.canOverlayBePlaced(tileX, tileY, isoMap));
    }

}
