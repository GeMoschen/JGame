package de.gemo.game.terrain.utils;

import java.nio.*;

import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;

public class BufferedTexture {

    private int _textureId = -1;
    private final ByteBuffer _buffer;
    private final int _width, _height;

    public BufferedTexture(int width, int height) {
        _width = width;
        _height = height;
        _buffer = ByteBuffer.allocateDirect(_width * _height * CONSTANTS.BYTES_PER_PIXEL);
        createTexture();
    }

    private void createTexture() {
        _textureId = glGenTextures(); // Generate texture ID
        glBindTexture(GL_TEXTURE_2D, _textureId); // Bind texture ID

        // Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Send texel data to OpenGL
        _buffer.position(0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, getWidth(), getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, _buffer);
    }

    public boolean update() {
        return updatePartial(0, 0, getWidth(), getHeight());
    }

    public boolean updatePartial(int x, int y, int width, int height) {
        // some vars
        int minX = Math.max(0, x);
        int minY = Math.max(0, y);
        int maxX = Math.min(getWidth(), x + width);
        int maxY = Math.min(getHeight(), y + height);
        int dX = maxX - minX + 1;
        int dY = maxY - minY + 1;

        // we need at least 1 pixel to updatePosition...
        if (dX < 1 || dY < 1) {
            // return false
            return false;
        }

        // old bufferposition
        int oldBufferPosition = _buffer.position();

        // iterate over pixels...
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(dX * dY * CONSTANTS.BYTES_PER_PIXEL);
        for (y = minY; y < maxY; y++) {
            for (x = minX; x < maxX; x++) {
                int newBufferPosition = getBufferPosition(x, y);
                if (newBufferPosition < 0) {
                    continue;
                }
                _buffer.position(newBufferPosition);
                newBuffer.put(_buffer.get());
                newBuffer.put(_buffer.get());
                newBuffer.put(_buffer.get());
                newBuffer.put(_buffer.get());
            }
        }

        // reset bufferpositions
        _buffer.position(oldBufferPosition);
        newBuffer.position(0);

        // updatePosition texture
        bind();
        glTexSubImage2D(GL_TEXTURE_2D, 0, minX, minY, dX - 1, dY - 1, GL_RGBA, GL_UNSIGNED_BYTE, newBuffer);
        unbind();

        // return true
        return true;
    }

    public int getR(int x, int y) {
        int newBufferPosition = getBufferPosition(x, y);
        if (newBufferPosition < 0) {
            return 0;
        }
        _buffer.position(newBufferPosition);
        byte r = _buffer.get();
        int result = (int) r;
        if (r < 0) {
            r = (byte) (255 - r);
            result = (int) (255 - r);
        }
        _buffer.position(0);
        return result;
    }

    public int getG(int x, int y) {
        int newBufferPosition = getBufferPosition(x, y);
        if (newBufferPosition < 0) {
            return 0;
        }
        _buffer.position(newBufferPosition);
        _buffer.get();
        byte g = _buffer.get();
        int result = (int) g;
        if (g < 0) {
            g = (byte) (255 - g);
            result = (int) (255 - g);
        }
        _buffer.position(0);
        return result;
    }

    public int getB(int x, int y) {
        int newBufferPosition = getBufferPosition(x, y);
        if (newBufferPosition < 0) {
            return 0;
        }
        _buffer.position(newBufferPosition);
        _buffer.get();
        _buffer.get();
        byte b = _buffer.get();
        int result = (int) b;
        if (b < 0) {
            b = (byte) (255 - b);
            result = (int) (255 - b);
        }
        _buffer.position(0);
        return result;
    }

    public int getA(int x, int y) {
        int newBufferPosition = getBufferPosition(x, y);
        if (newBufferPosition < 0) {
            return 0;
        }
        _buffer.position(newBufferPosition);
        _buffer.get();
        _buffer.get();
        _buffer.get();
        byte a = _buffer.get();
        int result = (int) a;
        if (a < 0) {
            a = (byte) (255 - a);
            result = (int) (255 - a);
        }
        _buffer.position(0);
        return result;
    }

    public boolean clearPixel(int x, int y) {
        return setPixel(x, y, 0, 0, 0, 0);
    }

    public boolean setPixel(int x, int y, int r, int g, int b, int a) {
        // get the new bufferposition
        int newBufferPos = getBufferPosition(x, y);

        // if invalid => return false;
        if (newBufferPos < 0) {
            return false;
        }
        // save old bufferposition
        int oldBufferPos = _buffer.position();

        // advance bufferposition
        _buffer.position(newBufferPos);

        // put values
        _buffer.put((byte) r);
        _buffer.put((byte) g);
        _buffer.put((byte) b);
        _buffer.put((byte) a);

        // back to old bufferposition
        _buffer.position(oldBufferPos);

        // return true
        return true;
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, _textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    protected int getBufferPosition(int x, int y) {
        if (x >= 0 && y >= 0 && x < getWidth() && y < getHeight()) {
            return (y * getWidth() + x) * CONSTANTS.BYTES_PER_PIXEL;
        } else {
            return -1;
        }
    }
}
