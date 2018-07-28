package de.gemo.game.terrain.world;

import java.io.*;

import de.gemo.game.terrain.utils.*;

public class BridgeCreator {
    private static TexData texture;

    static {
        try {
            texture = new TexData("resources/world/bridge_01.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generate(boolean[][] terrainData, BufferedTexture terrainTexture, int searchX, int maxDistance) {
        for (int y = texture.getHeight() * 5; y < terrainData[0].length - texture.getHeight() * 5; y += texture.getHeight() * 1) {
            if (!terrainData[searchX][y]) {
                int foundLeft = -1;
                for (int x = searchX - 1; x > searchX - maxDistance / 2; x--) {
                    if (x < 20) {
                        break;
                    }
                    if (terrainData[x][y]) {
                        if (terrainData[x][y + texture.getHeight()] && terrainData[x][y + 2 * texture.getHeight()] && terrainData[x][y - texture.getHeight()]) {
                            foundLeft = x + 1;
                            break;
                        }
                    }
                }

                int foundRight = -1;
                for (int x = searchX + 1; x < searchX + maxDistance / 2; x++) {
                    if (x >= terrainData.length - 20) {
                        break;
                    }
                    if (terrainData[x][y]) {
                        if (terrainData[x][y + texture.getHeight()] && terrainData[x][y + 2 * texture.getHeight()] && terrainData[x][y - texture.getHeight()]) {
                            foundRight = x - 1;
                            break;
                        }
                    }
                }

                if (foundLeft > -1 && foundRight > -1) {
                    for (int tX = foundLeft - 10; tX <= foundRight + 10; tX++) {
                        for (int tY = 0; tY < texture.getHeight(); tY++) {
                            if (!texture.isFuchsia(tX, tY)) {
                                if (!terrainData[tX][y + tY]) {
                                    terrainTexture.setPixel(tX, y + tY, texture.getR(tX, tY), texture.getG(tX, tY), texture.getB(tX, tY), 255);
                                    terrainData[tX][y + tY] = true;
                                }
                            }
                        }
                    }
                    terrainTexture.update();
                    return;
                }
            }
        }

    }
}
