package de.gemo.engine.manager;

import java.awt.Font;
import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.ConfigurableEffect;

public class FontManager {

    public static final String VERDANA = "Verdana";

    private static UnicodeFont standardFont;
    private static HashMap<String, UnicodeFont> fontMap;

    static {
        fontMap = new HashMap<String, UnicodeFont>();
        standardFont = loadFont(VERDANA, Font.PLAIN, 12);
    }

    @SuppressWarnings("unchecked")
    public static UnicodeFont loadFont(String fontName, int style, int size) {
        Font winFont = new Font(fontName, style, size);
        UnicodeFont font = new UnicodeFont(winFont);
        font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        font.addAsciiGlyphs();
        try {
            font.loadGlyphs();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        fontMap.put(fontName + "_" + style + "_" + size, font);
        return font;
    }

    @SuppressWarnings("unchecked")
    public static UnicodeFont loadFont(String fontName, int style, int size, ConfigurableEffect... effects) {
        Font winFont = new Font(fontName, style, size);
        UnicodeFont font = new UnicodeFont(winFont);
        for (ConfigurableEffect effect : effects) {
            font.getEffects().add(effect);
        }
        font.addAsciiGlyphs();
        try {
            font.loadGlyphs();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        fontMap.put(fontName + "_" + style + "_" + size, font);
        return font;
    }

    public static UnicodeFont getFont(String fontName, int style, int size) {
        UnicodeFont font = fontMap.get(fontName + "_" + style + "_" + size);
        return font != null ? font : standardFont;
    }

    public static UnicodeFont getStandardFont() {
        return standardFont;
    }
}
