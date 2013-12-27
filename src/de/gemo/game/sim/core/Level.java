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
    private int[][] tempBlocked;
    private AbstractTile emptyTile;

    private ArrayList<Person> persons;

    public Level(int dimX, int dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.initLevel();
        this.persons = new ArrayList<Person>();
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
                java.awt.Color color = new java.awt.Color(rgb);
                AbstractTile tile = TileManager.getTile(color);
                if (tile != null) {
                    this.setTile(x, y, tile);
                } else {
                    // Tile invalid = set blocked
                    tile = TileManager.getTileByName("Blocked");
                    if (tile != null) {
                        this.setTile(x, y, tile);
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

        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                if (x == 4 || y == 4 || x == dimX - 5 || y == dimY - 5 || x == 0 || y == 0 || x == dimX / 2 || x == dimX / 2 + 1 || y == dimY / 2 || y == dimY / 2 + 1) {
                    AbstractTile tile = TileManager.getTileByName("Blocked");
                    if (tile != null) {
                        this.setTile(x, y, tile);
                    }
                } else {
                    AbstractTile tile = TileManager.getTileByName("Empty");
                    if (tile != null) {
                        this.setTile(x, y, tile);
                    }
                }
                this.tempBlocked[x][y] = 0;
            }
        }
    }

    private void createArrays() {
        // CREATE NODES
        this.tiles = new AbstractTile[dimX][dimY];
        this.tileIDs = new int[dimX][dimY];
        this.blocked = new boolean[dimX][dimY];
        this.tempBlocked = new int[dimX][dimY];
    }

    public void renderLevel() {
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                // glPushMatrix();
                // {
                // glEnable(GL_BLEND);
                // glEnable(GL_TEXTURE_2D);
                // glTranslatef(x * AbstractTile.TILE_SIZE +
                // AbstractTile.QUARTER_TILE_SIZE + x, y *
                // AbstractTile.TILE_SIZE + AbstractTile.QUARTER_TILE_SIZE - 3 +
                // y, 0);
                // org.newdawn.slick.Color color = new
                // org.newdawn.slick.Color(1, 1, 1, 0.2f);
                // FontManager.getStandardFont().drawString(0, 0, "" +
                // this.getTempBlockedValue(x, y), color);
                // }
                // glPopMatrix();

                glPushMatrix();
                {
                    glDisable(GL_TEXTURE_2D);
                    tiles[x][y].render(x, y);
                }
                glPopMatrix();
            }
        }
        glPushMatrix();
        {
            for (Person person : this.persons) {
                person.render();
            }
        }
        glPopMatrix();
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
        AStar star = new AStar(this.createAreaMap(), new ClosestHeuristic(), false);
        ArrayList<Point> list = star.calcShortestPath(start.x, start.y, goal.x, goal.y);
        return list;
    }

    public int getDimX() {
        return dimX;
    }

    public int getDimY() {
        return dimY;
    }

    public void setTile(int x, int y, AbstractTile tile) {
        this.tiles[x][y] = tile;
        this.tileIDs[x][y] = tile.getID();
        this.blocked[x][y] = tile.isBlockingPath();
    }

    public void addPerson(Person person) {
        this.persons.add(person);
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void tick() {
        for (Person person : this.persons) {
            person.update(0);
        }
    }

    public void modifyTempBlocked(int x, int y, int value) {
        if (x > -1 && x < dimX && y > -1 && y < dimY) {
            this.tempBlocked[x][y] += value;
            if (this.tempBlocked[x][y] < 1) {
                this.tempBlocked[x][y] = 0;
            }
        }
    }

    public int getTempBlockedValue(int x, int y) {
        if (x > -1 && x < dimX && y > -1 && y < dimY) {
            return this.tempBlocked[x][y];
        }
        return 10;
    }

    public AreaMap createAreaMap() {
        boolean[][] obstacleMap = new boolean[this.dimX][this.dimY];
        int[][] costMap = new int[this.dimX][this.dimY];
        for (int y = 0; y < this.dimY; y++) {
            for (int x = 0; x < this.dimX; x++) {
                obstacleMap[x][y] = this.getTile(x, y).isBlockingPath() || this.getTempBlockedValue(x, y) > 0;
                costMap[x][y] = this.getTempBlockedValue(x, y);
            }
        }
        return new AreaMap(this.dimX, this.dimY, obstacleMap, costMap);
    }
}
