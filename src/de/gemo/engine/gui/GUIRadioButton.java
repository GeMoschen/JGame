package de.gemo.engine.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.gemo.engine.exceptions.NotEnoughTexturesException;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.textures.MultiTexture;

import static org.lwjgl.opengl.GL11.*;

public class GUIRadioButton extends GUIElement {

    private String label = "";
    private Color normalColor;
    private boolean checked;
    private TrueTypeFont font;
    private RadioGroup group = null;

    private float textHeight = 0;

    public GUIRadioButton(float x, float y, MultiTexture multiTexture, String label) {
        this(x, y, multiTexture, label, false);
    }

    public GUIRadioButton(float x, float y, MultiTexture multiTexture, String label, boolean checked) {
        super(x, y, multiTexture);
        if (multiTexture.getTextureCount() < 2) {
            throw new NotEnoughTexturesException(multiTexture.getTextureCount(), 2);
        }
        initCheckbox(checked, label);
    }

    private void initCheckbox(boolean checked, String label) {
        this.setChecked(checked);
        this.setFont(FontManager.getStandardFont());
        this.setColor(Color.white);
        this.setLabel(label);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (this.isChecked()) {
            this.animation.goToFrame(1);
            if (this.group != null) {
                this.group.setActiveElement(this);
            }
        } else {
            this.animation.goToFrame(0);
        }
    }

    public RadioGroup getGroup() {
        return group;
    }

    public void setGroup(RadioGroup group) {
        if (this.group != group && this.group != null) {
            this.group.removeElement(this);

        }
        if (group != null) {
            group.addElement(this);
        }
        this.group = group;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setColor(Color color) {
        this.normalColor = new Color(color.r, color.g, color.b, (float) this.alpha);
    }

    public Color getColor() {
        return normalColor;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public void setFont(TrueTypeFont font) {
        this.font = font;
        this.setLabel(this.getLabel());
        this.textHeight = this.font.getHeight("Z") / 2f ;
    }

    public TrueTypeFont getFont() {
        return font;
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.normalColor.a = (float) alpha;
    }

    @Override
    public void render() {
        super.render();
        glTranslatef(0f, 0f, -1f);
        int x = (int) (this.animation.getWidth() / 2f + 2);
        this.font.drawString(x, (int) (-this.textHeight), this.label, this.normalColor);
        glTranslatef(0f, 0f, +1f);
    }

    @Override
    public void fireFocusGainedEvent() {
        this.setChecked(true);
    }
}
