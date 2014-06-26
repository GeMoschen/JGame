package de.gemo.game.terrain.world;

import java.io.*;
import java.nio.*;
import java.util.*;

import org.lwjgl.opengl.*;

import de.gemo.game.terrain.entities.*;
import de.gemo.game.terrain.utils.*;
import de.gemo.game.terrain.world.generators.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class World implements IRenderObject {

    private int width, height;

    private boolean[][] terrainData;
    private TexData terrainTexture, grassTexture, backgroundTexture;
    private BufferedTexture terrainTex;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.createWorld(width, height);
    }

    public void createWorld(int width, int height) {
        try {
            this.terrainTexture = new TexData("resources/terrain/wood_terrain.jpg");
            this.backgroundTexture = new TexData("resources/terrainBackgrounds/background_wood.jpg");
            this.grassTexture = new TexData("resources/grasses/wood.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.terrainTex = new BufferedTexture(this.getWidth(), this.getHeight());

        AbstractWorldGenerator generator = new StandardWorldGenerator(this.getWidth(), this.getHeight());

        this.terrainData = generator.generate();
        this.paintTerrainTexture(generator);
        this.createEffects();
    }

    private void paintTerrainTexture(AbstractWorldGenerator generator) {
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                if (this.terrainData[x][y]) {
                    this.terrainTex.setPixel(x, y, this.terrainTexture.getR(x, y), this.terrainTexture.getG(x, y), this.terrainTexture.getB(x, y), 255);
                } else {
                    this.terrainTex.clearPixel(x, y);
                }
            }
        }
        this.terrainTex.update();
    }

    private int getBlendedValue(int background, int foreground, float alphaForeground) {
        float floatBackground = (float) background / 255f;
        float alphaBackground = 1f;
        float floatForeground = (float) foreground / 255f;
        float result = floatForeground * alphaForeground + floatBackground * alphaBackground * (1f - alphaForeground);
        return (int) (result * 255);
    }

    private void createEffects() {
        List<Integer> xListGrass = new ArrayList<Integer>();
        List<Integer> yListGrass = new ArrayList<Integer>();

        List<Integer> xList3DRight = new ArrayList<Integer>();
        List<Integer> yList3DRight = new ArrayList<Integer>();

        List<Integer> xList3DLeft = new ArrayList<Integer>();
        List<Integer> yList3DLeft = new ArrayList<Integer>();

        for (int y = this.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < this.getWidth(); x++) {
                boolean placeGrass_1 = !this.isPixelSolid(x, y - 1) && this.isPixelSolid(x, y);
                if (placeGrass_1) {
                    xListGrass.add(x);
                    yListGrass.add(y);
                }

                boolean threeDEffectRight = (this.isPixelSolid(x, y) && !this.isPixelSolid(x + 1, y));
                if (threeDEffectRight) {
                    Vector2f normal = this.getNormal(x, y);
                    if (Math.abs(normal.getY()) < 0.95d) {
                        xList3DRight.add(x);
                        yList3DRight.add(y);
                    }
                }

                boolean threeDEffectLeft = (this.isPixelSolid(x, y) && !this.isPixelSolid(x - 1, y));
                if (threeDEffectLeft) {
                    Vector2f normal = this.getNormal(x, y);
                    if (Math.abs(normal.getY()) < 0.95d) {
                        xList3DLeft.add(x);
                        yList3DLeft.add(y);
                    }
                }
            }
        }

        // 3D-Effect RIGHT
        for (int i = 0; i < xList3DRight.size(); i++) {
            int x = xList3DRight.get(i);
            int y = yList3DRight.get(i);
            for (int offX = 0; offX < 10; offX++) {
                int r = this.terrainTex.getR(x - offX, y);
                int g = this.terrainTex.getG(x - offX, y);
                int b = this.terrainTex.getB(x - offX, y);
                float alpha = 0.8f - ((float) offX * 0.08f);
                r = this.getBlendedValue(r, 10 + offX * 10, alpha);
                g = this.getBlendedValue(g, 10 + offX * 10, alpha);
                b = this.getBlendedValue(b, 10 + offX * 10, alpha);
                this.terrainTex.setPixel(x, y - offX, r, g, b, 255);
            }
        }

        // 3D-Effect LEFT
        for (int i = 0; i < xList3DLeft.size(); i++) {
            int x = xList3DLeft.get(i);
            int y = yList3DLeft.get(i);
            for (int offX = 0; offX < 8; offX++) {
                int r = this.terrainTex.getR(x + offX, y);
                int g = this.terrainTex.getG(x + offX, y);
                int b = this.terrainTex.getB(x + offX, y);
                float alpha = 0.8f - ((float) offX * 0.1f);
                r = this.getBlendedValue(r, 55 + offX * 10, alpha);
                g = this.getBlendedValue(g, 55 + offX * 10, alpha);
                b = this.getBlendedValue(b, 55 + offX * 10, alpha);
                this.terrainTex.setPixel(x, y + offX, r, g, b, 255);
            }
        }

        // grass
        for (int i = 0; i < xListGrass.size(); i++) {
            int x = xListGrass.get(i);
            int y = yListGrass.get(i);
            for (int offY = 0; offY < this.grassTexture.getHeight(); offY++) {
                if (!this.grassTexture.isFuchsia(x, offY)) {
                    int newY = y + offY - 8;
                    if (newY < 0 || newY >= this.height) {
                        continue;
                    }
                    this.terrainData[x][y + offY - 8] = true;
                    this.terrainTex.setPixel(x, y + offY - 8, this.grassTexture.getR(x, offY), this.grassTexture.getG(x, offY), this.grassTexture.getB(x, offY), 255);
                }
            }
        }

        this.terrainTex.update();
    }

    public void updateTexture(int x, int y, int width, int height) {
        this.terrainTex.updatePartial(x, y, width, height);
    }

    public void filledCircle(int midX, int midY, int radius, TerrainType terrainType, boolean replaceAir) {
        this.filledCircle(midX, midY, radius, radius, terrainType, replaceAir);
    }

    public void filledCircle(int midX, int midY, int radius, int wallThickness, TerrainType terrainType, boolean replaceAir) {
        long start = System.nanoTime();
        int innerRadius = radius - wallThickness;
        innerRadius = innerRadius * innerRadius;
        int radiusSquared = radius * radius;
        int xSquared, ySquared, XPlusY;

        // make circle
        for (int y = -radius; y <= radius; y++) {
            ySquared = y * y;
            for (int x = -radius; x <= radius; x++) {
                xSquared = x * x;
                XPlusY = xSquared + ySquared;
                if (XPlusY <= radiusSquared && XPlusY >= innerRadius) {
                    int newX = midX + x;
                    int newY = midY + y;

                    if (newX < 0 || newY < 0 || newX >= this.width || newY >= this.height) {
                        continue;
                    }

                    if (replaceAir || (!replaceAir && this.terrainData[newX][newY])) {
                        this.terrainTex.setPixel(midX + x, midY + y, terrainType.getR(), terrainType.getG(), terrainType.getB(), terrainType.getA());
                    }
                }
            }
        }
        long d = System.nanoTime() - start;
        float dur = d / 1000000f;
        System.out.println("Circle Duration: " + dur);
    }

    public boolean isPixelSolid(int x, int y) {
        return this.isPixelSolid(x, y, true);
    }

    public boolean isUnderFreeSky(int x, int y) {
        for (int thisY = y - 1; thisY >= 0; thisY--) {
            if (this.isPixelSolid(x, thisY)) {
                return false;
            }
        }
        return true;
    }

    public boolean isPixelSolid(int x, int y, boolean defaultValue) {
        if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
            return this.terrainData[x][y];
        } else {
            return defaultValue;
        }
    }

    public Vector2f getNormal(int x, int y) {
        Vector2f average = new Vector2f();
        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                if (this.isPixelSolid(x + i, y + j)) {
                    Vector2f.sub(average, new Vector2f(i, j), average);
                }
            }
        }
        return Vector2f.normalize(average);
    }

    private int getBufferPosition(int x, int y) {
        if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
            return (y * this.getWidth() + x) * CONSTANTS.BYTES_PER_PIXEL;
        } else {
            return 0;
        }
    }

    public static int getBufferPosition(int x, int y, int maxWidth, int maxHeight) {
        if (x >= 0 && y >= 0 && x < maxWidth && y < maxHeight) {
            return (y * maxWidth + x) * CONSTANTS.BYTES_PER_PIXEL;
        } else {
            return 0;
        }
    }

    @Override
    public void render() {
        glColor4f(1, 1, 1, 1);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);

        this.terrainTex.bind();
        this.renderTexture();
    }

    private void renderTexture() {
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex3i(0, 0, -1);

            glTexCoord2f(1, 0);
            glVertex3i(this.getWidth(), 0, -1);

            glTexCoord2f(1, 1);
            glVertex3i(this.getWidth(), this.getHeight(), -1);

            glTexCoord2f(0, 1);
            glVertex3i(0, this.getHeight(), -1);
        }
        glEnd();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

}
