package de.gemo.game.skyroads;

import static org.lwjgl.opengl.GL11.*;

public class Level {

    private Cube[][] ground;
    private int width, height;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;
        this.ground = new Cube[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.ground[x][y] = null;
            }
        }
    }

    public boolean setCube(int x, int y, Cube cube) {
        if (x > -1 && x < this.width && y > -1 && y < this.height) {
            this.ground[x][y] = cube;
            return true;
        }
        return false;
    }

    public Cube getCube(int x, int y) {
        if (x > -1 && x < this.width && y > -1 && y < this.height) {
            return this.ground[x][y];
        }
        return null;
    }

    public boolean hasCube(int x, int y) {
        return (x > -1 && x < this.width && y > -1 && y < this.height && this.ground[x][y] != null);
    }

    public void render() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (this.ground[x][y] != null) {
                    glPushMatrix();
                    {
                        glTranslatef(x * 4, 0, -y * 10);
                        this.ground[x][y].render();
                    }
                    glPopMatrix();
                }
            }
        }
    }
}
