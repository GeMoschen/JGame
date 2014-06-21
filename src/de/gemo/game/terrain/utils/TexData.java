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
                this.rgb[x][y] = new Color(image.getRGB(x, y));
            }
        }
    }

    public Color getColor(int x, int y) {
        x = x % rgb.length;
        y = y % rgb[0].length;
        return this.rgb[x][y];
    }

    public byte getR(int x, int y) {
        x = x % rgb.length;
        y = y % rgb[0].length;
        return (byte) this.rgb[x][y].getRed();
    }

    public byte getG(int x, int y) {
        x = x % rgb.length;
        y = y % rgb[0].length;
        return (byte) this.rgb[x][y].getGreen();
    }

    public byte getB(int x, int y) {
        x = x % rgb.length;
        y = y % rgb[0].length;
        return (byte) this.rgb[x][y].getBlue();
    }

    public byte getA(int x, int y) {
        x = x % rgb.length;
        y = y % rgb[0].length;
        return (byte) this.rgb[x][y].getAlpha();
    }
}
