package de.gemo.engine.gui;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.core.FontManager;

public class GUIButton extends GUIElement {

    private String label = "";
    private String originalLabel = "";
    private Color normalColor, hoverColor, pressedColor, shadowColor;
    private UnicodeFont font;

    private float textWidth = 0, textHeight = 0;
    private float maxText = 0.80f;

    public GUIButton(float x, float y, Animation animation) {
        super(x, y, animation);
        initButton();
    }

    public GUIButton(float x, float y, MultiTexture multiTexture) {
        super(x, y, multiTexture);
        initButton();
    }

    public GUIButton(float x, float y, Animation animation, String label) {
        this(x, y, animation);
        this.setLabel(label);
    }

    private void initButton() {
        this.setFont(FontManager.getStandardFont());
        this.setColor(Color.white);
        this.setHoverColor(Color.white);
        this.setPressedColor(Color.white);
        this.shadowColor = new Color(50, 50, 50);
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
        this.originalLabel = label.toUpperCase();
        this.label = originalLabel;
        this.textWidth = this.font.getWidth(this.label);

        if (this.textWidth >= this.animation.getWidth() * maxText * 2) {
            this.label = label;
            this.textWidth = this.getFont().getWidth(this.label + "...");
            String tempLabel = this.label;
            while (this.textWidth >= this.animation.getWidth() * maxText * 2) {
                tempLabel = tempLabel.substring(0, tempLabel.length() - 1);
                this.textWidth = this.font.getWidth(tempLabel + "...");
            }
            this.label = tempLabel + "...";
        }

        this.textHeight = this.font.getHeight(this.label) / 2f + this.font.getYOffset(this.label) / 2f;
        this.textWidth = (this.textWidth / 2);
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        super.scale(scaleX, scaleY);
        this.setLabel(this.originalLabel);
    }

    public String getLabel() {
        return this.originalLabel;
    }

    public void setFont(UnicodeFont font) {
        this.font = font;
        this.setLabel(this.getLabel());
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
        this.shadowColor.a = (float) alpha;
    }

    public Animation getAnimation() {
        return animation;
    }

    @Override
    public void render() {
        super.render();
        if (this.label.length() > 0) {
            GL11.glTranslatef(0f, 0f, -1f);
            if (this.isHovered()) {
                this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.hoverColor);
            } else if (this.isActive()) {
                this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.pressedColor);
            } else {
                this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.normalColor);
            }
            GL11.glTranslatef(0f, 0f, +1f);
        }
    }
}
