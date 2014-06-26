package de.gemo.game.terrain.utils;

import java.nio.*;

import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;

public class BufferedTexture {

    private int textureID = -1;
    private final ByteBuffer buffer;
    private final int width, height;

    public BufferedTexture(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = ByteBuffer.allocateDirect(this.width * this.height * CONSTANTS.BYTES_PER_PIXEL);
        this.createTexture();
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
        this.buffer.position(0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.getWidth(), this.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, this.buffer);
    }

    public boolean update() {
        return this.updatePartial(0, 0, this.getWidth(), this.getHeight());
    }

    public boolean updatePartial(int x, int y, int width, int height) {
        // some vars
        int minX = Math.max(0, x);
        int minY = Math.max(0, y);
        int maxX = Math.min(this.getWidth(), x + width);
        int maxY = Math.min(this.getHeight(), y + height);
        int dX = maxX - minX + 1;
        int dY = maxY - minY + 1;

        // we need at least 1 pixel to update...
        if (dX < 1 || dY < 1) {
            // return false
            return false;
        }

        // old bufferposition
        int oldBufferPosition = this.buffer.position();

        // iterate over pixels...
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(dX * dY * CONSTANTS.BYTES_PER_PIXEL);
        for (y = minY; y < maxY; y++) {
            for (x = minX; x < maxX; x++) {
                int newBufferPosition = this.getBufferPosition(x, y);
                if (newBufferPosition < 0) {
                    continue;
                }
                this.buffer.position(newBufferPosition);
                newBuffer.put(this.buffer.get());
                newBuffer.put(this.buffer.get());
                newBuffer.put(this.buffer.get());
                newBuffer.put(this.buffer.get());
            }
        }

        // reset bufferpositions
        this.buffer.position(oldBufferPosition);
        newBuffer.position(0);

        // update texture
        this.bind();
        glTexSubImage2D(GL_TEXTURE_2D, 0, minX, minY, dX - 1, dY - 1, GL_RGBA, GL_UNSIGNED_BYTE, newBuffer);
        this.unbind();

        // return true
        return true;
    }

    public int getR(int x, int y) {
        int newBufferPosition = this.getBufferPosition(x, y);
        if (newBufferPosition < 0) {
            return 0;
        }
        this.buffer.position(newBufferPosition);
        byte r = this.buffer.get();
        int result = (int) r;
        if (r < 0) {
            r = (byte) (255 - r);
            result = (int) (255 - r);
        }
        this.buffer.position(0);
        return result;
    }

    public int getG(int x, int y) {
        int newBufferPosition = this.getBufferPosition(x, y);
        if (newBufferPosition < 0) {
            return 0;
        }
        this.buffer.position(newBufferPosition);
        this.buffer.get();
        byte g = this.buffer.get();
        int result = (int) g;
        if (g < 0) {
            g = (byte) (255 - g);
            result = (int) (255 - g);
        }
        this.buffer.position(0);
        return result;
    }

    public int getB(int x, int y) {
        int newBufferPosition = this.getBufferPosition(x, y);
        if (newBufferPosition < 0) {
            return 0;
        }
        this.buffer.position(newBufferPosition);
        this.buffer.get();
        this.buffer.get();
        byte b = this.buffer.get();
        int result = (int) b;
        if (b < 0) {
            b = (byte) (255 - b);
            result = (int) (255 - b);
        }
        this.buffer.position(0);
        return result;
    }

    public int getA(int x, int y) {
        int newBufferPosition = this.getBufferPosition(x, y);
        if (newBufferPosition < 0) {
            return 0;
        }
        this.buffer.position(newBufferPosition);
        this.buffer.get();
        this.buffer.get();
        this.buffer.get();
        byte a = this.buffer.get();
        int result = (int) a;
        if (a < 0) {
            a = (byte) (255 - a);
            result = (int) (255 - a);
        }
        this.buffer.position(0);
        return result;
    }

    public boolean clearPixel(int x, int y) {
        return this.setPixel(x, y, 0, 0, 0, 0);
    }

    public boolean setPixel(int x, int y, int r, int g, int b, int a) {
        // get the new bufferposition
        int newBufferPos = this.getBufferPosition(x, y);

        // if invalid => return false;
        if (newBufferPos < 0) {
            return false;
        }
        // save old bufferposition
        int oldBufferPos = this.buffer.position();

        // advance bufferposition
        this.buffer.position(newBufferPos);

        // put values
        this.buffer.put((byte) r);
        this.buffer.put((byte) g);
        this.buffer.put((byte) b);
        this.buffer.put((byte) a);

        // back to old bufferposition
        this.buffer.position(oldBufferPos);

        // return true
        return true;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, this.textureID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    protected int getBufferPosition(int x, int y) {
        if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
            return (y * this.getWidth() + x) * CONSTANTS.BYTES_PER_PIXEL;
        } else {
            return -1;
        }
    }
}
