package de.gemo.game.tile;

public class Obstacle {
    private final int originalX, originalY;

    private int fatherX = -1, fatherY = -1;
    public boolean used = false;

    public Obstacle(int x, int y) {
        this.originalX = x;
        this.originalY = y;
    }

    public void setUsed(int fatherX, int fatherY) {
        this.fatherX = fatherX;
        this.fatherY = fatherY;
        this.used = true;
    }

    public void setUnused() {
        this.fatherX = -1;
        this.fatherY = -1;
        this.used = false;
    }

    public IsoTile getFather(IsoMap isoMap) {
        return isoMap.getTile(fatherX, fatherY);
    }

    public int getFatherX() {
        if (isUsed()) {
            return fatherX;
        } else {
            return this.originalX;
        }
    }

    public int getFatherY() {
        if (isUsed()) {
            return fatherY;
        } else {
            return this.originalY;
        }
    }

    public boolean isUsed() {
        return used;
    }
}
