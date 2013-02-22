package de.gemo.engine.gui;

import de.gemo.engine.textures.Animation;
import de.gemo.engine.textures.MultiTexture;
import de.gemo.engine.textures.SingleTexture;

public class GUIGraphic extends GUIElement {

    public GUIGraphic(float x, float y, SingleTexture singleTexture) {
        super(x, y, singleTexture);
        this.animation.goToFrame(0);
    }

    public GUIGraphic(float x, float y, MultiTexture multiTexture) {
        super(x, y, multiTexture);
        this.animation.goToFrame(0);
    }

    public GUIGraphic(float x, float y, Animation animation) {
        super(x, y, animation);
        this.animation.goToFrame(0);
    }
}
