package de.gemo.gameengine.textures;

import java.util.ArrayList;
import java.util.List;

public class MultiTexture {

    private final ArrayList<SingleTexture> textures;
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

    public SingleTexture getTexture(int index) {
        return this.textures.get(index);
    }

    public int getTextureCount() {
        return this.textures.size();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Animation toAnimation() {
        return new Animation(this);
    }
}
