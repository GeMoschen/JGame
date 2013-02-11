package de.gemo.engine.gui;

import de.gemo.engine.animation.SingleTexture;

public class GUIGraphic extends GUIElement {

    public GUIGraphic(float x, float y, SingleTexture singleTexture) {
        super(x, y, singleTexture);
        this.animation.goToFrame(0);
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        super.scale(scaleX, scaleY);
    }
}
