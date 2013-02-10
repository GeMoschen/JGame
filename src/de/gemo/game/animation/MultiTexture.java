package de.gemo.game.animation;

import java.util.ArrayList;
import java.util.List;

public class MultiTexture {

    private final ArrayList<SingleTexture> textures;
    private boolean validIndex = false;
    private int index = -1;

    private float width, height;

    public MultiTexture(float width, float height) {
        textures = new ArrayList<SingleTexture>();
        this.width = width;
        this.height = height;
    }

    public MultiTexture(float width, float height, SingleTexture... singleTextures) {
        this(width, height);
        this.addTextures(singleTextures);
    }

    public void addTextures(SingleTexture... singleTextures) {
        for (SingleTexture texture : singleTextures) {
            this.textures.add(texture);
        }
    }

    public void addTextures(List<SingleTexture> singleTextures) {
        for (SingleTexture texture : singleTextures) {
            this.textures.add(texture);
        }
    }

    public int getIndex() {
        return index;
    }

    public int getTextureCount() {
        return this.textures.size();
    }

    public void setIndex(int index) {
        if (index > -1 && index < textures.size()) {
            this.index = index;
            this.validIndex = true;
        } else {
            this.index = -1;
            this.validIndex = false;
        }
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void scale(float scaleX, float scaleY) {
        this.width *= scaleX;
        this.height *= scaleY;
        for (SingleTexture singleTexture : this.textures) {
            singleTexture.scale(scaleX, scaleY);
        }
    }

    public void render(float x, float y, float z, float alpha) {
        this.render(x, y, z, 1, 1, 1, alpha);
    }

    public void render(float x, float y, float z, float r, float g, float b, float alpha) {
        if (this.validIndex) {
            textures.get(index).render(x, y, z, r, g, b, alpha);
        }
    }

    public MultiTexture clone() {
        MultiTexture multiTexture = new MultiTexture(width, height);
        for (SingleTexture texture : this.textures) {
            multiTexture.addTextures(texture.clone());
        }
        return multiTexture;
    }
}
