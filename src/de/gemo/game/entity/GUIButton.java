package de.gemo.game.entity;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import de.gemo.game.animation.Animation;
import de.gemo.game.core.FontManager;

public class GUIButton extends GUIElement {

    private String label = "";
    private String originalLabel = "";
    private Color color;
    private TrueTypeFont font;

    private float textWidth = 0, textHeight = 0;
    private float maxText = 0.80f;

    public GUIButton(float x, float y, Texture texture) {
        super(x, y, texture);
        this.setTexture(texture, 1, 4);
        this.setFont(FontManager.getStandardFont());
        this.setColor(Color.white);
        this.getClickbox().scaleY(0.25f);
    }

    public GUIButton(float x, float y, Texture texture, String label) {
        this(x, y, texture);
        this.setLabel(label);
    }

    public void setColor(Color color) {
        this.color = new Color(color.r, color.g, color.b, (float) this.alpha);
    }

    public Color getColor() {
        return color;
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
        this.textHeight = this.font.getHeight(this.label) / 2;

        if (this.textWidth >= this.animation.getSingleTileWidth() * maxText * 2) {
            this.label = label;
            this.textWidth = this.getFont().getWidth(this.label + "...");
            String tempLabel = this.label;
            while (this.textWidth >= this.animation.getSingleTileWidth() * maxText * 2) {
                tempLabel = tempLabel.substring(0, tempLabel.length() - 1);
                this.textWidth = this.font.getWidth(tempLabel + "...");
            }
            this.label = tempLabel + "...";
        }
        this.textWidth = (this.textWidth / 2);
    }
    @Override
    public void scale(float scale) {
        super.scale(scale);
        this.setLabel(this.originalLabel);
    }

    public String getLabel() {
        return this.originalLabel;
    }

    public void setFont(TrueTypeFont font) {
        this.font = font;
        this.setLabel(this.getLabel());
    }

    public TrueTypeFont getFont() {
        return font;
    }

    @Override
    public void setStatus(GUIElementStatus status) {
        super.setStatus(status);
        this.animation.goToFrame(this.getStatus().ordinal());
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.color.a = (float) alpha;
    }

    public Animation getAnimation() {
        return animation;
    }

    @Override
    public void render() {
        super.render();
        if (this.label.length() > 0) {
            GL11.glTranslatef(0f, 0f, -1f);
            this.font.drawString((int) (-this.textWidth), (int) (-this.textHeight), this.label, this.color);
            GL11.glTranslatef(0f, 0f, +1f);
        }
    }
}
