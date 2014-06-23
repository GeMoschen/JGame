package de.gemo.game.terrain.entities;

import java.io.*;
import java.nio.*;
import java.util.*;

import org.lwjgl.opengl.*;

import de.gemo.game.terrain.utils.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class World implements IRenderObject {

    private static final int BYTES_PER_PIXEL = 4;

    private ByteBuffer textureBuffer;
    private int textureID = -1;
    private int width, height;

    private TexData backgroundTexture;
    private TexData grassTexture;

    private TerrainSettings terrainSettings;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.createWorld(width, height);
    }

    public void createWorld(int width, int height) {
        try {
            this.backgroundTexture = new TexData("resources/terrain/rock_terrain.jpg");
            this.grassTexture = new TexData("resources/grasses/grass.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[][] terrainData = new int[width][height];

        if (this.textureBuffer == null) {
            this.textureBuffer = ByteBuffer.allocateDirect(terrainData.length * terrainData[0].length * BYTES_PER_PIXEL);
        } else {
            this.textureBuffer.clear();
        }

        this.createPerlinWorld(terrainData);
        this.paintTerrainTexture(terrainData);
        this.createGrass();
        this.createTexture();
    }

    private void createPerlinWorld(int[][] terrainData) {
        this.terrainSettings = new TerrainSettings();
        for (int x = 0; x < this.getWidth(); x++) {
            for (int wrongY = 0; wrongY < this.getHeight(); wrongY++) {
                int y = this.getHeight() - wrongY - 1;
                double noise = SimplexNoise.noise(x * this.terrainSettings.getFrequencyX() + this.terrainSettings.getOffsetX(), y * this.terrainSettings.getFrequencyY() + this.terrainSettings.getOffsetY());
                double addY = ((double) (y - (this.getHeight() / 12f)) / (double) this.getWidth());
                noise += 3.5d * addY;
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

                terrainData[x][y] = (noise >= (this.terrainSettings.getLowerCutOff() * (1d - ((double) (y) / (double) this.getHeight()) * 0.75)) && noise < this.terrainSettings.getUpperCutOff() ? 1 : 0);
                setPixelNoCheck(x, y, TerrainType.values()[terrainData[x][y]]);
            }
        }
    }

    public ByteBuffer getTerrainParts(int x, int y, int width, int height, boolean updateTexture) {
        long start = System.nanoTime();
        int minX = Math.max(0, x);
        int minY = Math.max(0, y);
        int maxX = Math.min(this.getWidth(), x + width);
        int maxY = Math.min(this.getHeight(), y + height);
        int dX = maxX - minX + 1;
        int dY = maxY - minY + 1;
        if (dX < 0 || dY < 0) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(dX * dY * BYTES_PER_PIXEL);
        for (y = minY; y < maxY; y++) {
            for (x = minX; x < maxX; x++) {
                this.textureBuffer.position(this.getBufferPosition(x, y));
                buffer.put(this.textureBuffer.get());
                buffer.put(this.textureBuffer.get());
                buffer.put(this.textureBuffer.get());
                buffer.put(this.textureBuffer.get());
                this.textureBuffer.position(0);
            }
        }
        buffer.position(0);
        if (updateTexture) {
            this.updateTexture(minX, minY, dX, dY, buffer);
        }

        long duration = System.nanoTime() - start;
        float d = duration / 1000000f;
        System.out.println("Terrain: " + d);
        return buffer;
    }

    private void paintTerrainTexture(int[][] terrainData) {
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                if (terrainData[x][y] == 1) {
                    this.textureBuffer.position(this.getBufferPosition(x, y));
                    this.textureBuffer.put(this.backgroundTexture.getR(x + (int) this.terrainSettings.getOffsetX(), y + (int) this.terrainSettings.getOffsetY()));
                    this.textureBuffer.put(this.backgroundTexture.getG(x + (int) this.terrainSettings.getOffsetX(), y + (int) this.terrainSettings.getOffsetY()));
                    this.textureBuffer.put(this.backgroundTexture.getB(x + (int) this.terrainSettings.getOffsetX(), y + (int) this.terrainSettings.getOffsetY()));
                    this.textureBuffer.put(this.backgroundTexture.getA(x + (int) this.terrainSettings.getOffsetX(), y + (int) this.terrainSettings.getOffsetY()));
                    this.textureBuffer.position(0);
                }
            }
        }
    }

    private void createGrass() {
        List<Integer> xList = new ArrayList<Integer>();
        List<Integer> yList = new ArrayList<Integer>();
        for (int y = this.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < this.getWidth(); x++) {
                boolean placeGrass_1 = !this.isPixelSolid(x, y - 1) && this.isPixelSolid(x, y);
                if (placeGrass_1) {
                    xList.add(x);
                    yList.add(y);
                }
            }
        }

        for (int i = 0; i < xList.size(); i++) {
            int x = xList.get(i);
            int y = yList.get(i);
            for (int offY = 0; offY < this.grassTexture.getHeight(); offY++) {
                if (!this.grassTexture.isFuchsia(x, offY)) {
                    this.textureBuffer.position(this.getBufferPosition(x, y + offY - 8));
                    this.textureBuffer.put(this.grassTexture.getR(x, offY));
                    this.textureBuffer.put(this.grassTexture.getG(x, offY));
                    this.textureBuffer.put(this.grassTexture.getB(x, offY));
                    this.textureBuffer.put(this.grassTexture.getA(x, offY));
                    this.textureBuffer.position(0);
                }
            }
        }
    }

    private void createTexture() {
        this.textureID = glGenTextures(); // Generate texture ID
        glBindTexture(GL_TEXTURE_2D, this.textureID); // Bind texture ID

        // Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Send texel data to OpenGL
        this.textureBuffer.position(0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.getWidth(), this.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, this.textureBuffer);
    }

    public void updateTexture(int x, int y, int width, int height, ByteBuffer buffer) {
        glBindTexture(GL_TEXTURE_2D, this.textureID);
        glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width - 1, height - 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
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
                    setPixel(midX + x, midY + y, terrainType, false);
                }
            }
        }

        // paint texture & grass
        if (terrainType.equals(TerrainType.TERRAIN)) {
            // paint texture
            for (int y = -radius; y <= radius; y++) {
                ySquared = y * y;
                for (int x = -radius; x <= radius; x++) {
                    xSquared = x * x;
                    XPlusY = xSquared + ySquared;
                    if (XPlusY <= radiusSquared && XPlusY >= innerRadius) {
                        int mX = midX + x;
                        int mY = midY + y;
                        this.textureBuffer.position(this.getBufferPosition(mX, mY));
                        this.textureBuffer.put(this.backgroundTexture.getR(mX + (int) this.terrainSettings.getOffsetX(), mY + (int) this.terrainSettings.getOffsetY()));
                        this.textureBuffer.put(this.backgroundTexture.getG(mX + (int) this.terrainSettings.getOffsetX(), mY + (int) this.terrainSettings.getOffsetY()));
                        this.textureBuffer.put(this.backgroundTexture.getB(mX + (int) this.terrainSettings.getOffsetX(), mY + (int) this.terrainSettings.getOffsetY()));
                        this.textureBuffer.put(this.backgroundTexture.getA(mX + (int) this.terrainSettings.getOffsetX(), mY + (int) this.terrainSettings.getOffsetY()));
                        this.textureBuffer.position(0);
                    }
                }
            }

            // paint grass
            for (int y = -radius; y <= radius; y++) {
                ySquared = y * y;
                for (int x = -radius; x <= radius; x++) {
                    xSquared = x * x;
                    XPlusY = xSquared + ySquared;
                    if (XPlusY <= radiusSquared && XPlusY >= innerRadius) {
                        boolean placeGrass_1 = !this.isPixelSolid(midX + x, midY + y - 1) && this.isPixelSolid(midX + x, midY + y);
                        if (placeGrass_1) {
                            for (int offY = 0; offY < 15; offY++) {
                                this.textureBuffer.position(this.getBufferPosition(midX + x, midY + y + offY));
                                this.textureBuffer.put((byte) 0);
                                this.textureBuffer.put((byte) (128 / (offY / 3 + 1) + 32));
                                this.textureBuffer.put((byte) 0);
                                this.textureBuffer.put((byte) 255);
                                this.textureBuffer.position(0);
                            }
                        }
                    }
                }
            }
        }
        long d = System.nanoTime() - start;
        float dur = d / 1000000f;
        System.out.println("Circle Duration: " + dur);
    }

    public TerrainType getTerrainType(int x, int y) {
        if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
            int oldPosition = this.textureBuffer.position();
            this.textureBuffer.position(this.getBufferPosition(x, y));
            byte r = this.textureBuffer.get();
            byte g = this.textureBuffer.get();
            byte b = this.textureBuffer.get();
            byte a = this.textureBuffer.get();
            this.textureBuffer.position(oldPosition);
            return TerrainType.byRGBA(r, g, b, a);
        }
        return TerrainType.INVALID;
    }

    public void setPixel(int x, int y, TerrainType terrainType, boolean replaceAir) {
        if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
            TerrainType type = this.getTerrainType(x, y);
            if (type.equals(terrainType) || (!replaceAir && type.equals(TerrainType.AIR))) {
                return;
            }

            this.textureBuffer.position(this.getBufferPosition(x, y));
            this.textureBuffer.put(terrainType.getR());
            this.textureBuffer.put(terrainType.getG());
            this.textureBuffer.put(terrainType.getB());
            this.textureBuffer.put(terrainType.getA());
            this.textureBuffer.position(0);
        }
    }

    private void setPixelNoCheck(int x, int y, TerrainType terrainType) {
        if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
            textureBuffer.position(this.getBufferPosition(x, y));
            this.textureBuffer.put(terrainType.getR());
            this.textureBuffer.put(terrainType.getG());
            this.textureBuffer.put(terrainType.getB());
            this.textureBuffer.put(terrainType.getA());
            textureBuffer.position(0);
        }
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
            return !this.getTerrainType(x, y).equals(TerrainType.AIR);
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
            return (y * this.getWidth() + x) * BYTES_PER_PIXEL;
        } else {
            return 0;
        }
    }

    @Override
    public void render() {
        glColor4f(1, 1, 1, 1);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);

        glBindTexture(GL_TEXTURE_2D, this.textureID);
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
