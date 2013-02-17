package de.gemo.engine.manager;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.ConfigurableEffect;
import org.newdawn.slick.util.ResourceLoader;

public class FontManager {

    public static final String DEFAULT = "Verdana";
    public static final int MINIMUM_DEFAULT_SIZE = 12;

    public static final String ANALOG = "ANALOG";

    private static UnicodeFont standardFont;
    private static HashMap<String, UnicodeFont> fontMap;

    public static void initFirstFont() {
        fontMap = new HashMap<String, UnicodeFont>();
        standardFont = loadFont(DEFAULT, Font.PLAIN, MINIMUM_DEFAULT_SIZE);
        loadFont(DEFAULT, Font.BOLD, MINIMUM_DEFAULT_SIZE);
        loadFont(DEFAULT, Font.ITALIC, MINIMUM_DEFAULT_SIZE);
        loadFont(DEFAULT, Font.BOLD | Font.ITALIC, MINIMUM_DEFAULT_SIZE);
    }

    public static void init() {
        fontMap = new HashMap<String, UnicodeFont>();
        standardFont = loadFont(DEFAULT, Font.PLAIN, MINIMUM_DEFAULT_SIZE);
        for (int size = MINIMUM_DEFAULT_SIZE + 2; size < 30; size += 2) {
            loadFont(DEFAULT, Font.PLAIN, size);
            loadFont(DEFAULT, Font.BOLD, size);
            loadFont(DEFAULT, Font.ITALIC, size);
            loadFont(DEFAULT, Font.BOLD | Font.ITALIC, size);
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadFontFromJar(String path, String fontName, int style, int size) {
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream(path);
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont = awtFont.deriveFont((float) size).deriveFont(style);
            UnicodeFont font = new UnicodeFont(awtFont);
            font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
            font.addAsciiGlyphs();
            try {
                font.loadGlyphs();
            } catch (SlickException e) {
                e.printStackTrace();
            }
            fontMap.put(fontName + "_" + style + "_" + size, font);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadFontFromJar(String path, String fontName, int style, int size, ConfigurableEffect... effects) {
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream(path);
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont = awtFont.deriveFont((float) size).deriveFont(style);
            UnicodeFont font = new UnicodeFont(awtFont);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        UnicodeFont font = getFont(DEFAULT, style, MINIMUM_DEFAULT_SIZE);
        return ((font != null) ? font : standardFont);
    }
}
