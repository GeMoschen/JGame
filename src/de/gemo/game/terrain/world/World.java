package de.gemo.game.terrain.world;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import de.gemo.game.terrain.entities.*;
import de.gemo.game.terrain.utils.*;
import de.gemo.game.terrain.world.generators.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class World implements IRenderObject {

    private int width, height;

    private boolean[][] terrainData;
    private TexData texTerrain, texGrass, texBackground;

    private int craterR = 152, craterG = 113, craterB = 82;
    private BufferedTexture terrainTexture;
    private AbstractWorldGenerator generator;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.createWorld(width, height);
    }

    public void createWorld(int width, int height) {
        try {
            this.texTerrain = new TexData("resources/terrain/wood_terrain.jpg");
            this.texBackground = new TexData("resources/terrainBackgrounds/background_wood.jpg");
            this.texGrass = new TexData("resources/grasses/wood.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.generator = new StandardWorldGenerator(this.getWidth(), this.getHeight());
        this.terrainTexture = new BufferedTexture(this.getWidth(), this.getHeight());
        this.terrainData = generator.generate();
        this.paintTerrain();
        this.createFX();
        this.createBridge();
        this.terrainTexture.update();
    }

    private void createBridge() {
        BridgeCreator.generate(this.terrainData, this.terrainTexture, this.width / 2, 800);
    }

    private void paintTerrain() {
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                if (this.terrainData[x][y]) {
                    this.terrainTexture.setPixel(x, y, this.texTerrain.getR(x, y), this.texTerrain.getG(x, y), this.texTerrain.getB(x, y), 255);
                } else {
                    this.terrainTexture.clearPixel(x, y);
                }
            }
        }
    }

    private void createFX() {
        List<Point> grassList = new ArrayList<Point>();
        List<Point> left3D = new ArrayList<Point>();
        List<Point> right3D = new ArrayList<Point>();

        for (int y = this.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < this.getWidth(); x++) {
                boolean placeGrass_1 = !this.isPixelSolid(x, y - 1) && this.isPixelSolid(x, y);
                if (placeGrass_1) {
                    grassList.add(new Point(x, y));
                }

                boolean threeDEffectRight = (this.isPixelSolid(x, y) && !this.isPixelSolid(x + 1, y));
                if (threeDEffectRight) {
                    Vector2f normal = this.getNormal(x, y);
                    if (Math.abs(normal.getY()) < 0.95d) {
                        right3D.add(new Point(x, y));
                    }
                }

                boolean threeDEffectLeft = (this.isPixelSolid(x, y) && !this.isPixelSolid(x - 1, y));
                if (threeDEffectLeft) {
                    Vector2f normal = this.getNormal(x, y);
                    if (Math.abs(normal.getY()) < 0.95d) {
                        left3D.add(new Point(x, y));
                    }
                }
            }
        }

        // 3D-Effect RIGHT
        for (Point point : right3D) {
            int x = point.x;
            int y = point.y;
            for (int offX = 0; offX < 10; offX++) {
                int r = this.terrainTexture.getR(x - offX, y);
                int g = this.terrainTexture.getG(x - offX, y);
                int b = this.terrainTexture.getB(x - offX, y);
                float alpha = 0.8f - ((float) offX * 0.08f);
                r = this.getBlendedValue(r, 10 + offX * 10, alpha);
                g = this.getBlendedValue(g, 10 + offX * 10, alpha);
                b = this.getBlendedValue(b, 10 + offX * 10, alpha);
                this.terrainTexture.setPixel(x - offX, y, r, g, b, 255);
            }
        }

        // 3D-Effect LEFT
        for (Point point : left3D) {
            int x = point.x;
            int y = point.y;
            for (int offX = 0; offX < 8; offX++) {
                int r = this.terrainTexture.getR(x + offX, y);
                int g = this.terrainTexture.getG(x + offX, y);
                int b = this.terrainTexture.getB(x + offX, y);
                float alpha = 0.8f - ((float) offX * 0.1f);
                r = this.getBlendedValue(r, 55 + offX * 10, alpha);
                g = this.getBlendedValue(g, 55 + offX * 10, alpha);
                b = this.getBlendedValue(b, 55 + offX * 10, alpha);
                this.terrainTexture.setPixel(x + offX, y, r, g, b, 255);
            }
        }

        // grass
        for (Point point : grassList) {
            int x = point.x;
            int y = point.y;
            for (int offY = 0; offY < this.texGrass.getHeight(); offY++) {
                if (!this.texGrass.isFuchsia(x, offY)) {
                    int newY = y + offY - 8;
                    if (newY < 0 || newY >= this.height) {
                        continue;
                    }
                    this.terrainData[x][y + offY - 8] = true;
                    this.terrainTexture.setPixel(x, y + offY - 8, this.texGrass.getR(x, offY), this.texGrass.getG(x, offY), this.texGrass.getB(x, offY), 255);
                }
            }
        }
    }

    public void explode(int midX, int midY, int radius) {
        this.explode(midX, midY, radius, 0);
    }

    public void explode(int midX, int midY, int radius, int airRadius) {
        // crater
        this.fillCircle(midX, midY, radius, 7, TerrainType.CRATER);

        // background
        this.fillCircle(midX, midY, radius - 7, radius - 7, TerrainType.BACKGROUND);

        // air
        if (airRadius > 0) {
            this.fillCircle(midX, midY, airRadius, airRadius, TerrainType.AIR);
        }

        // update texture
        int leftX = midX - radius - 1;
        int topY = midY - radius - 1;
        this.terrainTexture.updatePartial(leftX, topY, radius * 2 + 2, radius * 2 + 2);
    }

    public boolean isOutOfEntityBounds(Vector2f vector) {
        return this.isOutOfEntityBounds(vector.getX(), vector.getY());
    }

    public boolean isOutOfEntityBounds(float x, float y) {
        return x < -50 || x > this.width + 50 || y > this.getHeight() + 50;
    }

    private void fillCircle(int midX, int midY, int radius, int wallThickness, TerrainType terrainType) {
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

                    if (terrainType.equals(TerrainType.AIR)) {
                        // air
                        this.terrainTexture.clearPixel(newX, newY);
                        this.terrainData[newX][newY] = false;
                    } else if (terrainType.equals(TerrainType.CRATER)) {
                        // crater
                        if (this.terrainData[newX][newY]) {
                            this.terrainTexture.setPixel(newX, newY, craterR, craterG, craterB, 255);
                        }
                    } else if (terrainType.equals(TerrainType.BACKGROUND)) {
                        // background
                        if (this.terrainData[newX][newY]) {
                            this.terrainTexture.setPixel(newX, newY, this.texBackground.getR(newX, newY), this.texBackground.getG(newX, newY), this.texBackground.getB(newX, newY), 255);
                            this.terrainData[newX][newY] = false;
                        }
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

    private int getBlendedValue(int background, int foreground, float alphaForeground) {
        float floatBackground = (float) background / 255f;
        float alphaBackground = 1f;
        float floatForeground = (float) foreground / 255f;
        float result = floatForeground * alphaForeground + floatBackground * alphaBackground * (1f - alphaForeground);
        return (int) (result * 255);
    }

    @Override
    public void render() {
        glColor4f(1, 1, 1, 1);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        this.renderTexture();
    }

    private void renderTexture() {
        this.terrainTexture.bind();
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
        this.terrainTexture.unbind();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

}
