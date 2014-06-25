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

    private static final int BYTES_PER_PIXEL = 4;

    private ByteBuffer terrainBuffer, backgroundBuffer;
    private int terrainTextureID = -1, backgroundTextureID = -1;
    private int width, height;

    private TexData terrainTexture, grassTexture, backgroundTexture;

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

        if (this.terrainBuffer == null) {
            this.terrainBuffer = ByteBuffer.allocateDirect(this.getWidth() * this.getHeight() * BYTES_PER_PIXEL);
        } else {
            this.terrainBuffer.clear();
        }

        if (this.backgroundBuffer == null) {
            this.backgroundBuffer = ByteBuffer.allocateDirect(this.getWidth() * this.getHeight() * BYTES_PER_PIXEL);
        } else {
            this.backgroundBuffer.clear();
        }

        AbstractWorldGenerator generator = new StandardWorldGenerator(this.getWidth(), this.getHeight());

        boolean[][] terrainData = generator.generate();
        this.paintTerrainTexture(generator, terrainData);
        this.createEffects();
        this.createTerrainTexture();
        this.createBackgroundTexture();
    }

    private int getR(int x, int y) {
        this.terrainBuffer.position(this.getBufferPosition(x, y));
        byte r = this.terrainBuffer.get();
        int result = (int) r;
        if (r < 0) {
            r = (byte) (255 - r);
            result = (int) (255 - r);
        }
        this.terrainBuffer.position(0);
        return result;
    }

    private int getG(int x, int y) {
        this.terrainBuffer.position(this.getBufferPosition(x, y));
        this.terrainBuffer.get();
        byte g = this.terrainBuffer.get();
        int result = (int) g;
        if (g < 0) {
            g = (byte) (255 - g);
            result = (int) (255 - g);
        }
        this.terrainBuffer.position(0);
        return result;
    }

    private int getB(int x, int y) {
        this.terrainBuffer.position(this.getBufferPosition(x, y));
        this.terrainBuffer.get();
        this.terrainBuffer.get();
        byte b = this.terrainBuffer.get();
        int result = (int) b;
        if (b < 0) {
            b = (byte) (255 - b);
            result = (int) (255 - b);
        }
        this.terrainBuffer.position(0);
        return result;
    }

    private void paintTerrainTexture(AbstractWorldGenerator generator, boolean[][] terrainData) {
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                this.terrainBuffer.position(this.getBufferPosition(x, y));
                if (terrainData[x][y]) {
                    this.terrainBuffer.put((byte) this.terrainTexture.getR(x + (int) generator.getTerrainSettings().getOffsetX(), y + (int) generator.getTerrainSettings().getOffsetY()));
                    this.terrainBuffer.put((byte) this.terrainTexture.getG(x + (int) generator.getTerrainSettings().getOffsetX(), y + (int) generator.getTerrainSettings().getOffsetY()));
                    this.terrainBuffer.put((byte) this.terrainTexture.getB(x + (int) generator.getTerrainSettings().getOffsetX(), y + (int) generator.getTerrainSettings().getOffsetY()));
                    this.terrainBuffer.put((byte) 255);
                } else {
                    this.terrainBuffer.position(this.getBufferPosition(x, y));
                    this.terrainBuffer.put((byte) 0);
                    this.terrainBuffer.put((byte) 0);
                    this.terrainBuffer.put((byte) 0);
                    this.terrainBuffer.put((byte) 0);
                }
                this.terrainBuffer.position(0);
            }
        }
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
                int r = this.getR(x - offX, y);
                int g = this.getG(x - offX, y);
                int b = this.getB(x - offX, y);
                float alpha = 0.8f - ((float) offX * 0.08f);
                r = this.getBlendedValue(r, 10 + offX * 10, alpha);
                g = this.getBlendedValue(g, 10 + offX * 10, alpha);
                b = this.getBlendedValue(b, 10 + offX * 10, alpha);
                this.terrainBuffer.position(this.getBufferPosition(x - offX, y));
                this.terrainBuffer.put((byte) r);
                this.terrainBuffer.put((byte) g);
                this.terrainBuffer.put((byte) b);
                this.terrainBuffer.position(0);
            }
        }

        // 3D-Effect LEFT
        for (int i = 0; i < xList3DLeft.size(); i++) {
            int x = xList3DLeft.get(i);
            int y = yList3DLeft.get(i);
            for (int offX = 0; offX < 8; offX++) {
                int r = this.getR(x + offX, y);
                int g = this.getG(x + offX, y);
                int b = this.getB(x + offX, y);
                float alpha = 0.8f - ((float) offX * 0.1f);
                r = this.getBlendedValue(r, 55 + offX * 10, alpha);
                g = this.getBlendedValue(g, 55 + offX * 10, alpha);
                b = this.getBlendedValue(b, 55 + offX * 10, alpha);
                this.terrainBuffer.position(this.getBufferPosition(x + offX, y));
                this.terrainBuffer.put((byte) r);
                this.terrainBuffer.put((byte) g);
                this.terrainBuffer.put((byte) b);
                this.terrainBuffer.position(0);
            }
        }

        // grass
        for (int i = 0; i < xListGrass.size(); i++) {
            int x = xListGrass.get(i);
            int y = yListGrass.get(i);
            for (int offY = 0; offY < this.grassTexture.getHeight(); offY++) {
                if (!this.grassTexture.isFuchsia(x, offY)) {
                    this.terrainBuffer.position(this.getBufferPosition(x, y + offY - 8));
                    this.terrainBuffer.put((byte) this.grassTexture.getR(x, offY));
                    this.terrainBuffer.put((byte) this.grassTexture.getG(x, offY));
                    this.terrainBuffer.put((byte) this.grassTexture.getB(x, offY));
                    this.terrainBuffer.put((byte) 255);
                    this.terrainBuffer.position(0);
                }
            }
        }
    }

    private void createTerrainTexture() {
        this.terrainTextureID = glGenTextures(); // Generate texture ID
        glBindTexture(GL_TEXTURE_2D, this.terrainTextureID); // Bind texture ID

        // Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Send texel data to OpenGL
        this.terrainBuffer.position(0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.getWidth(), this.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, this.terrainBuffer);
    }

    private void createBackgroundTexture() {
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                this.terrainBuffer.position(this.getBufferPosition(x, y));
                setBackground(x, y, TerrainType.byRGBA(this.terrainBuffer.get(), this.terrainBuffer.get(), this.terrainBuffer.get(), this.terrainBuffer.get()).isSolid());
                this.terrainBuffer.position(0);
            }
        }

        this.backgroundTextureID = glGenTextures(); // Generate texture ID
        glBindTexture(GL_TEXTURE_2D, this.backgroundTextureID); // Bind texture
                                                                // ID
        // Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Send texel data to OpenGL
        this.backgroundBuffer.position(0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.getWidth(), this.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, this.backgroundBuffer);
    }

    public void updateTexture(int x, int y, int width, int height, int textureID, ByteBuffer buffer) {
        buffer.position(0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width - 1, height - 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        buffer.position(0);
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
                this.terrainBuffer.position(this.getBufferPosition(x, y));
                buffer.put(this.terrainBuffer.get());
                buffer.put(this.terrainBuffer.get());
                buffer.put(this.terrainBuffer.get());
                buffer.put(this.terrainBuffer.get());
                this.terrainBuffer.position(0);
            }
        }
        buffer.position(0);
        if (updateTexture) {
            this.updateTexture(minX, minY, dX, dY, this.terrainTextureID, buffer);
        }

        long duration = System.nanoTime() - start;
        float d = duration / 1000000f;
        System.out.println("Terrain: " + d);
        return buffer;
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
                        this.terrainBuffer.position(this.getBufferPosition(mX, mY));
                        this.terrainBuffer.put((byte) this.terrainTexture.getR(mX, mY));
                        this.terrainBuffer.put((byte) this.terrainTexture.getG(mX, mY));
                        this.terrainBuffer.put((byte) this.terrainTexture.getB(mX, mY));
                        this.terrainBuffer.put((byte) 255);
                        this.terrainBuffer.position(0);
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
                                this.terrainBuffer.position(this.getBufferPosition(midX + x, midY + y + offY));
                                this.terrainBuffer.put((byte) 0);
                                this.terrainBuffer.put((byte) (128 / (offY / 3 + 1) + 32));
                                this.terrainBuffer.put((byte) 0);
                                this.terrainBuffer.put((byte) 255);
                                this.terrainBuffer.position(0);
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
            int oldPosition = this.terrainBuffer.position();
            this.terrainBuffer.position(this.getBufferPosition(x, y));
            byte r = this.terrainBuffer.get();
            byte g = this.terrainBuffer.get();
            byte b = this.terrainBuffer.get();
            byte a = this.terrainBuffer.get();
            this.terrainBuffer.position(oldPosition);
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

            this.terrainBuffer.position(this.getBufferPosition(x, y));
            this.terrainBuffer.put((byte) terrainType.getR());
            this.terrainBuffer.put((byte) terrainType.getG());
            this.terrainBuffer.put((byte) terrainType.getB());
            this.terrainBuffer.put((byte) terrainType.getA());
            this.terrainBuffer.position(0);
        }
    }

    private void setBackground(int x, int y, boolean collidable) {
        if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
            this.backgroundBuffer.position(this.getBufferPosition(x, y));
            if (collidable) {
                this.backgroundBuffer.put((byte) this.backgroundTexture.getR(x, y));
                this.backgroundBuffer.put((byte) this.backgroundTexture.getG(x, y));
                this.backgroundBuffer.put((byte) this.backgroundTexture.getB(x, y));
                this.backgroundBuffer.put((byte) 255);
            } else {
                this.backgroundBuffer.put((byte) 0);
                this.backgroundBuffer.put((byte) 0);
                this.backgroundBuffer.put((byte) 0);
                this.backgroundBuffer.put((byte) 0);
            }
            this.backgroundBuffer.position(0);
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

    public static int getBufferPosition(int x, int y, int maxWidth, int maxHeight) {
        if (x >= 0 && y >= 0 && x < maxWidth && y < maxHeight) {
            return (y * maxWidth + x) * BYTES_PER_PIXEL;
        } else {
            return 0;
        }
    }

    @Override
    public void render() {
        glColor4f(1, 1, 1, 1);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);

        glBindTexture(GL_TEXTURE_2D, this.backgroundTextureID);
        this.renderTexture();

        glBindTexture(GL_TEXTURE_2D, this.terrainTextureID);
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
