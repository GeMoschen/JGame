package de.gemo.game.core;

import java.awt.Font;
import java.util.HashMap;

import org.newdawn.slick.TrueTypeFont;

public class FontManager {

    public static final String DIN = "DIN Schablonierschrift";
    public static final String VERDANA = "Verdana";

    private static TrueTypeFont standardFont;
    private static HashMap<String, TrueTypeFont> fontMap;

    static {
        fontMap = new HashMap<String, TrueTypeFont>();
        Font standard = new Font(VERDANA, Font.BOLD, 12);
        standardFont = new TrueTypeFont(standard, true);
    }

    public static void loadFont(String fontName, int style, int size) {
        Font winFont = new Font(fontName, style, size);
        TrueTypeFont font = new TrueTypeFont(winFont, true);
        fontMap.put(fontName + "_" + style + "_" + size, font);
    }

    public static TrueTypeFont getFont(String fontName, int style, int size) {
        TrueTypeFont font = fontMap.get(fontName + "_" + style + "_" + size);
        return font != null ? font : standardFont;
    }

    public static TrueTypeFont getStandardFont() {
        return standardFont;
    }
}
