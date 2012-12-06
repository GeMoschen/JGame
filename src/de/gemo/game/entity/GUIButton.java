package de.gemo.game.entity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import de.gemo.game.animation.Animation;
import de.gemo.game.core.FontManager;

public class GUIButton extends GUIElement {

    private GUIElementStatus status;
    private Animation animation;

    private float width = 128, height = 32;
    private String label = "";
    private Color color;
    private TrueTypeFont font;

    private float textWidth = 0, textHeight = 0;

    private float maxText = 0.8f;
    private ActionListener listener = null;

    public GUIButton(float x, float y, float width, float height, Texture texture) {
        super(x, y, width, height, texture);
        this.width = width;
        this.height = height;
        this.animation = new Animation(texture, 1, 4);
        this.setStatus(GUIElementStatus.NONE);
        this.setFont(FontManager.getStandardFont());
        this.setColor(Color.white);
        this.listener = null;
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
        this.label = label;
        this.textWidth = this.font.getWidth(this.label);
        this.textHeight = this.font.getHeight(this.label) / 2;

        if (this.textWidth >= this.width * maxText) {
            this.label = label;
            this.textWidth = this.getFont().getWidth(this.label + "...");
            String tempLabel = this.label;
            while (this.textWidth >= this.width * maxText) {
                tempLabel = tempLabel.substring(0, tempLabel.length() - 1);
                this.textWidth = this.font.getWidth(tempLabel + "...");
            }
            this.label = tempLabel + "...";
        }
        this.textWidth = (int) (this.textWidth / 2);
    }

    public String getLabel() {
        return label;
    }

    public void setFont(TrueTypeFont font) {
        this.font = font;
        this.setLabel(this.getLabel());
    }

    public TrueTypeFont getFont() {
        return font;
    }

    public void setStatus(GUIElementStatus status) {
        this.status = status;
        this.animation.goToFrame(this.status.ordinal());
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.color.a = (float) alpha;
    }

    public GUIElementStatus getStatus() {
        return status;
    }

    public void setActionListener(ActionListener actionListener) {
        this.listener = actionListener;
    }

    public void fireEvent(ActionEvent event) {
        if (this.listener != null) {
            this.listener.actionPerformed(event);
        }
    }

    public Animation getAnimation() {
        return animation;
    }

    @Override
    public void render() {
        this.animation.render(this.center.getX(), this.center.getY(), this.center.getZ(), this.angle, this.alpha, this.width, this.height);
        if (this.label.length() > 0) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glTranslated(0d, 0d, -1d);
            this.font.drawString((float) (-this.textWidth), (float) (-this.textHeight), this.label, this.color);
            GL11.glTranslated(0d, 0d, +1d);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
}
