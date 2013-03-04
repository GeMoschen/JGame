package de.gemo.game.tile;

import de.gemo.game.tile.manager.TileManager;

public class HeightMap {

    public float[][] Heights;
    public int sizeX, sizeY;

    private PerlinGenerator Perlin;

    public HeightMap(int sizeX, int sizeY, int seed) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        Heights = new float[sizeX][sizeY];
        Perlin = new PerlinGenerator(seed);
    }

    public void AddPerlinNoise(float f) {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                Heights[i][j] += Perlin.Noise(f * i / (float) sizeX, f * j / (float) sizeX, 0);
            }
        }
    }

    public void Perturb(float f, float d) {
        int u, v;
        float[][] temp = new float[sizeX][sizeX];
        for (int i = 0; i < sizeX; ++i) {
            for (int j = 0; j < sizeY; ++j) {
                u = i + (int) (Perlin.Noise(f * i / (float) sizeX, f * j / (float) sizeY, 0) * d);
                v = j + (int) (Perlin.Noise(f * i / (float) sizeX, f * j / (float) sizeY, 1) * d);
                if (u < 0)
                    u = 0;
                if (u >= sizeX)
                    u = sizeX - 1;
                if (v < 0)
                    v = 0;
                if (v >= sizeY)
                    v = sizeY - 1;
                temp[i][j] = Heights[u][v];
            }
        }
        Heights = temp;
    }

    public void Erode(float smoothness) {
        for (int i = 1; i < sizeX - 1; i++) {
            for (int j = 1; j < sizeY - 1; j++) {
                float d_max = 0.0f;
                int[] match = {0, 0};

                for (int u = -1; u <= 1; u++) {
                    for (int v = -1; v <= 1; v++) {
                        if (Math.abs(u) + Math.abs(v) > 0) {
                            float d_i = Heights[i][j] - Heights[i + u][j + v];
                            if (d_i > d_max) {
                                d_max = d_i;
                                match[0] = u;
                                match[1] = v;
                            }
                        }
                    }
                }

                if (0 < d_max && d_max <= (smoothness / (float) sizeX)) {
                    float d_h = 0.5f * d_max;
                    Heights[i][j] -= d_h;
                    Heights[i + match[0]][j + match[1]] += d_h;
                }
            }
        }
    }

    public void Smoothen() {
        for (int i = 1; i < sizeX - 1; ++i) {
            for (int j = 1; j < sizeY - 1; ++j) {
                float total = 0.0f;
                for (int u = -1; u <= 1; u++) {
                    for (int v = -1; v <= 1; v++) {
                        total += Heights[i + u][j + v];
                    }
                }

                Heights[i][j] = total / 9.0f;
            }
        }
    }

    public void printForest(IsoMap isoMap) {
        for (int i = 1; i < sizeX - 1; ++i) {
            for (int j = 1; j < sizeY - 1; ++j) {
                if (Heights[i][j] > 0.21f || (Heights[i][j] > 0.12f && Heights[i][j] < 0.16f)) {
                    isoMap.setTile(i, j, TileManager.getTile("tree_01"), true);
                    continue;
                }
                if (Heights[i][j] < -0.07f) {
                    isoMap.setTile(i, j, TileManager.getTile("water"), true);
                }

            }
        }
    }
}
