package de.gemo.game.tile;

import java.awt.Point;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.newdawn.slick.util.pathfinding.heuristics.ManhattanHeuristic;

public abstract class IsoMap implements TileBasedMap {
    protected IsoTile[][] tileMap;

    protected TileInformation[][] tileInfos;

    public static boolean SHOW_SECURITY = false;
    public static boolean SHOW_POWER = false;

    protected int width, height;
    protected int tileWidth, tileHeight, halfTileWidth, halfTileHeight;

    protected float offsetX, offsetY;

    // vars for pathfinding
    private AStarPathFinder aStar;
    private int pathMode = 0;

    public IsoMap(int width, int height, int tileWidth, int tileHeight) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.halfTileWidth = (int) (this.tileWidth / 2f);
        this.halfTileHeight = (int) (this.tileHeight / 2f);
        this.tileMap = new IsoTile[width][height];
        this.tileInfos = new TileInformation[width][height];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.tileMap[x][y] = TileManager.getTile("grass");
                this.tileInfos[x][y] = new TileInformation(x, y);
            }
        }
        this.offsetX = 0;
        this.offsetY = 0;

        this.initAStar();
    }

    private void initAStar() {
        this.aStar = new AStarPathFinder(this, this.width * 3, false, new ManhattanHeuristic(0));
    }

    public Path getBuildingPath(int startX, int startY, int goalX, int goalY) {
        // BUILDINGS = 0
        this.pathMode = 0;
        return this.aStar.findPath(null, startX, startY, goalX, goalY);
    }

    public Path getPowerPath(int startX, int startY, int goalX, int goalY) {
        // POWER = 1
        this.pathMode = 1;
        return this.aStar.findPath(null, startX, startY, goalX, goalY);
    }

    @Override
    public boolean blocked(PathFindingContext arg0, int tileX, int tileY) {
        // powerpath
        if (this.pathMode == 1) {
            TileInformation tileInfo = this.getTileInformation(tileX, tileY);
            return !this.getTileInformation(tileInfo.getFatherX(), tileInfo.getFatherY()).isPowered();
        }
        // by default, we return the buildingpath
        return this.getTileInformation(tileX, tileY).isUsed();
    }

    @Override
    public int getHeightInTiles() {
        return this.height;
    }

    @Override
    public int getWidthInTiles() {
        return this.width;
    }

    public boolean isTileConnectedToPowersource(int tileX, int tileY) {
        for (Point tile : PowerManager.getPowersourceTiles()) {
            if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
                if (this.getPowerPath(tileX, tileY, tile.x, tile.y) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public float getCost(PathFindingContext arg0, int arg1, int arg2) {
        return 1.0f;
    }

    @Override
    public void pathFinderVisited(int arg0, int arg1) {
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void addOffset(float offsetX, float offsetY) {
        this.offsetX += offsetX;
        this.offsetY += offsetY;
    }

    public IsoTile getTile(int tileX, int tileY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            return tileMap[tileX][tileY];
        }
        return TileManager.getTile("unknown");
    }

    public void setTile(int tileX, int tileY, IsoTile tile, boolean isTileUsed) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            TileInformation obst = this.tileInfos[tileX][tileY];
            int fatherX = obst.getFatherX();
            int fatherY = obst.getFatherY();
            if (obst.isUsed()) {
                IsoTile removal = tileMap[fatherX][fatherY];
                tileMap[fatherX][fatherY] = tile;
                removal.onRemove(obst.getFatherX(), obst.getFatherY(), this);
            }

            IsoTile removal = tileMap[fatherX][fatherY];
            tileMap[tileX][tileY] = tile;
            removal.onRemove(obst.getFatherX(), obst.getFatherY(), this);

            if (isTileUsed) {
                this.setTileUsed(tileX, tileY, tileX, tileY);
            } else {
                this.setTileUnused(tileX, tileY);
            }
        }
    }

    public void setTileUsed(int tileX, int tileY, int fatherX, int fatherY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            this.tileInfos[tileX][tileY].setUsed(fatherX, fatherY);
        }
    }

    public TileInformation getTileInformation(int tileX, int tileY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            return this.tileInfos[tileX][tileY];
        }
        return new TileInformation(-1, -1);
    }

    protected TileInformation getUnsafeTileInformation(int tileX, int tileY) {
        return this.tileInfos[tileX][tileY];
    }

    public void setTileUnused(int tileX, int tileY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            this.tileInfos[tileX][tileY].setUnused();
        }
    }

    public boolean isTileUsed(int tileX, int tileY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            return this.tileInfos[tileX][tileY].isUsed();
        }
        return true;
    }

    public IsoTile getFatherTile(int tileX, int tileY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            return this.tileInfos[tileX][tileY].getFather(this);
        }
        return TileManager.getTile("unknown");
    }

    public void render() {
        this.render(0, this.width, 0, this.height - 1);
    }

    public abstract void render(int minX, int maxX, int minY, int maxY);

    public abstract int getIsoX(int x, int y);

    public abstract int getIsoY(int x, int y);

    public abstract int getTileX(float mouseX, float mouseY);

    public abstract int getTileY(float mouseX, float mouseY);

    public abstract IsoTile getNorthEast(int tileX, int tileY);

    public abstract IsoTile getSouthEast(int tileX, int tileY);

    public abstract IsoTile getSouthWest(int tileX, int tileY);

    public abstract IsoTile getNorthWest(int tileX, int tileY);

    public abstract TileInformation getNorthEastInfo(int tileX, int tileY);

    public abstract TileInformation getSouthEastInfo(int tileX, int tileY);

    public abstract TileInformation getSouthWestInfo(int tileX, int tileY);

    public abstract TileInformation getNorthWestInfo(int tileX, int tileY);

    public IsoTile[][] getTileMap() {
        return tileMap;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getHalfTileWidth() {
        return halfTileWidth;
    }

    public int getHalfTileHeight() {
        return halfTileHeight;
    }
}
