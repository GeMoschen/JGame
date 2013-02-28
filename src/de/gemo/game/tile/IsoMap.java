package de.gemo.game.tile;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.newdawn.slick.util.pathfinding.heuristics.ManhattanHeuristic;

import de.gemo.engine.particles.Emitter;
import de.gemo.engine.particles.FireEmitter;
import de.gemo.engine.particles.ParticleSystem;
import de.gemo.engine.particles.SmokeEmitter;
import de.gemo.game.tile.set.TileType;

public abstract class IsoMap implements TileBasedMap {

    public static ParticleSystem particleSystem;
    public static Emitter smokeEmitter, fireEmitter;

    protected IsoTile[][] tileMap;
    protected IsoTile[][] overlayMap;

    protected TileInformation[][] tileInfos;

    public static boolean SHOW_POLLUTION = false;
    public static boolean SHOW_SECURITY = false;
    public static boolean SHOW_POWER = false;
    public static boolean SHOW_JOBS = false;

    protected int width, height;
    protected int tileWidth, tileHeight, halfTileWidth, halfTileHeight;

    protected float offsetX, offsetY;

    // vars for pathfinding
    private AStarPathFinder aStar;
    private int pathMode = 0;
    private ArrayList<TileType> ignoredSearchTypes = new ArrayList<TileType>();

    public IsoMap(int width, int height, int tileWidth, int tileHeight) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.halfTileWidth = (int) (this.tileWidth / 2f);
        this.halfTileHeight = (int) (this.tileHeight / 2f);
        this.tileMap = new IsoTile[width][height];
        this.overlayMap = new IsoTile[width][height];
        this.tileInfos = new TileInformation[width][height];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.tileMap[x][y] = TileManager.getTile("grass");
                this.overlayMap[x][y] = null;
                this.tileInfos[x][y] = new TileInformation(x, y);
            }
        }
        this.offsetX = 0;
        this.offsetY = 0;

        this.initAStar();

        // initialize particlesystem
        particleSystem = new ParticleSystem();
        smokeEmitter = new SmokeEmitter(particleSystem, 0, 0);
        fireEmitter = new FireEmitter(particleSystem, 0, 0);
    }

    private void initAStar() {
        this.aStar = new AStarPathFinder(this, this.width * 3, false, new ManhattanHeuristic(0));
    }

    public Path getBuildingPath(int startX, int startY, int goalX, int goalY, TileType... types) {
        // BUILDINGS = 0
        this.pathMode = 0;
        this.ignoredSearchTypes.clear();
        for (TileType type : types) {
            this.ignoredSearchTypes.add(type);
        }
        return this.aStar.findPath(null, startX, startY, goalX, goalY);
    }

    public Path getPowerPath(int startX, int startY, int goalX, int goalY) {
        // POWER = 1
        this.pathMode = 1;
        this.ignoredSearchTypes.clear();
        return this.aStar.findPath(null, startX, startY, goalX, goalY);
    }

    @Override
    public boolean blocked(PathFindingContext arg0, int tileX, int tileY) {
        if (this.pathMode == 1) {
            // powerpath
            TileInformation tileInfo = this.getTileInformation(tileX, tileY);
            return !this.getTileInformation(tileInfo.getFatherX(), tileInfo.getFatherY()).isPowered();
        }
        // by default, we return the buildingpath
        boolean ignored = false;
        boolean found = false;
        for (TileType type : ignoredSearchTypes) {
            if (this.getTile(tileX, tileY).getType().equals(type)) {
                found = true;
                break;
            }
        }
        ignored = found;
        return this.getTileInformation(tileX, tileY).isUsed() && !ignored;
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

    public void setOverlay(int tileX, int tileY, IsoTile tile) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            this.overlayMap[tileX][tileY] = tile;
        }
    }

    public IsoTile getOverlay(int tileX, int tileY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            return this.overlayMap[tileX][tileY];
        }
        return null;
    }

    public IsoTile getOverlayNotNull(int tileX, int tileY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            if (this.hasOverlay(tileX, tileY)) {
                return this.overlayMap[tileX][tileY];
            }
        }
        return TileManager.getTile("unknown");
    }

    public boolean hasOverlay(int tileX, int tileY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            return this.overlayMap[tileX][tileY] != null;
        }
        return false;
    }

    public void removeOverlay(int tileX, int tileY) {
        if (tileX > -1 && tileX < this.width && tileY > -1 && tileY < this.height) {
            this.overlayMap[tileX][tileY] = null;
        }
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

    public abstract IsoTile getNorthEastOverlay(int tileX, int tileY);

    public abstract IsoTile getSouthEastOverlay(int tileX, int tileY);

    public abstract IsoTile getSouthWestOverlay(int tileX, int tileY);

    public abstract IsoTile getNorthWestOverlay(int tileX, int tileY);

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
