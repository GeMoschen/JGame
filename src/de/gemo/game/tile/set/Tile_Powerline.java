package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.manager.PowerlineManager;

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
        isoMap.getTileInformation(tileX, tileY).setPowered(false);
        this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
        // this.informAllNeighbours(isoMap, tileX, tileY);
    }

    @Override
    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        if (!isoMap.getTile(tileX, tileY).getType().equals(TileType.STREET)) {
            isoMap.setTile(tileX, tileY, PowerlineManager.getTile(tileX, tileY, isoMap), true);
        } else {
            isoMap.setOverlay(tileX, tileY, PowerlineManager.getTileForOverlay(tileX, tileY, isoMap));
        }
        if (isoMap.isTileConnectedToPowersource(tileX, tileY)) {
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            // this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
        }
        this.informAllNeighbours(isoMap, tileX, tileY);
    }

    @Override
    public void onNeighbourChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
        System.out.println("neighbour of " + tileX + " / " + tileY + " changed!");
        if (isoMap.hasOverlay(tileX, tileY)) {
            isoMap.setOverlay(tileX, tileY, PowerlineManager.getTileForOverlay(tileX, tileY, isoMap));
        } else {
            isoMap.setTile(tileX, tileY, PowerlineManager.getTile(tileX, tileY, isoMap), true);
        }

        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        boolean wasPowered = isoMap.getTileInformation(tileX, tileY).isPowered();
        System.out.println("now: " + isNowPowered);
        System.out.println("was: " + wasPowered);
        if (isNowPowered) {
            System.out.println("power");
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            // neighbours will only get informed, if the power wasn't there but now is
            if (!wasPowered) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        } else {
            System.out.println("no power");
            isoMap.getTileInformation(tileX, tileY).setPowered(false);
            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        }
    }

    @Override
    public void onNeighbourPowerChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {

        System.out.println("neighbour of " + tileX + " / " + tileY + " changed power!");
        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        boolean wasPowered = isoMap.getTileInformation(tileX, tileY).isPowered();
        System.out.println("now: " + isNowPowered);
        System.out.println("was: " + wasPowered);
        if (isNowPowered) {
            System.out.println("power");
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            // neighbours will only get informed, if the power wasn't there but now is
            if (!wasPowered) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        } else {
            System.out.println("no power");
            isoMap.getTileInformation(tileX, tileY).setPowered(false);
            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        }
    }

    @Override
    public boolean canBePlacedAt(IsoMap isoMap, int tileX, int tileY) {
        return (super.canBePlacedAt(isoMap, tileX, tileY)) || (isoMap.getTile(tileX, tileY).getType().canHaveOverlay() && isoMap.getOverlay(tileX, tileY) == null && PowerlineManager.canOverlayBePlaced(tileX, tileY, isoMap));
    }

}
