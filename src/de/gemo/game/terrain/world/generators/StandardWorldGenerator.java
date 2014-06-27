package de.gemo.game.terrain.world.generators;

import de.gemo.game.terrain.utils.*;

public class StandardWorldGenerator extends AbstractWorldGenerator {

    public StandardWorldGenerator(int width, int height) {
        super(new TerrainSettings(), width, height);
    }

    @Override
    protected boolean[][] createPerlinWorld() {
        boolean[][] terrainData = new boolean[this.getWidth()][this.getHeight()];
        for (int x = 0; x < this.getWidth(); x++) {
            for (int wrongY = 0; wrongY < this.getHeight(); wrongY++) {
                int y = this.getHeight() - wrongY - 1;
                double noise = SimplexNoise.noise(x * this.terrainSettings.getFrequencyX() + this.terrainSettings.getOffsetX(), y * this.terrainSettings.getFrequencyY() + this.terrainSettings.getOffsetY());
                double addY = ((double) (y) / (double) this.getHeight());
                noise += 3.25f * addY;
                // left
                double dX = (double) x / (this.getWidth() / 2d);
                if (dX < 1) {
                    noise *= dX;
                }

                // right
                dX = Math.abs(x - this.getWidth()) / (this.getWidth() / 2d);
                if (dX < 1) {
                    noise *= dX;
                }

                // middle
                if (x > 0) {
                    double distX = Math.abs((double) x - ((double) this.getWidth() / 2f));
                    if (distX < ((double) this.getWidth() / 2f)) {
                        distX = distX / ((double) this.getWidth() / 2f);
                        noise *= distX;
                    }
                }

                terrainData[x][y] = (noise >= this.terrainSettings.getLowerCutOff() && noise < this.terrainSettings.getUpperCutOff());
                if (x < 5 || x > this.getWidth() - 5) {
                    terrainData[x][y] = true;
                }
            }
        }
        return terrainData;
    }

}
