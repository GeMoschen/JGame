package de.gemo.game.terrain.world;

import de.gemo.game.terrain.entities.IRenderObject;
import de.gemo.game.terrain.utils.BufferedTexture;
import de.gemo.game.terrain.utils.TerrainType;
import de.gemo.game.terrain.utils.TexData;
import de.gemo.game.terrain.world.generators.AbstractWorldGenerator;
import de.gemo.game.terrain.world.generators.StandardWorldGenerator;
import de.gemo.gameengine.units.Vector2f;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class World implements IRenderObject {

    private int _width, _height;

    private boolean[][] _terrainData;
    private TexData _texTerrain, _texGrass, _texBackground;

    private int _craterR = 152, _craterG = 113, _craterB = 82;
    private BufferedTexture _terrainTexture;
    private AbstractWorldGenerator _worldGenerator;

    public World(int width, int height) {
        _width = width;
        _height = height;
        createWorld(width, height);
    }

    public void createWorld(int width, int height) {
        try {
            _texTerrain = new TexData("resources/terrain/wood_terrain.jpg");
            _texBackground = new TexData("resources/terrainBackgrounds/background_wood.jpg");
            _texGrass = new TexData("resources/grasses/wood.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        _worldGenerator = new StandardWorldGenerator(getWidth(), getHeight());
        _terrainTexture = new BufferedTexture(getWidth(), getHeight());
        _terrainData = _worldGenerator.generate();
        paintTerrain();
        createFX();
        createBridge();
        _terrainTexture.update();
    }

    private void createBridge() {
        BridgeCreator.generate(_terrainData, _terrainTexture, _width / 2, 800);
    }

    private void paintTerrain() {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (_terrainData[x][y]) {
                    _terrainTexture.setPixel(x, y, _texTerrain.getR(x, y), _texTerrain.getG(x, y), _texTerrain.getB(x, y), 255);
                } else {
                    _terrainTexture.clearPixel(x, y);
                }
            }
        }
    }

    private void createFX() {
        List<Point> grassList = new ArrayList<Point>();
        List<Point> left3D = new ArrayList<Point>();
        List<Point> right3D = new ArrayList<Point>();

        for (int y = getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < getWidth(); x++) {
                boolean placeGrass_1 = !isPixelSolid(x, y - 1) && isPixelSolid(x, y);
                if (placeGrass_1) {
                    grassList.add(new Point(x, y));
                }

                boolean threeDEffectRight = (isPixelSolid(x, y) && !isPixelSolid(x + 1, y));
                if (threeDEffectRight) {
                    right3D.add(new Point(x, y));
                }

                boolean threeDEffectLeft = (isPixelSolid(x, y) && !isPixelSolid(x - 1, y));
                if (threeDEffectLeft) {
                    left3D.add(new Point(x, y));
                }
            }
        }

        // 3D-Effect RIGHT
        for (Point point : right3D) {
            int x = point.x;
            int y = point.y;
            for (int offX = 0; offX < 11; offX++) {
                if (x - offX < 0 || x - offX >= _terrainData.length || !_terrainData[x - offX][y]) {
                    continue;
                }
                int r = _terrainTexture.getR(x - offX, y);
                int g = _terrainTexture.getG(x - offX, y);
                int b = _terrainTexture.getB(x - offX, y);
                float alpha = 0.8f - ((float) offX * 0.08f);
                r = getBlendedValue(r, 10 + offX * 10, alpha);
                g = getBlendedValue(g, 10 + offX * 10, alpha);
                b = getBlendedValue(b, 10 + offX * 10, alpha);
                _terrainTexture.setPixel(x - offX, y, r, g, b, 255);
            }
        }

        // 3D-Effect LEFT
        for (Point point : left3D) {
            int x = point.x;
            int y = point.y;
            for (int offX = 0; offX < 6; offX++) {
                if (x + offX < 0 || x + offX >= _terrainData.length || !_terrainData[x + offX][y]) {
                    continue;
                }
                int r = _terrainTexture.getR(x + offX, y);
                int g = _terrainTexture.getG(x + offX, y);
                int b = _terrainTexture.getB(x + offX, y);
                float alpha = 0.8f - ((float) offX * 0.1f);
                r = getBlendedValue(r, 55 + offX * 10, alpha);
                g = getBlendedValue(g, 55 + offX * 10, alpha);
                b = getBlendedValue(b, 55 + offX * 10, alpha);
                _terrainTexture.setPixel(x + offX, y, r, g, b, 255);
            }
        }

        // grass
        for (Point point : grassList) {
            int x = point.x;
            int y = point.y;
            for (int offY = 0; offY < _texGrass.getHeight(); offY++) {
                if (!_texGrass.isFuchsia(x, offY)) {
                    int newY = y + offY - 8;
                    if (newY < 0 || newY >= _height) {
                        continue;
                    }
                    _terrainData[x][y + offY - 8] = true;
                    _terrainTexture.setPixel(x, y + offY - 8, _texGrass.getR(x, offY), _texGrass.getG(x, offY), _texGrass.getB(x, offY), 255);
                }
            }
        }
    }

    public void explode(float midX, float midY, int radius) {
        explode(midX, midY, radius, 0);
    }

    public void explode(int midX, int midY, int radius) {
        explode(midX, midY, radius, 0);
    }

    public void explode(float midX, float midY, int radius, int airRadius) {
        explode((int) midX, (int) midY, radius, airRadius);
    }

    public void explode(int midX, int midY, int radius, int airRadius) {
        // crater
        fillCircle(midX, midY, radius, 7, TerrainType.CRATER);

        // background
        fillCircle(midX, midY, radius - 7, radius - 7, TerrainType.BACKGROUND);

        // air
        if (airRadius > 0) {
            fillCircle(midX, midY, airRadius, airRadius, TerrainType.AIR);
        }

        // updatePosition texture
        int leftX = midX - radius - 1;
        int topY = midY - radius - 1;
        _terrainTexture.updatePartial(leftX, topY, radius * 2 + 2, radius * 2 + 2);
    }

    public boolean isOutOfEntityBounds(Vector2f vector) {
        return isOutOfEntityBounds(vector.getX(), vector.getY());
    }

    public boolean isOutOfEntityBounds(float x, float y) {
        return x < -50 || x > _width + 50 || y > getHeight() + 50;
    }

    private void fillCircle(int midX, int midY, int radius, int wallThickness, TerrainType terrainType) {
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
                    int newX = midX + x;
                    int newY = midY + y;

                    if (newX < 0 || newY < 0 || newX >= _width || newY >= _height) {
                        continue;
                    }

                    if (terrainType.equals(TerrainType.AIR)) {
                        // air
                        _terrainTexture.clearPixel(newX, newY);
                        _terrainData[newX][newY] = false;
                    } else if (terrainType.equals(TerrainType.CRATER)) {
                        // crater
                        if (_terrainData[newX][newY]) {
                            _terrainTexture.setPixel(newX, newY, _craterR, _craterG, _craterB, 255);
                        }
                    } else if (terrainType.equals(TerrainType.BACKGROUND)) {
                        // background
                        if (_terrainData[newX][newY]) {
                            _terrainTexture.setPixel(newX, newY, _texBackground.getR(newX, newY), _texBackground.getG(newX, newY), _texBackground.getB(newX, newY), 255);
                            _terrainData[newX][newY] = false;
                        }
                    }

                }
            }
        }
    }

    public boolean isPixelSolid(int x, int y) {
        return isPixelSolid(x, y, true);
    }

    public boolean isUnderFreeSky(int x, int y) {
        for (int thisY = y - 1; thisY >= 0; thisY--) {
            if (isPixelSolid(x, thisY)) {
                return false;
            }
        }
        return true;
    }

    public boolean isPixelSolid(int x, int y, boolean defaultValue) {
        if (x >= 0 && y >= 0 && x < getWidth() && y < getHeight()) {
            return _terrainData[x][y];
        } else {
            return defaultValue;
        }
    }

    public Vector2f getNormal(int x, int y) {
        Vector2f average = new Vector2f();
        int normalSize = 5;
        for (int i = -normalSize; i <= normalSize; i++) {
            for (int j = -normalSize; j <= normalSize; j++) {
                if (isPixelSolid(x + i, y + j)) {
                    Vector2f.sub(average, new Vector2f(i, j), average);
                }
            }
        }
        return Vector2f.normalize(average);
    }

    private int getBlendedValue(int background, int foreground, float alphaForeground) {
        float floatBackground = (float) background / 255f;
        float alphaBackground = 1f;
        float floatForeground = (float) foreground / 255f;
        float result = floatForeground * alphaForeground + floatBackground * alphaBackground * (1f - alphaForeground);
        return (int) (result * 255);
    }

    @Override
    public void render() {
        glColor4f(1, 1, 1, 1);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        renderTexture();
    }

    private void renderTexture() {
        _terrainTexture.bind();
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex3i(0, 0, -1);

            glTexCoord2f(1, 0);
            glVertex3i(getWidth(), 0, -1);

            glTexCoord2f(1, 1);
            glVertex3i(getWidth(), getHeight(), -1);

            glTexCoord2f(0, 1);
            glVertex3i(0, getHeight(), -1);
        }
        glEnd();
        _terrainTexture.unbind();
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

}
