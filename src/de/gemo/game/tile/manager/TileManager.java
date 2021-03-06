package de.gemo.game.tile.manager;

import java.util.HashMap;

import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.set.Tile_Bulldozer;
import de.gemo.game.tile.set.Tile_Grass;
import de.gemo.game.tile.set.Tile_House_Mid_01;
import de.gemo.game.tile.set.Tile_House_Small_01;
import de.gemo.game.tile.set.Tile_Mouse;
import de.gemo.game.tile.set.Tile_None;
import de.gemo.game.tile.set.Tile_Police_01;
import de.gemo.game.tile.set.Tile_PowerPlant_01;
import de.gemo.game.tile.set.Tile_Tree_01;
import de.gemo.game.tile.set.Tile_Unknown;
import de.gemo.game.tile.set.Tile_Water;
import de.gemo.game.tile.set.Tile_White;
import de.gemo.game.tile.set.powerline.Tile_Powerline_E;
import de.gemo.game.tile.set.powerline.Tile_Powerline_Intercept;
import de.gemo.game.tile.set.powerline.Tile_Powerline_N;
import de.gemo.game.tile.set.powerline.Tile_Powerline_NE;
import de.gemo.game.tile.set.powerline.Tile_Powerline_NE_Overlay;
import de.gemo.game.tile.set.powerline.Tile_Powerline_NW;
import de.gemo.game.tile.set.powerline.Tile_Powerline_NW_Overlay;
import de.gemo.game.tile.set.powerline.Tile_Powerline_S;
import de.gemo.game.tile.set.powerline.Tile_Powerline_TIntercept_NE;
import de.gemo.game.tile.set.powerline.Tile_Powerline_TIntercept_NW;
import de.gemo.game.tile.set.powerline.Tile_Powerline_TIntercept_SE;
import de.gemo.game.tile.set.powerline.Tile_Powerline_TIntercept_SW;
import de.gemo.game.tile.set.powerline.Tile_Powerline_W;
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
import de.gemo.game.tile.set.watertubes.Tile_Watertube_E;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_Intercept;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_N;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_NE;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_NW;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_S;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_TIntercept_NE;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_TIntercept_NW;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_TIntercept_SE;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_TIntercept_SW;
import de.gemo.game.tile.set.watertubes.Tile_Watertube_W;

public class TileManager {
    private static HashMap<String, IsoTile> registeredTiles;

    static {
        registeredTiles = new HashMap<String, IsoTile>();

        // general
        registeredTiles.put("unknown", new Tile_Unknown());
        registeredTiles.put("none", new Tile_None());
        registeredTiles.put("bulldozer", new Tile_Bulldozer());
        registeredTiles.put("grass", new Tile_Grass());
        registeredTiles.put("white", new Tile_White());
        registeredTiles.put("mouse", new Tile_Mouse());
        registeredTiles.put("water", new Tile_Water());

        // streets
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

        // watertubes
        registeredTiles.put("watertube_nw", new Tile_Watertube_NW());
        registeredTiles.put("watertube_ne", new Tile_Watertube_NE());
        registeredTiles.put("watertube_intercept", new Tile_Watertube_Intercept());
        registeredTiles.put("watertube_tintercept_ne", new Tile_Watertube_TIntercept_NE());
        registeredTiles.put("watertube_tintercept_nw", new Tile_Watertube_TIntercept_NW());
        registeredTiles.put("watertube_tintercept_se", new Tile_Watertube_TIntercept_SE());
        registeredTiles.put("watertube_tintercept_sw", new Tile_Watertube_TIntercept_SW());
        registeredTiles.put("watertube_n", new Tile_Watertube_N());
        registeredTiles.put("watertube_e", new Tile_Watertube_E());
        registeredTiles.put("watertube_s", new Tile_Watertube_S());
        registeredTiles.put("watertube_w", new Tile_Watertube_W());

        // buildings
        registeredTiles.put("tile_house_small_01", new Tile_House_Small_01());
        registeredTiles.put("tile_house_mid_01", new Tile_House_Mid_01());

        // industry
        registeredTiles.put("powerplant_01", new Tile_PowerPlant_01());
        registeredTiles.put("police_01", new Tile_Police_01());

        // powerlines
        registeredTiles.put("powerline_nw", new Tile_Powerline_NW());
        registeredTiles.put("powerline_ne", new Tile_Powerline_NE());
        registeredTiles.put("powerline_intercept", new Tile_Powerline_Intercept());
        registeredTiles.put("powerline_tintercept_ne", new Tile_Powerline_TIntercept_NE());
        registeredTiles.put("powerline_tintercept_nw", new Tile_Powerline_TIntercept_NW());
        registeredTiles.put("powerline_tintercept_se", new Tile_Powerline_TIntercept_SE());
        registeredTiles.put("powerline_tintercept_sw", new Tile_Powerline_TIntercept_SW());
        registeredTiles.put("powerline_n", new Tile_Powerline_N());
        registeredTiles.put("powerline_e", new Tile_Powerline_E());
        registeredTiles.put("powerline_s", new Tile_Powerline_S());
        registeredTiles.put("powerline_w", new Tile_Powerline_W());
        registeredTiles.put("powerline_nw_overlay", new Tile_Powerline_NW_Overlay());
        registeredTiles.put("powerline_ne_overlay", new Tile_Powerline_NE_Overlay());

        // trees
        registeredTiles.put("tree_01", new Tile_Tree_01());
    }

    public static IsoTile getTile(String name) {
        return registeredTiles.get(name);
    }
}
