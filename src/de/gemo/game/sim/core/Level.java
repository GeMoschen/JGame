package de.gemo.game.sim.core;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import de.gemo.game.sim.tiles.*;
import de.gemo.pathfinding.*;

import static org.lwjgl.opengl.GL11.*;

public class Level {

    private int dimX = 40, dimY = 30;
    private AbstractTile[][] tiles;
    private int[][] tileIDs;
    private boolean[][] blocked;
    private AbstractTile emptyTile;

    public Level(int dimX, int dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.initLevel();
    }

    public Level(BufferedImage image) {
        this.dimX = image.getWidth();
        this.dimY = image.getHeight();
        this.loadLevelFromImage(image);
    }

    private void loadLevelFromImage(BufferedImage image) {
        // EMPTY RECTANGLE
        this.emptyTile = TileManager.getTileByName("Blocked");

        // CREATE NODES
        this.createArrays();
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb);
                AbstractTile tile = TileManager.getTile(color);
                if (tile != null) {
                    this.tiles[x][y] = tile;
                    this.tileIDs[x][y] = tile.getID();
                    this.blocked[x][y] = tile.isBlockingPath();
                } else {
                    // Tile invalid = set blocked
                    tile = TileManager.getTileByName("Blocked");
                    if (tile != null) {
                        this.tiles[x][y] = tile;
                        this.tileIDs[x][y] = tile.getID();
                        this.blocked[x][y] = tile.isBlockingPath();
                    }
                }
            }
        }
    }

    private void initLevel() {
        // EMPTY RECTANGLE
        this.emptyTile = TileManager.getTileByName("Blocked");
        // CREATE NODES
        this.createArrays();

        Random random = new Random();

        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                if (random.nextFloat() > 0.05f) {
                    AbstractTile tile = TileManager.getTileByName("Empty");
                    if (tile != null) {
                        this.tiles[x][y] = tile;
                        this.tileIDs[x][y] = tile.getID();
                        this.blocked[x][y] = tile.isBlockingPath();
                    }
                } else {
                    AbstractTile tile = TileManager.getTileByName("Blocked");
                    if (tile != null) {
                        this.tiles[x][y] = tile;
                        this.tileIDs[x][y] = tile.getID();
                        this.blocked[x][y] = tile.isBlockingPath();
                    }
                }
            }
        }
    }

    private void createArrays() {
        // CREATE NODES
        this.tiles = new AbstractTile[dimX][dimY];
        this.tileIDs = new int[dimX][dimY];
        this.blocked = new boolean[dimX][dimY];
    }

    public void renderLevel() {
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                glPushMatrix();
                {
                    tiles[x][y].render(x, y);
                }
                glPopMatrix();
            }
        }
    }

    public AbstractTile getTile(int x, int y) {
        if (x > -1 && x < dimX && y > -1 && y < dimY) {
            return tiles[x][y];
        }
        return emptyTile;
    }

    AreaMap areaMap = null;
    AStar star = null;

    public ArrayList<Point> getPath(Point start, Point goal) {
        AreaMap areaMap = new AreaMap(this);
        AStar star = new AStar(areaMap, new ClosestHeuristic(), false);
        ArrayList<Point> list = star.calcShortestPath(start.x, start.y, goal.x, goal.y);
        return list;
    }

    public int getDimX() {
        return dimX;
    }

    public int getDimY() {
        return dimY;
    }

}
