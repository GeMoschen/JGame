package de.gemo.game.terrain.utils;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

public class TexData {

    private Color[][] _pixels;

    public TexData(String fileName) throws IOException {
        BufferedImage image = ImageIO.read(new File(fileName));
        loadData(image);
    }

    private void loadData(BufferedImage image) {
        _pixels = new Color[image.getWidth()][image.getHeight()];
        for (int y = 0; y < _pixels[0].length; y++) {
            for (int x = 0; x < _pixels.length; x++) {
                _pixels[x][y] = new Color(image.getRGB(x, y), true);
            }
        }
    }

    public Color getRGB(int x, int y) {
        x = x % _pixels.length;
        y = y % _pixels[0].length;
        return _pixels[x][y];
    }

    public int getR(int x, int y) {
        x = x % _pixels.length;
        y = y % _pixels[0].length;
        return _pixels[x][y].getRed();
    }

    public int getG(int x, int y) {
        x = x % _pixels.length;
        y = y % _pixels[0].length;
        return _pixels[x][y].getGreen();
    }

    public int getB(int x, int y) {
        x = x % _pixels.length;
        y = y % _pixels[0].length;
        return _pixels[x][y].getBlue();
    }

    public boolean isFuchsia(final int x, final int y) {
        return getR(x, y) == 255 && getG(x, y) == 0 && getB(x, y) == 255;
    }

    public int getWidth() {
        return _pixels.length;
    }

    public int getHeight() {
        return _pixels[0].length;
    }
}
