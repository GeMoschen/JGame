package de.gemo.game.terrain.core;

import java.io.*;
import java.nio.*;

import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;

import de.gemo.gameengine.units.*;

public class World implements IRenderObject {

    private static final int BYTES_PER_PIXEL = 4;

    private int[][] terrainData;
    private ByteBuffer textureBuffer;
    private int textureID = -1;

    private TexData backgroundTexture;

    public World(int width, int height) {
        this.createTerrain(2 * 512, 1 * 512);
    }

    public int getWidth() {
        return this.terrainData.length;
    }

    public int getHeight() {
        return this.terrainData[0].length;
    }

    private int updateTexture(int currentID, ByteBuffer buffer) {
        buffer.position(0);
        long start = System.nanoTime();
        if (currentID == -1) {
            currentID = glGenTextures(); // Generate texture ID
            glBindTexture(GL_TEXTURE_2D, currentID); // Bind texture ID

            // Setup wrap mode
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

            // Setup texture scaling filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // Send texel data to OpenGL
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.terrainData.length, this.terrainData[0].length, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } else {
            glBindTexture(GL_TEXTURE_2D, currentID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.terrainData.length, this.terrainData[0].length, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }

        long d = System.nanoTime() - start;
        float dur = d / 1000000f;
        System.out.println("Update Texture: " + dur);
        return currentID;
    }

    public void createTerrain(int width, int height) {
        try {
            this.backgroundTexture = new TexData("resources/dirt.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.terrainData = new int[width][height];

        if (this.textureBuffer == null) {
            this.textureBuffer = ByteBuffer.allocateDirect(terrainData.length * terrainData[0].length * BYTES_PER_PIXEL);
        } else {
            this.textureBuffer.clear();
        }

        this.createPerlinWorld();
        this.updateTerrainTexture();
        this.createGrass();
        this.updateTexture();
    }

    private void createPerlinWorld() {
        float freq = 0.008f;

        float offX = (float) (Math.random() * (Math.random() * 50000));
        float offY = (float) (Math.random() * (Math.random() * 50000));

        float cutOff = 0.4f;
        float upperCutOff = 20f;

        for (int x = 0; x < terrainData.length; x++) {
            for (int wrongY = 0; wrongY < terrainData[0].length; wrongY++) {
                int y = terrainData[0].length - wrongY - 1;
                double noise = SimplexNoise.noise(x * freq + offX, y * freq + offY);
                double addY = ((double) (y - 300) / (double) terrainData[0].length);
                noise += 6d * addY;

                // left
                double dX = (double) x / 512d;
                if (dX < 1) {
                    noise *= dX;
                }

                // right
                dX = Math.abs(x - terrainData.length) / 512d;
                if (dX < 1) {
                    noise *= dX;
                }

                this.terrainData[x][y] = noise >= cutOff * (1d - ((double) (y) / (double) terrainData[0].length) * 0.75) && noise < upperCutOff ? 1 : 0;
                this.setPixelNoCheck(x, y, this.terrainData[x][y]);
            }
        }
    }

    private void createGrass() {
        for (int y = 0; y < this.terrainData[0].length; y++) {
            for (int x = 0; x < this.terrainData.length; x++) {
                boolean placeGrass_1 = !this.isPixelSolid(x, y - 1) && this.isPixelSolid(x, y);
                if (placeGrass_1) {
                    if (this.isUnderFreeSky(x, y)) {
                        textureBuffer.position(this.getBufferPosition(x, y));
                        textureBuffer.put(this.backgroundTexture.getR(x, y));
                        textureBuffer.put((byte) Math.min(255, this.backgroundTexture.getG(x, y) + 96));
                        textureBuffer.put(this.backgroundTexture.getB(x, y));
                        textureBuffer.put(this.backgroundTexture.getA(x, y));

                        textureBuffer.position(this.getBufferPosition(x, y + 1));
                        textureBuffer.put(this.backgroundTexture.getR(x, y + 1));
                        textureBuffer.put((byte) Math.min(255, this.backgroundTexture.getG(x, y + 1) + 48));
                        textureBuffer.put(this.backgroundTexture.getB(x, y + 1));
                        textureBuffer.put(this.backgroundTexture.getA(x, y + 1));
                    } else {
                        textureBuffer.position(this.getBufferPosition(x, y));
                        textureBuffer.put(this.backgroundTexture.getR(x, y));
                        textureBuffer.put((byte) Math.min(255, this.backgroundTexture.getG(x, y) + 48));
                        textureBuffer.put(this.backgroundTexture.getB(x, y));
                        textureBuffer.put(this.backgroundTexture.getA(x, y));

                        textureBuffer.position(this.getBufferPosition(x, y + 1));
                        textureBuffer.put(this.backgroundTexture.getR(x, y + 1));
                        textureBuffer.put((byte) Math.min(255, this.backgroundTexture.getG(x, y + 1) + 16));
                        textureBuffer.put(this.backgroundTexture.getB(x, y + 1));
                        textureBuffer.put(this.backgroundTexture.getA(x, y + 1));
                    }

                    textureBuffer.position(0);
                }
            }
        }
    }

    private void updateTerrainTexture() {
        for (int y = 0; y < this.terrainData[0].length; y++) {
            for (int x = 0; x < this.terrainData.length; x++) {
                if (this.terrainData[x][y] == 1) {
                    textureBuffer.position(this.getBufferPosition(x, y));
                    textureBuffer.put(this.backgroundTexture.getR(x, y));
                    textureBuffer.put(this.backgroundTexture.getG(x, y));
                    textureBuffer.put(this.backgroundTexture.getB(x, y));
                    textureBuffer.put(this.backgroundTexture.getA(x, y));
                    textureBuffer.position(0);
                }
            }
        }
    }

    public void updateTexture() {
        this.textureID = updateTexture(this.textureID, this.textureBuffer);
    }

    public void filledCircle(int midX, int midY, int radius, int terrainType, boolean replaceAir) {
        this.filledCircle(midX, midY, radius, radius, terrainType, replaceAir);
    }

    public void filledCircle(int midX, int midY, int radius, int wallThickness, int terrainType, boolean replaceAir) {
        long start = System.nanoTime();
        int innerRadius = radius - wallThickness;
        innerRadius = innerRadius * innerRadius;
        int radiusSquared = radius * radius;
        int xSquared, ySquared, XPlusY;
        for (int y = -radius; y <= radius; y++) {
            ySquared = y * y;
            for (int x = -radius; x <= radius; x++) {
                xSquared = x * x;
                XPlusY = xSquared + ySquared;
                if (XPlusY <= radiusSquared && XPlusY >= innerRadius) {
                    setPixel(midX + x, midY + y, terrainType, replaceAir);
                }
            }
        }

        long d = System.nanoTime() - start;
        float dur = d / 1000000f;
        System.out.println("Circle Duration: " + dur);
    }

    private void setPixel(int x, int y, int terrainType, boolean replaceAir) {
        if (x >= 0 && y >= 0 && x < this.terrainData.length && y < this.terrainData[0].length) {
            if (this.terrainData[x][y] == terrainType || (!replaceAir && this.terrainData[x][y] == 0)) {
                return;
            }

            textureBuffer.position(this.getBufferPosition(x, y));
            if (terrainType == 1) {
                textureBuffer.put((byte) 255);
                textureBuffer.put((byte) 255);
                textureBuffer.put((byte) 255);
                textureBuffer.put((byte) 255);
            } else if (terrainType == 2) {
                textureBuffer.put((byte) 127);
                textureBuffer.put((byte) 127);
                textureBuffer.put((byte) 127);
                textureBuffer.put((byte) 255);
            } else if (terrainType == 0) {
                textureBuffer.put((byte) 0);
                textureBuffer.put((byte) 0);
                textureBuffer.put((byte) 0);
                textureBuffer.put((byte) 0);
            }
            textureBuffer.position(0);
            this.terrainData[x][y] = terrainType;
        }
    }

    private void setPixelNoCheck(int x, int y, int terrainType) {
        if (x >= 0 && y >= 0 && x < this.terrainData.length && y < this.terrainData[0].length) {
            textureBuffer.position(this.getBufferPosition(x, y));
            if (terrainType == 1) {
                textureBuffer.put((byte) 255);
                textureBuffer.put((byte) 255);
                textureBuffer.put((byte) 255);
                textureBuffer.put((byte) 255);
            } else if (terrainType == 2) {
                textureBuffer.put((byte) 127);
                textureBuffer.put((byte) 127);
                textureBuffer.put((byte) 127);
                textureBuffer.put((byte) 255);
            } else if (terrainType == 0) {
                textureBuffer.put((byte) 0);
                textureBuffer.put((byte) 0);
                textureBuffer.put((byte) 0);
                textureBuffer.put((byte) 0);
            }
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
        if (x >= 0 && y >= 0 && x < this.terrainData.length && y < this.terrainData[0].length) {
            return this.terrainData[x][y] != 0;
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
        if (x >= 0 && y >= 0 && x < this.terrainData.length && y < this.terrainData[0].length) {
            return (y * this.terrainData.length + x) * BYTES_PER_PIXEL;
        } else {
            return 0;
        }
    }

    @Override
    public void render() {
        glColor4f(1, 1, 1, 1);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, this.textureID);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex3i(0, 0, -1);

            glTexCoord2f(1, 0);
            glVertex3i(this.terrainData.length, 0, -1);

            glTexCoord2f(1, 1);
            glVertex3i(this.terrainData.length, this.terrainData[0].length, -1);

            glTexCoord2f(0, 1);
            glVertex3i(0, this.terrainData[0].length, -1);
        }
        glEnd();
    }

}
