package de.gemo.game.terrain.utils;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

public class TexData {

    private Color[][] rgb;

    public TexData(String fileName) throws IOException {
        BufferedImage image = ImageIO.read(new File(fileName));
        this.loadData(image);
    }

    private void loadData(BufferedImage image) {
        this.rgb = new Color[image.getWidth()][image.getHeight()];
        for (int y = 0; y < this.rgb[0].length; y++) {
            for (int x = 0; x < this.rgb.length; x++) {
                this.rgb[x][y] = new Color(image.getRGB(x, y), true);
            }
        }
    }

    public Color getRGB(int x, int y) {
        x = x % rgb.length;
        y = y % rgb[0].length;
        return this.rgb[x][y];
    }

    public int getR(int x, int y) {
        x = x % rgb.length;
        y = y % rgb[0].length;
        return this.rgb[x][y].getRed();
    }

    public int getG(int x, int y) {
        x = x % rgb.length;
        y = y % rgb[0].length;
        return this.rgb[x][y].getGreen();
    }

    public int getB(int x, int y) {
        x = x % rgb.length;
        y = y % rgb[0].length;
        return this.rgb[x][y].getBlue();
    }

    public boolean isFuchsia(final int x, final int y) {
        return this.getR(x, y) == 255 && this.getG(x, y) == 0 && this.getB(x, y) == 255;
    }

    public int getWidth() {
        return this.rgb.length;
    }

    public int getHeight() {
        return this.rgb[0].length;
    }
}
