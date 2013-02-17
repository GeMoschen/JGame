package de.gemo.engine.gui;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.collision.CollisionHelper;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.events.mouse.AbstractMouseEvent;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.exceptions.NotEnoughTexturesException;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.units.Vector;

import static org.lwjgl.opengl.GL11.*;

public class GUIDropdownList extends GUIElement {

    private String label = "";
    private Color normalColor, hoverColor, pressedColor;
    private UnicodeFont font;

    private float textHeight = 0;
    private float maxText = 0.78f;

    private final Animation elementTexture;
    private final ArrayList<Object> itemList;
    private final ArrayList<Hitbox> clickboxList;

    private int selectedIndex = 0;

    public GUIDropdownList(float x, float y, MultiTexture multiTexture, MultiTexture elementTexture) {
        super(x, y, multiTexture);
        if (multiTexture.getTextureCount() < 3) {
            throw new NotEnoughTexturesException(multiTexture.getTextureCount(), 3);
        }
        this.itemList = new ArrayList<Object>();
        this.elementTexture = new Animation(elementTexture);
        this.clickboxList = new ArrayList<Hitbox>();
        this.setAutoLooseFocus(false);
        this.setLooseFocusOnFocusClick(true);
        initDropdown();
    }

    public void addItem(Object object) {
        this.itemList.add(object);
        this.refreshElementClickboxes();
    }

    private void refreshElementClickboxes() {
        Hitbox elementClickbox;
        this.clickboxList.clear();
        float height = (this.elementTexture.getHeight()) / 2f;
        int index = 0;
        float startY = this.animation.getHalfHeight() + this.elementTexture.getHalfHeight() - 2;
        float dif = (this.animation.getWidth() - this.elementTexture.getWidth()) / 2f;
        for (int i = 0; i < this.itemList.size(); i++) {
            elementClickbox = new Hitbox(this.center);
            elementClickbox.addPoint(-this.elementTexture.getHalfWidth() - dif, -height + startY * index + startY);
            elementClickbox.addPoint(+this.elementTexture.getHalfWidth() - dif, -height + startY * index + startY);
            elementClickbox.addPoint(+this.elementTexture.getHalfWidth() - dif, height + startY * index + startY);
            elementClickbox.addPoint(-this.elementTexture.getHalfWidth() - dif, height + startY * index + startY);
            elementClickbox.recalculatePositions();
            this.clickboxList.add(elementClickbox);
            index++;
        }
    }

    private void initDropdown() {
        this.setFont(FontManager.getStandardFont());
        this.setColor(Color.white);
        this.setHoverColor(Color.white);
        this.setPressedColor(Color.white);
        this.animation.goToFrame(0);
        this.elementTexture.goToFrame(0);
        this.selectedIndex = -1;
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

    private void setLabel(String label) {
        this.label = this.getShortenedText(label, this.animation.getWidth() * this.maxText);
    }

    private String getShortenedText(String text, float maxWidth) {
        float width = this.font.getWidth(text);
        if (width > maxWidth) {
            width = this.font.getWidth(text + "...");
            String tempLabel = text;
            while (width >= maxWidth) {
                tempLabel = tempLabel.substring(0, tempLabel.length() - 1);
                width = this.font.getWidth(tempLabel + "...");
            }
            text = tempLabel + "...";
        }
        return text;
    }

    public String getLabel() {
        return this.label;
    }

    public Object getSelectedItem() {
        if (this.selectedIndex > -1 && this.selectedIndex < this.itemList.size()) {
            return this.itemList.get(this.selectedIndex);
        }
        return null;
    }

    public void setSelectedItem(int index) {
        if (index > -1 && index < this.itemList.size()) {
            this.selectedIndex = index;
            this.setLabel(this.itemList.get(this.selectedIndex).toString());
        } else {
            this.selectedIndex = -1;
            this.setLabel("");
        }
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
        if (this.isFocused() || this.isActive()) {
            this.animation.goToFrame(2);
        } else if (this.isHovered()) {
            this.animation.goToFrame(1);
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
    public boolean isVectorInClickbox(Vector vector) {
        if (!this.isFocused()) {
            return super.isVectorInClickbox(vector);
        } else {
            boolean result = super.isVectorInClickbox(vector);
            if (!result) {
                for (Hitbox box : this.clickboxList) {
                    if (CollisionHelper.isVectorInHitbox(vector, box)) {
                        return true;
                    }
                }
            }
            return result;
        }
    }

    @Override
    public void render() {
        // draw itemlist
        if (this.isFocused()) {
            float myY = -10;
            glTranslatef(0f, this.animation.getHeight() - 5, -2f);
            for (Object object : this.itemList) {
                this.elementTexture.render(((this.elementTexture.getWidth() - this.animation.getWidth()) / 2f), myY, this.getZ() - 100, 1);
                this.font.drawString((int) (-this.animation.getWidth() / 2f + 4), (int) (myY - this.textHeight + 1), this.getShortenedText(object.toString(), this.animation.getWidth() * this.maxText), this.normalColor);
                myY += this.elementTexture.getHeight();
            }
            glTranslatef(0f, -(this.animation.getHeight() - 5), +2f);
        }

        // draw normal Dropdown-appearance
        super.render();
        if (this.label.length() > 0) {
            glTranslatef(0f, 0f, -1f);
            if (this.isFocused()) {
                this.font.drawString((int) (-this.animation.getWidth() / 2f + 4), (int) (-this.textHeight), this.label, this.pressedColor);
            } else if (this.isHovered()) {
                this.font.drawString((int) (-this.animation.getWidth() / 2f + 4), (int) (-this.textHeight), this.label, this.hoverColor);
            } else {
                this.font.drawString((int) (-this.animation.getWidth() / 2f + 4), (int) (-this.textHeight), this.label, this.normalColor);
            }
            glTranslatef(0f, 0f, +1f);
        }

        this.refreshElementClickboxes();
        for (Hitbox box : this.clickboxList) {
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            box.render();
            glEnable(GL_BLEND);
        }
    }

    @Override
    public void fireMouseEvent(AbstractMouseEvent event) {
        if (event.isMouseClick()) {
            if (this.isFocused()) {
                MouseClickEvent mcEvent = (MouseClickEvent) event;
                if (mcEvent.isLeftButton()) {
                    int index = this.getClickedHitbox(new Vector(event.getX(), event.getY()));
                    if (index > -1) {
                        this.selectedIndex = index;
                        this.setLabel(this.itemList.get(this.selectedIndex).toString());
                    }
                }
            }
        }
    }

    private int getClickedHitbox(Vector vector) {
        if (super.isVectorInClickbox(vector)) {
            return -2;
        }
        int index = 0;
        for (Hitbox box : this.clickboxList) {
            if (CollisionHelper.isVectorInHitbox(vector, box)) {
                return index;
            }
            index++;
        }
        return -1;
    }

}
