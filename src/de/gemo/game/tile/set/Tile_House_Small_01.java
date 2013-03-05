package de.gemo.game.tile.set;

import java.awt.Point;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileInformation;
import de.gemo.game.tile.manager.HouseManager;
import de.gemo.game.tile.manager.TileManager;

public class Tile_House_Small_01 extends IsoTile {

    public Tile_House_Small_01() {
        super(TileType.HOUSE_SMALL, TextureManager.getTexture("tile_house_small_01").toAnimation(), true, 0, -5);
        this.buildPrice = 250;
        this.removalPrice = 75;
    }

    @Override
    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        isoMap.setTile(tileX, tileY, this, true);
        if (isoMap.isTileConnectedToPowersource(tileX, tileY)) {
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
        }
        this.informAllNeighbours(isoMap, tileX, tileY);
        isoMap.getTileInformation(tileX, tileY).addPollutionLevel(5f);
        HouseManager.addHouse(tileX, tileY);
    }

    @Override
    public void onRemove(IsoMap isoMap, int tileX, int tileY) {
        isoMap.getTileInformation(tileX, tileY).setPowered(false);
        isoMap.getTileInformation(tileX, tileY).resetStatistics();
        this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
        isoMap.getTileInformation(tileX, tileY).addPollutionLevel(-5f);
        HouseManager.removeHouse(tileX, tileY);
    }

    @Override
    public void onNeighbourPowerChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        boolean wasPowered = isoMap.getTileInformation(tileX, tileY).isPowered();
        if (isNowPowered) {
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            // neighbours will only get informed, if the power wasn't there but now is
            if (!wasPowered) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        } else {
            isoMap.getTileInformation(tileX, tileY).setPowered(false);
            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        }
    }

    @Override
    public void doTick(IsoMap isoMap, int tileX, int tileY) {
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);

        int secLevel = generator.nextInt(10);
        if (tileInfo.getSecureLevel() >= secLevel && tileInfo.isPowered()) {
            float grow = Math.abs(0.05f + tileInfo.getSecureLevel() / 60f);
            tileInfo.addTickLevel(grow);
            if (tileInfo.getTickLevel() > 10 && generator.nextGaussian() > 0.92f) {
                this.grow(isoMap, tileX, tileY);
            }
        } else {
            tileInfo.addTickLevel(-1.5f + tileInfo.getSecureLevel() / 60f);
            if (tileInfo.getTickLevel() < -5 && generator.nextFloat() > 0.6f) {
                System.out.println("want to ungrow!");
            }
        }
    }

    private void grow(IsoMap isoMap, int tileX, int tileY) {
        final int random = generator.nextInt(4);

        Point origin = null, p1 = null, p2 = null, p3 = null;

        switch (random) {
            case 0 : {
                origin = new Point(tileX, tileY);
                p1 = new Point(tileX, tileY - 1);
                p2 = new Point(tileX + 1, tileY - 1);
                p3 = new Point(tileX + 1, tileY);
                break;
            }
            case 1 : {
                origin = new Point(tileX, tileY + 1);
                p1 = new Point(tileX, tileY + 1);
                p2 = new Point(tileX + 1, tileY);
                p3 = new Point(tileX + 1, tileY + 1);
                break;
            }
            case 2 : {
                origin = new Point(tileX - 1, tileY + 1);
                p1 = new Point(tileX - 1, tileY + 1);
                p2 = new Point(tileX - 1, tileY);
                p3 = new Point(tileX, tileY + 1);
                break;
            }
            case 3 : {
                origin = new Point(tileX - 1, tileY);
                p1 = new Point(tileX - 1, tileY);
                p2 = new Point(tileX - 1, tileY - 1);
                p3 = new Point(tileX, tileY - 1);
                break;
            }
        }

        // get tiles
        boolean HOUSE_1 = isoMap.getTile(p1.x, p1.y).getType().equals(TileType.HOUSE_SMALL);
        boolean HOUSE_2 = isoMap.getTile(p2.x, p2.y).getType().equals(TileType.HOUSE_SMALL);
        boolean HOUSE_3 = isoMap.getTile(p3.x, p3.y).getType().equals(TileType.HOUSE_SMALL);

        int housesAround = 0;
        if (HOUSE_1) {
            housesAround++;
        }
        if (HOUSE_2) {
            housesAround++;
        }
        if (HOUSE_3) {
            housesAround++;
        }

        // check for small houses
        if (!HOUSE_1 || !HOUSE_2 || !HOUSE_3) {
            return;
        }

        // check satisfaction
        if (isoMap.getTileInformation(p1.x, p1.y).getTickLevel() < 10 || isoMap.getTileInformation(p2.x, p2.y).getTickLevel() < 10 || isoMap.getTileInformation(p3.x, p3.y).getTickLevel() < 10) {
            return;
        }

        isoMap.setTile(origin.x, origin.y, TileManager.getTile("grass"), false);
        isoMap.setTile(p1.x, p1.y, TileManager.getTile("grass"), false);
        isoMap.setTile(p2.x, p2.y, TileManager.getTile("grass"), false);
        isoMap.setTile(p3.x, p3.y, TileManager.getTile("grass"), false);

        // set to mid-house
        TileManager.getTile("tile_house_mid_01").onPlace(isoMap, origin.x, origin.y);
    }
}
