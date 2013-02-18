package de.gemo.engine.gui;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.exceptions.NotEnoughTexturesException;

import static org.lwjgl.opengl.GL11.*;

public class GUIButtonScalable extends GUIButton {

    protected float width = 120, sizeWidth = 7;

    public GUIButtonScalable(float x, float y, Animation animation) {
        super(x, y, animation);
        if (animation.getTextureCount() < 9) {
            throw new NotEnoughTexturesException(animation.getTextureCount(), 3);
        }
        this.setWidth(width);
        this.generateClickbox();
    }

    public GUIButtonScalable(float x, float y, MultiTexture multiTexture) {
        super(x, y, multiTexture);
        if (multiTexture.getTextureCount() < 9) {
            throw new NotEnoughTexturesException(multiTexture.getTextureCount(), 3);
        }
        this.setWidth(width);
        this.generateClickbox();
    }

    private void generateClickbox() {
        float x = width / 2f + sizeWidth / 2f;
        float y = this.animation.getHeight() / 2;
        Hitbox clickbox = new Hitbox(this.center.getX() + width / 2f, this.center.getY());

        clickbox.addPoint(-x, -y);
        clickbox.addPoint(+x, -y);
        clickbox.addPoint(+x, +y);
        clickbox.addPoint(-x, +y);
        clickbox.recalculatePositions();
        this.setClickbox(clickbox);
    }

    public GUIButtonScalable(float x, float y, Animation animation, String label) {
        super(x, y, animation, label);
        this.setWidth(200);
    }

    public void setWidth(float width) {
        this.width = width - 2 * sizeWidth;
    }

    public void setLabel(String label) {
        this.originalLabel = label;
        this.label = originalLabel;
        this.textWidth = this.font.getWidth(this.label);

        if (this.textWidth > this.width) {
            this.label = label;
            this.textWidth = this.getFont().getWidth(this.label + "...");
            String tempLabel = this.label;
            while (this.textWidth > this.width && tempLabel.length() > 0) {
                tempLabel = tempLabel.substring(0, Math.max(tempLabel.length() - 1, 0));
                this.textWidth = this.font.getWidth(tempLabel + "...");
            }
            this.label = tempLabel + "...";
        }
        this.textWidth = (this.textWidth / 2);
    }

    @Override
    public void render() {
        glPushMatrix();
        {
            glTranslatef(width / 2f, 0, 0);
            glPushMatrix();
            {
                this.animation.getMultiTextures().getTexture(this.getStatus().ordinal()).render(width, this.animation.getHeight(), 1, 1, 1, getAlpha());
                glTranslatef(-(width / 2f), 0, 0);
                this.animation.getMultiTextures().getTexture(this.getStatus().ordinal() + 3).render(sizeWidth, this.animation.getHeight(), 1, 1, 1, getAlpha());
                glTranslatef(+(width / 2f), 0, 0);

                glTranslatef(+(width / 2f), 0, 0);
                this.animation.getMultiTextures().getTexture(this.getStatus().ordinal() + 6).render(sizeWidth, this.animation.getHeight(),1, 1, 1, getAlpha());
                glTranslatef(-(width / 2f), 0, 0);
            }
            glPopMatrix();

            glPushMatrix();
            {
                if (this.label.length() > 0) {
                    glTranslatef(0f, 0f, -1f);
                    if (this.isHovered()) {
                        this.hoverColor.bind();
                        this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.hoverColor);
                    } else if (this.isActive()) {
                        this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.pressedColor);
                    } else {
                        this.normalColor.bind();
                        this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.normalColor);
                    }
                    glTranslatef(0f, 0f, +1f);
                }
            }
            glPopMatrix();
        }
        glPopMatrix();
    }
}
