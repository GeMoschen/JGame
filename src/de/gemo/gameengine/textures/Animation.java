package de.gemo.gameengine.textures;

import java.util.HashSet;
import java.util.UUID;

public class Animation {

    private static final HashSet<UUID> TAKEN_UUIDS = new HashSet<>();

    protected int currentFrame = -1;
    private MultiTexture multiTextures;
    private float halfWidth = 0, halfHeight = 0;

    protected float currentStep = 0f;

    private float wantedFPS;

    private Runnable _endListener = null;
    private UUID _uuid;

    public Animation(MultiTexture multiTexture) {
        this(multiTexture, 30);
    }

    public Animation(MultiTexture multiTexture, int wantedFPS) {
        this.multiTextures = multiTexture;
        this.setWantedFPS(wantedFPS);
        this.halfWidth = this.multiTextures.getWidth() / 2f;
        this.halfHeight = this.multiTextures.getHeight() / 2f;
        this.goToFrame(0);
        _uuid = UUID.randomUUID();
        while (TAKEN_UUIDS.contains(_uuid)) {
            _uuid = UUID.randomUUID();
        }
    }

    public void setEndListener(final Runnable endListener) {
        _endListener = endListener;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public int getNumFrames() {
        return multiTextures.getTextureCount();
    }

    public void goToNextFrame() {
        this.setCurrentFrame(this.currentFrame + 1);
    }

    public void goToPreviousFrame() {
        this.setCurrentFrame(this.currentFrame - 1);
    }

    public void goToFirstFrame() {
        this.setCurrentFrame(0);
    }

    public void goToLastFrame() {
        this.setCurrentFrame(getNumFrames() - 1);
    }

    public void setCurrentFrame(int frame) {
        this.currentFrame = frame;
        this.currentStep = frame;
    }

    public boolean goToFrame(int frame) {
        // we need a MultiTexture
        if (this.multiTextures == null) {
            return false;
        }

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
            currentStep -= (int) currentStep;
            if (_endListener != null) {
                _endListener.run();
            }
        }

        // updatePosition the frame
        this.currentFrame = frame;
        return result;
    }

    public void setWantedFPS(int wantedFPS) {
        this.wantedFPS = (float) wantedFPS / 1000f;
    }

    public boolean step(float delta) {
        if (this.multiTextures == null) {
            return false;
        }

        float toGo = this.wantedFPS * delta;
        this.currentStep += toGo;
        boolean result = false;
        if (this.currentStep >= this.multiTextures.getTextureCount()) {
            this.currentStep -= this.multiTextures.getTextureCount();
            if (_endListener != null) {
                _endListener.run();
            }
            result = true;
        }
        return result || this.goToFrame((int) this.currentStep);
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public float getCurrentStep() {
        return currentStep;
    }

    public SingleTexture getCurrentTexture() {
        if (this.multiTextures == null) {
            return null;
        }
        return this.multiTextures.getTexture(this.getCurrentFrame());
    }

    public float getWidth() {
        if (this.multiTextures == null) {
            return 0;
        }
        return this.multiTextures.getWidth();
    }

    public float getHeight() {
        if (this.multiTextures == null) {
            return 0;
        }
        return this.multiTextures.getHeight();
    }

    public void render() {
        this.render(1f, 1f, 1f, 1f);
    }

    public void render(float alpha) {
        this.render(1f, 1f, 1f, alpha);
    }

    public void render(float r, float g, float b, float alpha) {
        if (this.multiTextures == null) {
        }
        if (this.currentFrame > -1 && this.currentFrame < this.multiTextures.getTextureCount()) {
            this.multiTextures.getTexture(this.currentFrame).render(r, g, b, alpha);
        }
    }

    public MultiTexture getMultiTextures() {
        return multiTextures;
    }

    public int getTextureCount() {
        if (this.multiTextures != null) {
            return this.multiTextures.getTextureCount();
        } else {
            return 0;
        }
    }

    public Animation clone() {
        final Animation animation = new Animation(this.multiTextures);
        animation._uuid = _uuid;
        return animation;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Animation) {
            final Animation other = (Animation) obj;
            return _uuid == other._uuid;
        }
        return false;
    }
}
