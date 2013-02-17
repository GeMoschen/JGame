package de.gemo.engine.animation;

public class Animation {

    protected int currentFrame = -1;
    private MultiTexture multiTextures;
    private float halfWidth = 0, halfHeight = 0;

    protected float currentStep = 0f;

    private float wantedFPS;

    public Animation(MultiTexture multiTexture) {
        this(multiTexture, 30);
    }

    public Animation(MultiTexture multiTexture, int wantedFPS) {
        this.multiTextures = multiTexture;
        this.setWantedFPS(wantedFPS);
        this.halfWidth = this.multiTextures.getWidth() / 2f;
        this.halfHeight = this.multiTextures.getHeight() / 2f;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public void nextFrame() {
        this.goToFrame(this.currentFrame + 1);
    }

    public void lastFrame() {
        this.goToFrame(this.currentFrame - 1);
    }

    public boolean goToFrame(int frame) {
        // we need different frames
        if (this.currentFrame == frame) {
            return false;
        }

        // check framebounds
        boolean result = false;
        if (frame < 0) {
            frame = this.multiTextures.getTextureCount() - 1;
            result = true;
        } else if (frame > this.multiTextures.getTextureCount() - 1) {
            frame = 0;
            result = true;
        }

        // update the frame
        this.currentFrame = frame;
        return result;
    }

    public void setWantedFPS(int wantedFPS) {
        this.wantedFPS = (float) wantedFPS / 1000f;
    }

    public boolean step(float delta) {
        float toGo = this.wantedFPS * delta;
        this.currentStep += toGo;
        boolean result = false;
        if (this.currentStep >= this.multiTextures.getTextureCount()) {
            this.currentStep -= this.multiTextures.getTextureCount();
            result = true;
        }
        return result || this.goToFrame((int) this.currentStep);
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public float getWidth() {
        return this.multiTextures.getWidth();
    }

    public float getHeight() {
        return this.multiTextures.getHeight();
    }

    public void render(float x, float y, float alpha) {
        this.render(x, y, 1, 1, 1, alpha);
    }

    public void render(float x, float y, float r, float g, float b, float alpha) {
        if (this.currentFrame >= 0 && this.currentFrame < this.multiTextures.getTextureCount()) {
            this.multiTextures.getTexture(this.currentFrame).render(x, y, r, g, b, alpha);
        }
    }

    public int getTextureCount() {
        if (this.multiTextures != null) {
            return this.multiTextures.getTextureCount();
        } else {
            return 0;
        }
    }

    public Animation clone() {
        return new Animation(this.multiTextures);
    }
}
