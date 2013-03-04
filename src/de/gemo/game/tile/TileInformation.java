package de.gemo.game.tile;

import de.gemo.game.tile.manager.PowerManager;

public class TileInformation {
    private final int originalX, originalY;

    private int fatherX = -1, fatherY = -1;
    public boolean used = false;

    private float secureLevel = 0, secureLevelAlpha = 0;
    private float pollutionLevel = 0, pollutionLevelAlpha = 0;
    private float jobLevel = 0, jobLevelAlpha = 0;

    private float satisfactionLevel = 0;
    private float tickLevel = 0;

    private boolean powered = false;

    public TileInformation(int x, int y) {
        this.originalX = x;
        this.originalY = y;
    }

    // /////////////////////////
    //
    // USED
    //
    // /////////////////////////

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

    // /////////////////////////
    //
    // SECURITY
    //
    // /////////////////////////

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
                lvlCap += 0.1f;
            }
            if (lvlCap > 1) {
                lvlCap = 1;
            }
            this.secureLevelAlpha = lvlCap;
        }
    }

    // /////////////////////////
    //
    // POLLUTION
    //
    // /////////////////////////

    public float getPollutionLevel() {
        return pollutionLevel;
    }

    public float getPollutionLevelAlpha() {
        return pollutionLevelAlpha;
    }

    public void addPollutionLevel(float pollutionLevel) {
        this.pollutionLevel += pollutionLevel;
        if (this.pollutionLevel <= 1) {
            this.pollutionLevel = 0;
            this.pollutionLevelAlpha = 0f;
        } else {
            float lvlCap = this.pollutionLevel * 2.5f / 255f * 2.5f;
            if (lvlCap < 0.2f) {
                lvlCap += 0.1f;
            }
            if (lvlCap > 1) {
                lvlCap = 1;
            }
            this.pollutionLevelAlpha = lvlCap;
        }
    }

    // /////////////////////////
    //
    // JOBS
    //
    // /////////////////////////

    public float getJobLevel() {
        return jobLevel;
    }

    public float getJobLevelAlpha() {
        return jobLevelAlpha;
    }

    public void addJobLevel(float jobLevel) {
        this.jobLevel += jobLevel;
        if (this.jobLevel <= 1) {
            this.jobLevel = 0;
            this.jobLevelAlpha = 0f;
        } else {
            float lvlCap = this.jobLevel * 2f / 255f * 2.5f;
            if (lvlCap < 0.2f) {
                lvlCap += 0.1f;
            }
            if (lvlCap > 1) {
                lvlCap = 1;
            }
            this.jobLevelAlpha = lvlCap;
        }
    }

    // /////////////////////////
    //
    // POWER
    //
    // /////////////////////////

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isPowerSource() {
        return PowerManager.isPowersource(this.getFatherX(), this.getFatherY());
    }

    public boolean setPowered(boolean powered) {
        if (powered != this.powered) {
            this.powered = powered;
            return true;
        }
        return false;
    }

    // /////////////////////////
    //
    // OTHER THINGS
    //
    // /////////////////////////

    public void addSatisfaction(float satisfaction) {
        this.satisfactionLevel += satisfaction;
        if (this.satisfactionLevel < -100) {
            this.satisfactionLevel = -100;
        }
        if (this.satisfactionLevel > 100) {
            this.satisfactionLevel = 100;
        }
    }

    public float getSatisfactionLevel() {
        return satisfactionLevel;
    }

    public void addTickLevel(float tickLevel) {
        this.tickLevel += tickLevel;
        if (this.tickLevel < -15) {
            this.tickLevel = -15;
        } else if (this.tickLevel > 15) {
            this.tickLevel = 15;
        }
    }

    public float getTickLevel() {
        return tickLevel;
    }

    public void resetStatistics() {
        this.satisfactionLevel = 0;
        this.tickLevel = 0;
    }

    // /////////////////////////
    //
    // FATHER
    //
    // /////////////////////////

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
