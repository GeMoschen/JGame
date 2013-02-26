package de.gemo.game.tile;

public class TileInformation {
    private final int originalX, originalY;

    private int fatherX = -1, fatherY = -1;
    public boolean used = false;

    private float secureLevel = 0, secureLevelAlpha = 0;
    private boolean powered = false;

    public TileInformation(int x, int y) {
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

    public float getSecureLevel() {
        return secureLevel;
    }

    public float getSecureLevelAlpha() {
        return secureLevelAlpha;
    }

    public void addSecureLevel(float secureLevel) {
        this.secureLevel += secureLevel;
        if (this.secureLevel <= 1) {
            this.secureLevel = 0;
            this.secureLevelAlpha = 0f;
        } else {
            float lvlCap = this.secureLevel / 10f * 0.2f;
            if (lvlCap < 0.5f) {
                lvlCap += 0.2f;
            }
            if (lvlCap > 1) {
                lvlCap = 1;
            }
            this.secureLevelAlpha = lvlCap;
        }
    }

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isPowerSource() {
        return PowerManager.isPowersource(this.getFatherX(), this.getFatherY());
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public IsoTile getFather(IsoMap isoMap) {
        return isoMap.getTile(this.getFatherX(), this.getFatherY());
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

    public int getOriginalX() {
        return originalX;
    }

    public int getOriginalY() {
        return originalY;
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isValid() {
        return this.originalX > -1 && this.originalY > -1;
    }
}
