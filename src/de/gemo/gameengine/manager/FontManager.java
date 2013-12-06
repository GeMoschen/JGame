package de.gemo.gameengine.manager;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

public class FontManager {

    public static final String DEFAULT = "Verdana";
    public static final int MINIMUM_DEFAULT_SIZE = 10;

    public static final String ANALOG = "ANALOG";

    private static TrueTypeFont standardFont;
    private static final HashMap<String, TrueTypeFont> fontMap = new HashMap<String, TrueTypeFont>();

    public static void initFirstFont() {
        loadFont(DEFAULT, Font.PLAIN, MINIMUM_DEFAULT_SIZE - 2);
        loadFont(DEFAULT, Font.PLAIN, MINIMUM_DEFAULT_SIZE);
        loadFont(DEFAULT, Font.BOLD, MINIMUM_DEFAULT_SIZE);
        loadFont(DEFAULT, Font.ITALIC, MINIMUM_DEFAULT_SIZE);
        loadFont(DEFAULT, Font.BOLD | Font.ITALIC, MINIMUM_DEFAULT_SIZE);
        standardFont = getFont(DEFAULT, Font.PLAIN, MINIMUM_DEFAULT_SIZE);
    }

    public static void init() {
        for (int size = MINIMUM_DEFAULT_SIZE + 2; size <= 24; size += 2) {
            loadFont(DEFAULT, Font.PLAIN, size);
            loadFont(DEFAULT, Font.BOLD, size);
            loadFont(DEFAULT, Font.ITALIC, size);
            loadFont(DEFAULT, Font.BOLD | Font.ITALIC, size);
        }
    }

    public static void loadFontFromJar(String path, String fontName, int style, int size) {
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream(path);
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont = awtFont.deriveFont((float) size).deriveFont(style);
            TrueTypeFont font = new TrueTypeFont(awtFont, true);
            fontMap.put(fontName + "_" + style + "_" + size, font);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TrueTypeFont loadFont(String fontName, int style, int size) {
        if (getFontButNull(fontName, style, size) == null) {
            Font winFont = new Font(fontName, style, size);
            TrueTypeFont font = new TrueTypeFont(winFont, true);
            fontMap.put(fontName + "_" + style + "_" + size, font);
            return fontMap.get(fontName + "_" + style + "_" + size);
        } else {
            System.out.println("ERROR: Font '" + fontName + "' ('" + style + "', " + size + ") is already registered");
            return null;
        }
    }

    public static TrueTypeFont getFont(String fontName, int style, int size) {
        TrueTypeFont font = fontMap.get(fontName + "_" + style + "_" + size);
        return font != null ? font : standardFont;
    }

    private static TrueTypeFont getFontButNull(String fontName, int style, int size) {
        return fontMap.get(fontName + "_" + style + "_" + size);
    }

    public static TrueTypeFont getStandardFont() {
        return getStandardFont(Font.PLAIN);
    }

    public static TrueTypeFont getStandardFont(int style) {
        TrueTypeFont font = getFont(DEFAULT, style, MINIMUM_DEFAULT_SIZE);
        return ((font != null) ? font : standardFont);
    }

    public static TrueTypeFont getStandardFontSized(int size) {
        TrueTypeFont font = getFont(DEFAULT, Font.PLAIN, size);
        return ((font != null) ? font : standardFont);
    }

    public static TrueTypeFont getStandardFont(int size, int style) {
        TrueTypeFont font = getFont(DEFAULT, style, size);
        return ((font != null) ? font : standardFont);
    }
}
