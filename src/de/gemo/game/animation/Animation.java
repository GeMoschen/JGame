package de.gemo.game.animation;

public class Animation {

    protected int currentFrame = -1;
    private MultiTexture multiTextures;

    protected float currentStep = 0f;

    private float wantedFPS;

    public Animation(MultiTexture multiTexture) {
        this(multiTexture, 30);
    }

    public Animation(MultiTexture multiTexture, int wantedFPS) {
        this.multiTextures = multiTexture;
        this.setWantedFPS(wantedFPS);
    }

    public void nextFrame() {
        this.goToFrame(this.currentFrame + 1);
    }

    public void lastFrame() {
        this.goToFrame(this.currentFrame - 1);
    }

    public void goToFrame(int frame) {
        // check framebounds
        if (frame < 0) {
            frame = this.multiTextures.getTextureCount() - 1;
        } else if (frame > this.multiTextures.getTextureCount()) {
            frame = 0;
        }

        // update the frame
        this.currentFrame = frame;
        this.multiTextures.setIndex(this.currentFrame);
    }

    public void setWantedFPS(int wantedFPS) {
        this.wantedFPS = (float) wantedFPS / 1000f;
    }

    public void step(float delta) {
        float toGo = this.wantedFPS * delta;
        this.currentStep += toGo;
        if (this.currentStep >= this.multiTextures.getTextureCount()) {
            this.currentStep -= this.multiTextures.getTextureCount();
        }
        this.goToFrame((int) this.currentStep);
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void scale(float scaleX, float scaleY) {
        this.multiTextures.scale(scaleX, scaleY);
    }

    public float getWidth() {
        return this.multiTextures.getWidth();
    }

    public float getHeight() {
        return this.multiTextures.getHeight();
    }

    public void render(float x, float y, float z, float alpha) {
        this.render(x, y, z, 1, 1, 1, alpha);
    }

    public void render(float x, float y, float z, float r, float g, float b, float alpha) {
        this.multiTextures.render(x, y, z, r, g, b, alpha);
    }

    public Animation clone() {
        return new Animation(this.multiTextures.clone());
    }
}
