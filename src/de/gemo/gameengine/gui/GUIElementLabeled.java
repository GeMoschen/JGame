package de.gemo.gameengine.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

import de.gemo.gameengine.manager.FontManager;
import de.gemo.gameengine.renderer.IFontRenderable;
import de.gemo.gameengine.renderer.Renderer;

import static org.lwjgl.opengl.GL11.*;

public abstract class GUIElementLabeled extends GUIElement implements IFontRenderable {

    public static enum TextAlignment {
        LEFT, CENTER, RIGHT;
    }

    protected Font font = FontManager.getStandardFont();
    protected Color fontColor = Color.white;
    protected String text = "";

    protected float halfTextWidth = 0, halfTextHeight = 0;
    protected TextAlignment alignment = TextAlignment.LEFT;

    @Override
    public final void setText(String text) {
        if (text != null) {
            this.text = text;
        } else {
            this.text = "";
        }

        // calculate
        this.halfTextWidth = this.font.getWidth(this.text) / 2f;
        this.halfTextHeight = this.font.getHeight(this.text) / 2f;
    }

    @Override
    public final String getText() {
        return this.text != null ? this.text : "";
    }

    @Override
    public final void setFontColor(Color fontColor) {
        if (fontColor != null) {
            this.fontColor = fontColor;
        } else {
            this.fontColor = Color.white;
        }
    }

    @Override
    public final Color getFontColor() {
        return fontColor;
    }

    @Override
    public final void setFont(Font font) {
        if (font != null) {
            this.font = font;
        }
    }

    @Override
    public final Font getFont() {
        return this.font;
    }

    @Override
    public final TextAlignment getTextAlignment() {
        return alignment;
    }

    @Override
    public final void setTextAlignment(TextAlignment alignment) {
        if (this.alignment == null) {
            this.alignment = TextAlignment.LEFT;
            return;
        }
        this.alignment = alignment;
    }

    @Override
    public final void renderFont() {
        // print text
        if (this.font != null) {
            glPushMatrix();
            {
                // translate to inherited position
                glTranslatef(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
                glRotatef(this.getAngle(), 0f, 0f, 1f);

                // translate to textposition
                switch (this.alignment) {
                case LEFT: {
                    glTranslatef((int) (4), (int) ((this.size.getY() / 2f) - this.halfTextHeight), this.getPosition().getZ() + 1);
                    break;
                }
                case CENTER: {
                    glTranslatef((int) ((this.size.getX() / 2f) - this.halfTextWidth), (int) ((this.size.getY() / 2f) - this.halfTextHeight), this.getPosition().getZ() + 1);
                    break;
                }
                case RIGHT: {
                    glTranslatef((int) ((this.size.getX()) - (this.halfTextWidth * 2f) - 5), (int) ((this.size.getY() / 2f) - this.halfTextHeight), this.getPosition().getZ() + 1);
                    break;
                }
                }

                // render string
                font.drawString(0, 0, this.text, this.fontColor);
            }
            glPopMatrix();
        }
    }

    @Override
    public void render() {
        super.render();
        Renderer.addFontRender(this);
    }

    @Override
    public void debugRender() {
        super.render();
        Renderer.addFontRender(this);
    }
}
