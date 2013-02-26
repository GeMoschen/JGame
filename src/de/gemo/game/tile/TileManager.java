package de.gemo.game.tile;

import java.util.HashMap;

import de.gemo.game.tile.set.Tile_Bulldozer;
import de.gemo.game.tile.set.Tile_Grass;
import de.gemo.game.tile.set.Tile_House_Small_01;
import de.gemo.game.tile.set.Tile_Mouse;
import de.gemo.game.tile.set.Tile_None;
import de.gemo.game.tile.set.Tile_Police_01;
import de.gemo.game.tile.set.Tile_PowerPlant_01;
import de.gemo.game.tile.set.Tile_Unknown;
import de.gemo.game.tile.set.Tile_White;
import de.gemo.game.tile.set.streets.Tile_Street_E;
import de.gemo.game.tile.set.streets.Tile_Street_Intercept;
import de.gemo.game.tile.set.streets.Tile_Street_N;
import de.gemo.game.tile.set.streets.Tile_Street_NE;
import de.gemo.game.tile.set.streets.Tile_Street_NW;
import de.gemo.game.tile.set.streets.Tile_Street_S;
import de.gemo.game.tile.set.streets.Tile_Street_TIntercept_NE;
import de.gemo.game.tile.set.streets.Tile_Street_TIntercept_NW;
import de.gemo.game.tile.set.streets.Tile_Street_TIntercept_SE;
import de.gemo.game.tile.set.streets.Tile_Street_TIntercept_SW;
import de.gemo.game.tile.set.streets.Tile_Street_W;

public class TileManager {
    private static HashMap<String, IsoTile> registeredTiles;

    static {
        registeredTiles = new HashMap<String, IsoTile>();
        registeredTiles.put("unknown", new Tile_Unknown());
        registeredTiles.put("none", new Tile_None());
        registeredTiles.put("bulldozer", new Tile_Bulldozer());
        registeredTiles.put("grass", new Tile_Grass());
        registeredTiles.put("white", new Tile_White());
        registeredTiles.put("mouse", new Tile_Mouse());
        registeredTiles.put("street_nw", new Tile_Street_NW());
        registeredTiles.put("street_ne", new Tile_Street_NE());
        registeredTiles.put("street_intercept", new Tile_Street_Intercept());
        registeredTiles.put("street_tintercept_ne", new Tile_Street_TIntercept_NE());
        registeredTiles.put("street_tintercept_nw", new Tile_Street_TIntercept_NW());
        registeredTiles.put("street_tintercept_se", new Tile_Street_TIntercept_SE());
        registeredTiles.put("street_tintercept_sw", new Tile_Street_TIntercept_SW());
        registeredTiles.put("street_n", new Tile_Street_N());
        registeredTiles.put("street_e", new Tile_Street_E());
        registeredTiles.put("street_s", new Tile_Street_S());
        registeredTiles.put("street_w", new Tile_Street_W());
        registeredTiles.put("tile_house_small_01", new Tile_House_Small_01());
        registeredTiles.put("powerplant_01", new Tile_PowerPlant_01());
        registeredTiles.put("police_01", new Tile_Police_01());
    }

    public static IsoTile getTile(String name) {
        return registeredTiles.get(name);
    }
}
