package de.gemo.gameengine.renderer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

import de.gemo.gameengine.gui.GUIElementLabeled.TextAlignment;

public interface IFontRenderable {
    public void setText(String text);

    public String getText();

    public void setFontColor(Color fontColor);

    public Color getFontColor();

    public void setFont(Font font);

    public Font getFont();

    public TextAlignment getTextAlignment();

    public void setTextAlignment(TextAlignment alignment);

    public void renderFont();
}
