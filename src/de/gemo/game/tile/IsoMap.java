package de.gemo.game.tile;

public abstract class IsoMap {
    protected IsoTile[][] tileMap;
    protected Obstacle[][] obstacles;

    protected int width, height;
    protected int tileWidth, tileHeight, halfTileWidth, halfTileHeight;

    protected float offsetX, offsetY;

    public IsoMap(int width, int height, int tileWidth, int tileHeight) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.halfTileWidth = (int) (this.tileWidth / 2f);
        this.halfTileHeight = (int) (this.tileHeight / 2f);
        this.tileMap = new IsoTile[width][height];
        this.obstacles = new Obstacle[width][height];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.tileMap[x][y] = TileManager.getTile("grass");
                this.obstacles[x][y] = new Obstacle(x, y);
            }
        }
        this.offsetX = 0;
        this.offsetY = 0;
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
        if (tileX >= 0 && tileX < this.width && tileY >= 0 && tileY < this.height) {
            return tileMap[tileX][tileY];
        }
        return TileManager.getTile("unknown");
    }

    public void setTile(int tileX, int tileY, IsoTile tile, boolean isTileUsed) {
        if (tileX >= 0 && tileX < this.width && tileY >= 0 && tileY < this.height) {
            Obstacle obst = this.obstacles[tileX][tileY];
            int fatherX = obst.getFatherX();
            int fatherY = obst.getFatherY();
            if (obst.isUsed()) {
                tileMap[fatherX][fatherY].onRemove(obst.getFatherX(), obst.getFatherY(), this);
                tileMap[fatherX][fatherY] = tile;
            }
            tileMap[tileX][tileY] = tile;
            if (isTileUsed) {
                this.setTileUsed(tileX, tileY, tileX, tileY);
            } else {
                this.setTileUnused(tileX, tileY);
            }
        }
    }

    public void setTileUsed(int tileX, int tileY, int fatherX, int fatherY) {
        if (tileX >= 0 && tileX < this.width && tileY >= 0 && tileY < this.height) {
            this.obstacles[tileX][tileY].setUsed(fatherX, fatherY);
        }
    }

    public void setTileUnused(int tileX, int tileY) {
        if (tileX >= 0 && tileX < this.width && tileY >= 0 && tileY < this.height) {
            this.obstacles[tileX][tileY].setUnused();
        }
    }

    public boolean isTileUsed(int tileX, int tileY) {
        if (tileX >= 0 && tileX < this.width && tileY >= 0 && tileY < this.height) {
            return this.obstacles[tileX][tileY].isUsed();
        }
        return true;
    }

    public IsoTile getFatherTile(int tileX, int tileY) {
        if (tileX >= 0 && tileX < this.width && tileY >= 0 && tileY < this.height) {
            return this.obstacles[tileX][tileY].getFather(this);
        }
        return TileManager.getTile("unknown");
    }

    public void render() {
        this.render(0, this.width, 0, this.height - 1);
    }

    public abstract void render(int minX, int maxX, int minY, int maxY);

    public abstract int getIsoX(int x, int y);

    public abstract int getIsoY(int x, int y);

    public abstract int getTileX(float mouseX, float mouseY, boolean bitMask);

    public abstract int getTileY(float mouseX, float mouseY, boolean bitMask);

    public abstract IsoTile getNorthEast(int tileX, int tileY);

    public abstract IsoTile getSouthEast(int tileX, int tileY);

    public abstract IsoTile getSouthWest(int tileX, int tileY);

    public abstract IsoTile getNorthWest(int tileX, int tileY);

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
