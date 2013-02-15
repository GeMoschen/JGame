package de.gemo.engine.manager;

import java.awt.Font;
import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.ConfigurableEffect;

public class FontManager {

    public static final String DEFAULT = "Verdana";
    public static final int DEFAULTSIZE = 12;
    public static final String ANALOG = "Analog";

    private static UnicodeFont standardFont;
    private static HashMap<String, UnicodeFont> fontMap;

    static {
        fontMap = new HashMap<String, UnicodeFont>();
        standardFont = loadFont(DEFAULT, Font.PLAIN, DEFAULTSIZE);
        loadFont(DEFAULT, Font.BOLD, DEFAULTSIZE);
        loadFont(DEFAULT, Font.ITALIC, DEFAULTSIZE);
        loadFont(DEFAULT, Font.BOLD | Font.ITALIC, DEFAULTSIZE);
    }

    @SuppressWarnings("unchecked")
    public static UnicodeFont loadFont(String fontName, int style, int size) {
        if (getFontButNull(fontName, style, size) == null) {
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
        } else {
            System.out.println("ERROR: Font '" + fontName + "' ('" + style + "', " + size + ") is already registered");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static UnicodeFont loadFont(String fontName, int style, int size, ConfigurableEffect... effects) {
        if (getFontButNull(fontName, style, size) == null) {
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
        } else {
            System.out.println("ERROR: Font '" + fontName + "' ('" + style + "', " + size + ") is already registered");
            return null;
        }
    }

    public static UnicodeFont getFont(String fontName, int style, int size) {
        UnicodeFont font = fontMap.get(fontName + "_" + style + "_" + size);
        return font != null ? font : standardFont;
    }

    private static UnicodeFont getFontButNull(String fontName, int style, int size) {
        return fontMap.get(fontName + "_" + style + "_" + size);
    }

    public static UnicodeFont getStandardFont() {
        return standardFont;
    }

    public static UnicodeFont getStandardFont(int style) {
        UnicodeFont font = getFont(DEFAULT, style, DEFAULTSIZE);
        return ((font != null) ? font : standardFont);
    }
}
