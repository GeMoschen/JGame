package de.gemo.engine.gui;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.collision.CollisionHelper;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.events.mouse.AbstractMouseEvent;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.exceptions.NotEnoughTexturesException;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.textures.Animation;
import de.gemo.engine.textures.MultiTexture;
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

    private int leftX = 0, leftX2Topic = 0;
    private int selectedIndex = 0;

    public GUIDropdownList(float x, float y, MultiTexture multiTexture, MultiTexture elementTexture) {
        super(x, y, multiTexture);
        if (multiTexture.getTextureCount() < 3) {
            throw new NotEnoughTexturesException(multiTexture.getTextureCount(), 3);
        }
        this.itemList = new ArrayList<Object>();
        this.elementTexture = new Animation(elementTexture);
        this.leftX = (int) (-this.elementTexture.getWidth() / 2f) + 4;
        this.leftX2Topic = (int) (-this.animation.getWidth() / 2f + 4);
        this.clickboxList = new ArrayList<Hitbox>();
        this.setAutoLooseFocus(false);
        this.setLooseFocusOnFocusClick(true);
        initDropdown();
    }

    public void addItem(Object object) {
        this.itemList.add(object);
        this.refreshElementClickboxes();
    }

    public boolean removeItem(Object object) {
        boolean result = this.itemList.remove(object);
        this.refreshElementClickboxes();
        this.setSelectedItem(this.selectedIndex);
        return result;
    }

    public boolean removeItem(int index) {
        if (index > -1 && index < this.itemList.size()) {
            boolean result = this.itemList.remove(index) != null;
            this.refreshElementClickboxes();
            this.setSelectedItem(this.selectedIndex);
            return result;
        }
        return false;
    }

    private void refreshElementClickboxes() {
        Hitbox elementClickbox;
        this.clickboxList.clear();
        float height = (this.elementTexture.getHeight()) / 2f;
        float startY = this.animation.getHalfHeight() + this.elementTexture.getHalfHeight();
        float difX = (this.animation.getWidth() - this.elementTexture.getWidth()) / 2f;
        float itemY = 0;
        for (int i = 0; i < this.itemList.size(); i++) {
            elementClickbox = new Hitbox(this.center);
            elementClickbox.addPoint(-this.elementTexture.getHalfWidth() - difX, -height + itemY + startY);
            elementClickbox.addPoint(+this.elementTexture.getHalfWidth() - difX, -height + itemY + startY);
            elementClickbox.addPoint(+this.elementTexture.getHalfWidth() - difX, height + itemY + startY);
            elementClickbox.addPoint(-this.elementTexture.getHalfWidth() - difX, height + itemY + startY);
            elementClickbox.scaleX(this.scaleX);
            elementClickbox.scaleY(this.scaleY);
            elementClickbox.setAngle(this.getAngle());
            itemY += (this.elementTexture.getHeight());
            this.clickboxList.add(elementClickbox);
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
        // draw normal Dropdown-appearance
        super.render();
        if (this.label.length() > 0) {
            glTranslatef(0f, 0f, -1f);
            if (this.isFocused()) {
                this.font.drawString(leftX2Topic, (int) (-this.textHeight), this.label, this.pressedColor);
            } else if (this.isHovered()) {
                this.font.drawString(leftX2Topic, (int) (-this.textHeight), this.label, this.hoverColor);
            } else {
                this.font.drawString(leftX2Topic, (int) (-this.textHeight), this.label, this.normalColor);
            }
            glTranslatef(0f, 0f, +1f);
        }

        // draw itemlist
        if (this.isFocused()) {
            glPushMatrix();
            {
                glTranslatef((int) ((this.elementTexture.getWidth() - this.animation.getWidth()) / 2f), this.animation.getHeight(), +10);
                for (Object object : this.itemList) {
                    this.elementTexture.render(getAlpha());
                    this.font.drawString(leftX, (int) (-this.textHeight + 1), this.getShortenedText(object.toString(), this.animation.getWidth() * this.maxText), this.normalColor);
                    glTranslatef(0, this.elementTexture.getHeight(), 0);
                }
            }
            glPopMatrix();
        }
    }

    @Override
    public void debugRender() {
        super.debugRender();
        if (this.isFocused()) {
            for (Hitbox box : this.clickboxList) {
                box.render();
            }
        }
    }

    @Override
    public void rotate(float angle) {
        super.rotate(angle);
        for (Hitbox box : this.clickboxList) {
            box.rotate(angle);
        }
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        super.scale(scaleX, scaleY);
        for (Hitbox box : this.clickboxList) {
            box.scaleX(scaleX);
            box.scaleY(scaleY);
        }
    }

    @Override
    public void fireMouseEvent(AbstractMouseEvent event) {
        if (event.isMouseClick()) {
            if (this.isFocused()) {
                MouseClickEvent mcEvent = (MouseClickEvent) event;
                if (mcEvent.isLeftButton()) {
                    int index = this.getClickedHitbox(new Vector(event.getX(), event.getY()));
                    if (index > -1 && index != this.selectedIndex) {
                        int oldIndex = this.selectedIndex;
                        this.preItemChange(oldIndex, index);
                        this.selectedIndex = index;
                        this.setLabel(this.itemList.get(this.selectedIndex).toString());
                        this.postItemChange(oldIndex, index);
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

    public void preItemChange(int oldIndex, int newIndex) {
    }

    public void postItemChange(int oldIndex, int newIndex) {
    }

}
