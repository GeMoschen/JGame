package de.gemo.engine.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.manager.FontManager;

public class GUILabel extends GUIElement {

    private String label = "";
    private Color normalColor;
    private UnicodeFont font;

    private float textHeight = 0f;

    public GUILabel(float x, float y, String label) {
        super(x, y, new MultiTexture(0, 0));
        initLabel();
        this.setLabel(label);
    }

    private void initLabel() {
        this.setFont(FontManager.getStandardFont());
        this.setColor(Color.white);
        this.autoGenerateClickbox();
    }

    public void setColor(Color color) {
        this.normalColor = new Color(color.r, color.g, color.b, (float) this.alpha);
    }

    public Color getColor() {
        return normalColor;
    }

    public void setLabel(String label) {
        float oldWidth = this.font.getWidth(this.label);
        this.label = label;
        float newWidth = this.font.getWidth(this.label);
        this.setTexture(new MultiTexture(this.font.getWidth(this.label), this.textHeight * 2));
        this.autoGenerateClickbox();
        float newHalfWidth = (newWidth - oldWidth) / 2;
        int integer = Math.round(this.getXOnScreen() + newHalfWidth);
        this.setPositionOnScreen(integer, (int) this.getYOnScreen());
    }

    public String getLabel() {
        return this.label;
    }

    public void setFont(UnicodeFont font) {
        int oldX = this.getXOnScreen();
        int oldY = this.getYOnScreen();
        this.font = font;
        this.setLabel(this.getLabel());
        this.textHeight = (int) (this.font.getHeight("Z") / 2f + this.font.getYOffset("Z") / 2f);
        this.setPositionOnScreen(oldX, oldY);
    }

    public UnicodeFont getFont() {
        return font;
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.normalColor.a = (float) alpha;
    }

    @Override
    public void render() {
        if (this.label.length() > 0) {
            int x = (int) (this.getX() - this.getXOnScreen());
            this.font.drawString(-x, (int) (-this.textHeight), this.label, this.normalColor);
        }
    }
}
