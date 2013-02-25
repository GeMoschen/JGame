package de.gemo.engine.gui;

import de.gemo.engine.textures.Animation;
import de.gemo.engine.textures.MultiTexture;

import static org.lwjgl.opengl.GL11.*;

public class GUIImageButton extends GUIButton {

    private boolean selected = false;
    private Animation iconAnimation;
    private String label = "";

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public GUIImageButton(float x, float y, Animation animation, Animation iconAnimation, int iconIndex) {
        super(x, y, animation);
        this.iconAnimation = iconAnimation.clone();
        this.iconAnimation.goToFrame(iconIndex);
    }

    public GUIImageButton(float x, float y, MultiTexture multiTexture, MultiTexture multiTextureIcons, int iconIndex) {
        this(x, y, multiTexture.toAnimation(), multiTextureIcons.toAnimation(), iconIndex);
    }

    @Override
    public void render() {
        if (!this.isSelected()) {
            super.render();
        } else {
            int frame = this.animation.getCurrentFrame();
            this.animation.goToFrame(2);
            super.render(1, 1, 0);
            this.animation.goToFrame(frame);
        }
        glPushMatrix();
        {
            glTranslatef(0f, 0f, -1f);
            this.iconAnimation.render();
            glTranslatef(0f, 0f, +1f);
        }
        glPopMatrix();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

}
