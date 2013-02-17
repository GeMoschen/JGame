package de.gemo.engine.gui;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.animation.SingleTexture;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.manager.FontManager;

import static org.lwjgl.opengl.GL11.*;

public class GUITextfield extends GUIElement {

    private static String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ˆ÷‰ƒ¸‹,;.:-_#'+*~!\"ß$%&/()=?}][{\\·‡<>| ^";

    private String label = "";
    private String originalLabel = "";
    private Color normalColor;
    private UnicodeFont font;

    private float textWidth = 0, textHeight = 0;
    private float maxText = 0.92f;
    private int maxLength = Integer.MAX_VALUE;

    private int tickCount = 0;
    private boolean showLine = true;

    private HashMap<Integer, Long> lastInputList = new HashMap<Integer, Long>();

    public GUITextfield(float x, float y, SingleTexture singleTexture) {
        super(x, y, singleTexture);
        initTextfield();
    }

    public GUITextfield(float x, float y, MultiTexture multiTexture) {
        super(x, y, multiTexture);
        initTextfield();
    }

    public GUITextfield(float x, float y, Animation animation) {
        super(x, y, animation);
        initTextfield();
    }

    private void initTextfield() {
        this.setFont(FontManager.getStandardFont());
        this.setColor(Color.white);
        this.setAutoLooseFocus(false);
        this.animation.goToFrame(0);
    }

    public void setMaxLength(int maxLength) {
        if (this.maxLength < 1) {
            this.maxLength = Integer.MAX_VALUE;
        }
        this.maxLength = maxLength;
    }

    public GUITextfield(float x, float y, SingleTexture singleTexture, String label) {
        this(x, y, singleTexture);
        this.setText(label);
    }

    public void setColor(Color color) {
        this.normalColor = new Color(color.r, color.g, color.b, (float) this.alpha);
    }

    public Color getColor() {
        return normalColor;
    }

    public void setMaxText(float maxText) {
        this.maxText = maxText;
    }

    public float getMaxText() {
        return maxText;
    }

    public void setText(String label) {
        this.originalLabel = label;
        this.label = originalLabel;
        this.textWidth = this.font.getWidth(this.label);

        if (this.textWidth >= this.animation.getWidth() * maxText) {
            this.label = label;
            this.textWidth = this.getFont().getWidth(this.label + "...");
            String tempLabel = this.label;
            while (this.textWidth >= this.animation.getWidth() * maxText) {
                tempLabel = tempLabel.substring(0, tempLabel.length() - 1);
                this.textWidth = this.font.getWidth(tempLabel + "...");
            }
            this.label = tempLabel + "...";
        }
        this.textWidth = (this.textWidth / 2);
    }

    public String getText() {
        return this.originalLabel;
    }

    public void setFont(UnicodeFont font) {
        this.font = font;
        this.setText(this.getText());
        this.textHeight = this.font.getHeight("Z") / 2f + this.font.getYOffset("Z") / 2f;
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
        super.render();
        glPushMatrix();
        {
            glTranslatef(0f, 0f, +1f);
            int x = (int) (-this.animation.getWidth() / 2 + 8);
            this.font.drawString(x, (int) (-this.textHeight), this.label, this.normalColor);
            if (this.isFocused() && this.showLine) {
                x += this.font.getWidth(this.label) - 2;
                this.font.drawString(x, (int) (-this.textHeight) - 1, "|", this.normalColor);
            }
            glTranslatef(0f, 0f, -1f);
        }
        glPopMatrix();
    }

    @Override
    public boolean handleKeyHold(KeyEvent event) {
        Long lastInput = lastInputList.get(event.getKey());
        if (lastInput == null) {
            lastInput = Long.MIN_VALUE;
        }

        int distance = 170;
        if (event.getKey() == 14) {
            distance = 70;
        }

        if (System.currentTimeMillis() > lastInput + distance) {
            if (allowedChars.contains("" + event.getCharacter()) && this.originalLabel.length() + 1 <= this.maxLength) {
                this.setText(originalLabel + event.getCharacter());
            } else {
                // BACKSPACE
                if (event.getKey() == 14 && originalLabel.length() > 0) {
                    this.setText(originalLabel.substring(0, originalLabel.length() - 1));
                }
            }
            lastInputList.put(event.getKey(), System.currentTimeMillis());
        }
        return false;
    }

    @Override
    public void doTick() {
        if (this.isFocused()) {
            tickCount++;
            if (tickCount % 10 == 0) {
                tickCount = 0;
                this.showLine = !this.showLine;
            }
        } else {
            this.showLine = true;
            this.tickCount = 0;
        }
    }
}
