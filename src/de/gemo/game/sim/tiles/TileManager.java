package de.gemo.game.sim.tiles;

import java.awt.*;
import java.util.*;

public class TileManager {

	private static HashMap<Color, AbstractTile> registeredTiles;
	private static HashMap<String, AbstractTile> registeredTilesByName;

	private static boolean initialized = false;

	public static void initialize() {
		if (!initialized) {
			System.out.println("[ FINE ] Initializing TileManager...");
			registeredTiles = new HashMap<Color, AbstractTile>();
			registeredTilesByName = new HashMap<String, AbstractTile>();

			registerAllTiles();
			System.out.println("[ FINE ] TileManager initialized.");
			initialized = true;
		} else {
			System.out.println("[ WARNING ] TileManager already initialized!");
		}
	}

	private static void registerAllTiles() {
		createTile(new TileBlocked());
		createTile(new TileEmpty());
	}

	private static void createTile(AbstractTile tile) {
		if (!registerTile(tile)) {
			System.out.println("[ ERROR ] Could not register tile '" + tile.getName() + "'!");
		}
	}

	public static boolean registerTile(AbstractTile tile) {
		if (isTileRegistered(tile)) {
			return false;
		}
		registeredTiles.put(tile.getIMGColor(), tile);
		registeredTilesByName.put(tile.getName(), tile);
		System.out.println("[ FINE ] Tile '" + tile.getName() + "' registered (ID: " + tile.getID() + ").");
		return true;
	}

	public static boolean unregisterTile(AbstractTile tile) {
		if (!isTileRegistered(tile)) {
			return false;
		}
		registeredTiles.remove(tile.getIMGColor());
		registeredTilesByName.remove(tile.getName());
		return true;
	}

	public static boolean isTileRegistered(AbstractTile tile) {
		return registeredTiles.containsKey(tile.getIMGColor());
	}

	public static boolean isTileRegistered(Color color) {
		return registeredTiles.containsKey(color);
	}

	public static AbstractTile getTile(Color color) {
		return registeredTiles.get(color);
	}

	public static boolean isTileRegistered(String name) {
		return registeredTilesByName.containsKey(name);
	}

	public static AbstractTile getTileByName(String name) {
		return registeredTilesByName.get(name);
	}
}
