package de.gemo.game.physics.gui.statics;

import de.gemo.gameengine.units.XMLTree;

public class GUIXML {

    private static XMLTree xmlTree = null;
    public static float PIC_SIZE = 1024f;

    public static void load(String fileName) {
        GUIXML.xmlTree = new XMLTree(fileName);
        if (xmlTree.getRootNode() != null) {
            String picSize = xmlTree.getRootNode().getAttributeValue("PicSize");
            try {
                GUIXML.PIC_SIZE = Integer.valueOf(picSize);
            } catch (Exception e) {
                System.out.println("WARNING: PicSize is invalid!");
            }
        }
    }

    public static String getString(String path) {
        return GUIXML.xmlTree.getString(path);
    }

    public static int getInt(String path) {
        return GUIXML.xmlTree.getInt(path);
    }

    public static int getInt(String path, int defaultValue) {
        return GUIXML.xmlTree.getInt(path, defaultValue);
    }
}
