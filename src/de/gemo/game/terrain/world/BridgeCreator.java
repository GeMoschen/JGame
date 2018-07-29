package de.gemo.game.terrain.world;

import de.gemo.game.terrain.utils.BufferedTexture;
import de.gemo.game.terrain.utils.TexData;

import java.io.IOException;

public class BridgeCreator {
    
    private static TexData TEXTURE;

    static {
        try {
            TEXTURE = new TexData("resources/world/bridge_01.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generate(boolean[][] terrainData, BufferedTexture terrainTexture, int searchX, int maxDistance) {
        for (int y = TEXTURE.getHeight() * 5; y < terrainData[0].length - TEXTURE.getHeight() * 5; y += TEXTURE.getHeight() * 1) {
            if (!terrainData[searchX][y]) {
                int foundLeft = -1;
                for (int x = searchX - 1; x > searchX - maxDistance / 2; x--) {
                    if (x < 20) {
                        break;
                    }
                    if (terrainData[x][y]) {
                        if (terrainData[x][y + TEXTURE.getHeight()] && terrainData[x][y + 2 * TEXTURE.getHeight()] && terrainData[x][y - TEXTURE.getHeight()]) {
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
                        if (terrainData[x][y + TEXTURE.getHeight()] && terrainData[x][y + 2 * TEXTURE.getHeight()] && terrainData[x][y - TEXTURE.getHeight()]) {
                            foundRight = x - 1;
                            break;
                        }
                    }
                }

                if (foundLeft > -1 && foundRight > -1) {
                    for (int tX = foundLeft - 10; tX <= foundRight + 10; tX++) {
                        for (int tY = 0; tY < TEXTURE.getHeight(); tY++) {
                            if (!TEXTURE.isFuchsia(tX, tY)) {
                                if (!terrainData[tX][y + tY]) {
                                    terrainTexture.setPixel(tX, y + tY, TEXTURE.getR(tX, tY), TEXTURE.getG(tX, tY), TEXTURE.getB(tX, tY), 255);
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
