package de.gemo.game.terrain.core;

import java.nio.*;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.keyboard.*;
import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.ARBTextureRectangle.*;
import static org.lwjgl.opengl.GL11.*;

public class TerrainCore extends GameEngine {

    private int[][] terrain;
    private ByteBuffer buffer;
    private int texID = -1;

    private Vector2f offset = new Vector2f();
    private Player player;

    public TerrainCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    @Override
    protected void createManager() {
        this.createTerrain(2 * 1024, 768);
        this.player = new Player(500, 100);
    }

    private void createTerrain(int width, int height) {
        this.terrain = new int[width][height];

        if (this.buffer == null) {
            this.buffer = ByteBuffer.allocateDirect(terrain.length * terrain[0].length * BYTES_PER_PIXEL);
        } else {
            this.buffer.clear();
        }

        this.createPerlinWorld();
        this.bakeTexture();
    }

    private void bakeTexture() {
        this.texID = updateTexture(this.texID, this.buffer);
    }

    private static final int BYTES_PER_PIXEL = 4;

    public int updateTexture(int currentID, ByteBuffer buffer) {
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
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.terrain.length, this.terrain[0].length, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } else {
            glBindTexture(GL_TEXTURE_2D, currentID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.terrain.length, this.terrain[0].length, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }

        long d = System.nanoTime() - start;
        float dur = d / 1000000f;
        System.out.println("Update Texture: " + dur);
        return currentID;
    }

    private void createPerlinWorld() {
        float freq = 0.006f;

        float offX = (float) (Math.random() * (Math.random() * 50000));
        float offY = (float) (Math.random() * (Math.random() * 50000));

        float cutOff = 0.4f;

        for (int x = 0; x < terrain.length; x++) {
            for (int wrongY = 0; wrongY < terrain[0].length; wrongY++) {
                int y = terrain[0].length - wrongY - 1;
                double noise = SimplexNoise.noise(x * freq + offX, y * freq + offY);
                double addY = ((double) (y - 300) / (double) terrain[0].length);
                noise += 6d * addY;

                // left
                double dX = (double) x / 512d;
                if (dX < 1) {
                    noise *= dX;
                }

                // right
                dX = Math.abs(x - terrain.length) / 512d;
                if (dX < 1) {
                    noise *= dX;
                }

                this.terrain[x][y] = noise >= cutOff * (1d - ((double) (y) / (double) terrain[0].length) * 0.75) ? 1 : 0;
                this.setPixelNoCheck(x, y, this.terrain[x][y]);
            }
        }
    }

    @Override
    protected void renderGame2D() {
        glEnable(GL_BLEND);
        glDisable(GL_LIGHTING);
        glClearColor(0f, 0f, 0f, 1f);
        glDisable(GL_TEXTURE_2D);

        glDisable(GL_TEXTURE_RECTANGLE_ARB);
        glEnable(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, this.texID);

        glPushMatrix();
        {
            glTranslatef(offset.getX(), offset.getY(), 0);
            glColor4f(1, 1, 1, 1);
            glBegin(GL_QUADS);
            {
                glTexCoord2f(0, 0);
                glVertex2i(0, 0);

                glTexCoord2f(1, 0);
                glVertex2i(this.terrain.length, 0);

                glTexCoord2f(1, 1);
                glVertex2i(this.terrain.length, this.terrain[0].length);

                glTexCoord2f(0, 1);
                glVertex2i(0, this.terrain[0].length);
            }
            glEnd();
        }
        glPopMatrix();

        this.player.render();
    }

    @Override
    protected void updateGame(int delta) {
        boolean left = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_LEFT);
        boolean right = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_RIGHT);
        boolean up = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_UP);
        boolean down = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_DOWN);
        boolean space = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_SPACE);
        this.player.setMovement(left, right, up, down, space);
        this.player.update(delta);
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_SPACE) {
            this.player.jump();
        } else {
            super.onKeyPressed(event);
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_F12) {
            this.createTerrain(terrain.length, terrain[0].length);
        } else {
            super.onKeyReleased(event);
        }
    }

    @Override
    public void onMouseMove(boolean handled, MouseMoveEvent event) {
        if (MouseManager.INSTANCE.isButtonDown(MouseButton.MIDDLE.getID())) {
            offset.move(event.getDifX(), event.getDifY());
        }
    }

    @Override
    public void onMouseUp(boolean handled, MouseReleaseEvent event) {
        if (event.getButton().equals(MouseButton.RIGHT)) {
            this.filledCircle(event.getX() - (int) offset.getX(), event.getY() - (int) offset.getY(), 30, 1, true);
            this.bakeTexture();
        } else if (event.getButton().equals(MouseButton.LEFT)) {
            this.filledCircle(event.getX() - (int) offset.getX(), event.getY() - (int) offset.getY(), 35, 5, 2, false);
            this.filledCircle(event.getX() - (int) offset.getX(), event.getY() - (int) offset.getY(), 30, 0, false);
            this.bakeTexture();
        }
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
        if (x >= 0 && y >= 0 && x < this.terrain.length && y < this.terrain[0].length) {
            if (this.terrain[x][y] == terrainType || (!replaceAir && this.terrain[x][y] == 0)) {
                return;
            }

            buffer.position(this.getBufferPosition(x, y));
            if (terrainType == 1) {
                buffer.put((byte) 255);
                buffer.put((byte) 255);
                buffer.put((byte) 255);
                buffer.put((byte) 255);
            } else if (terrainType == 2) {
                buffer.put((byte) 127);
                buffer.put((byte) 127);
                buffer.put((byte) 127);
                buffer.put((byte) 255);
            } else if (terrainType == 0) {
                buffer.put((byte) 0);
                buffer.put((byte) 0);
                buffer.put((byte) 0);
                buffer.put((byte) 0);
            }
            buffer.position(0);
            this.terrain[x][y] = terrainType;
        }
    }

    private void setPixelNoCheck(int x, int y, int terrainType) {
        if (x >= 0 && y >= 0 && x < this.terrain.length && y < this.terrain[0].length) {
            buffer.position(this.getBufferPosition(x, y));
            if (terrainType == 1) {
                buffer.put((byte) 255);
                buffer.put((byte) 255);
                buffer.put((byte) 255);
                buffer.put((byte) 255);
            } else if (terrainType == 2) {
                buffer.put((byte) 127);
                buffer.put((byte) 127);
                buffer.put((byte) 127);
                buffer.put((byte) 255);
            } else if (terrainType == 0) {
                buffer.put((byte) 0);
                buffer.put((byte) 0);
                buffer.put((byte) 0);
                buffer.put((byte) 0);
            }
            buffer.position(0);
        }
    }

    public boolean isPixelSolid(int x, int y) {
        if (x >= 0 && y >= 0 && x < this.terrain.length && y < this.terrain[0].length) {
            return this.terrain[x][y] != 0;
        } else {
            return true;
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
        if (x >= 0 && y >= 0 && x < this.terrain.length && y < this.terrain[0].length) {
            return (y * this.terrain.length + x) * BYTES_PER_PIXEL;
        } else {
            return 0;
        }
    }
}