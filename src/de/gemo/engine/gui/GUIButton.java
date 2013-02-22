package de.gemo.engine.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.exceptions.NotEnoughTexturesException;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.textures.Animation;
import de.gemo.engine.textures.MultiTexture;

import static org.lwjgl.opengl.GL11.*;

public class GUIButton extends GUIElement {

    protected String label = "";
    protected String originalLabel = "";
    protected Color normalColor, hoverColor, pressedColor;
    protected UnicodeFont font;

    protected float textWidth = 0, textHeight = 0;
    protected float maxText = 0.80f;

    public GUIButton(float x, float y, Animation animation) {
        super(x, y, animation);
        if (animation.getTextureCount() < 3) {
            throw new NotEnoughTexturesException(animation.getTextureCount(), 3);
        }
        initButton();
    }

    public GUIButton(float x, float y, MultiTexture multiTexture) {
        super(x, y, multiTexture);
        if (multiTexture.getTextureCount() < 3) {
            throw new NotEnoughTexturesException(multiTexture.getTextureCount(), 3);
        }
        initButton();
    }

    public GUIButton(float x, float y, Animation animation, String label) {
        this(x, y, animation);
        this.setLabel(label);
    }

    private void initButton() {
        this.setFont(FontManager.getStandardFont());
        this.setColor(Color.black);
        this.setHoverColor(Color.black);
        this.setPressedColor(Color.black);
        this.animation.goToFrame(0);
    }

    public void setColor(Color color) {
        this.normalColor = new Color(color.r, color.g, color.b, (float) this.alpha);
    }

    public Color getColor() {
        return normalColor;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }

    public Color getPressedColor() {
        return pressedColor;
    }

    public void setMaxText(float maxText) {
        this.maxText = maxText;
    }

    public float getMaxText() {
        return maxText;
    }

    public void setLabel(String label) {
        this.originalLabel = label;
        this.label = originalLabel;
        this.textWidth = this.font.getWidth(this.label);

        if (this.textWidth >= this.animation.getWidth() * maxText * 2) {
            this.label = label;
            this.textWidth = this.getFont().getWidth(this.label + "...");
            String tempLabel = this.label;
            while (this.textWidth >= this.animation.getWidth() * maxText * 2 && tempLabel.length() > 0) {
                tempLabel = tempLabel.substring(0, Math.max(tempLabel.length() - 1, 0));
                this.textWidth = this.font.getWidth(tempLabel + "...");
            }
            this.label = tempLabel + "...";
        }
        this.textWidth = (this.textWidth / 2);
    }

    public String getLabel() {
        return this.originalLabel;
    }

    public void setFont(UnicodeFont font) {
        this.font = font;
        this.setLabel(this.getLabel());
        this.textHeight = this.font.getHeight("Z") / 2f + this.font.getYOffset("Z") / 2f;
    }

    public UnicodeFont getFont() {
        return font;
    }

    @Override
    public void setStatus(GUIElementStatus status) {
        super.setStatus(status);
        if (this.isHovered()) {
            this.animation.goToFrame(1);
        } else if (this.isActive()) {
            this.animation.goToFrame(2);
        } else {
            this.animation.goToFrame(0);
        }
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.normalColor.a = (float) alpha;
        this.hoverColor.a = (float) alpha;
        this.pressedColor.a = (float) alpha;
    }

    @Override
    public void render() {
        super.render();
        glPushMatrix();
        {
            if (this.label.length() > 0) {
                glTranslatef(0f, 0f, -1f);
                if (this.isHovered()) {
                    this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.hoverColor);
                } else if (this.isActive()) {
                    this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.pressedColor);
                } else {
                    this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.normalColor);
                }
                glTranslatef(0f, 0f, +1f);
            }
        }
        glPopMatrix();
    }
}
