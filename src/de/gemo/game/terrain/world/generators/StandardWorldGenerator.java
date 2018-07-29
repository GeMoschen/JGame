package de.gemo.game.terrain.world.generators;

import de.gemo.game.terrain.utils.SimplexNoise;
import de.gemo.game.terrain.utils.TerrainSettings;

public class StandardWorldGenerator extends AbstractWorldGenerator {

    public StandardWorldGenerator(int width, int height) {
        super(new TerrainSettings(), width, height);
    }

    @Override
    protected boolean[][] createPerlinWorld() {
        boolean[][] terrainData = new boolean[getWidth()][getHeight()];
        for (int x = 0; x < getWidth(); x++) {
            for (int wrongY = 0; wrongY < getHeight(); wrongY++) {
                int y = getHeight() - wrongY - 1;
                double noise = SimplexNoise.noise(x * _terrainSettings.getFrequencyX() + _terrainSettings.getOffsetX(), y * _terrainSettings.getFrequencyY() + _terrainSettings.getOffsetY());
                double addY = ((double) (y) / (double) getHeight());
                noise += 1.25f * addY;
                // left cutoff for level
                double dX = (double) x / (getWidth() / 8d);
                if (dX < 1) {
                    noise *= dX;
                }

                // right cutoff for level
                dX = Math.abs(x - getWidth()) / (getWidth() / 8d);
                if (dX < 1) {
                    noise *= dX;
                }

                // middle cutoff - lower values = more space in the middle ; higher values = less space in the middle
                final float cutoffMiddle = 3.8f;
                if (x > 0) {
                    double distX = Math.abs((double) x - ((double) getWidth() / 2f));
                    if (distX < ((double) getWidth() / cutoffMiddle)) {
                        distX = distX / ((double) getWidth() / cutoffMiddle);
                        noise *= distX;
                    }
                }

                // cutoff
                terrainData[x][y] = (noise >= _terrainSettings.getLowerCutOff() && noise < _terrainSettings.getUpperCutOff());

                // level borders left & right
                if (x < 5 || x > getWidth() - 5) {
                    terrainData[x][y] = true;
                }
            }
        }
        return terrainData;
    }

}
