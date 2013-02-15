package de.gemo.engine.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.animation.SingleTexture;

import static org.lwjgl.opengl.GL11.*;

public class TextureManager {
    private static HashMap<String, MultiTexture> textureMap = new HashMap<String, MultiTexture>();

    public static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static SingleTexture loadSingleTexture(String path) throws FileNotFoundException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("ERROR: Texture '" + path + "' does not exist!");
            return null;
        }
        Texture texture = TextureLoader.getTexture(getExtension(file).toUpperCase(), new FileInputStream(file), false, GL_NEAREST);
        return new SingleTexture(texture, 0, 0, texture.getImageWidth(), texture.getImageHeight());
    }

    public static MultiTexture SingleToMultiTexture(SingleTexture singleTexture) {
        return new MultiTexture(singleTexture.getWidth(), singleTexture.getHeight(), singleTexture);
    }

    public static SingleTexture loadSingleTexture(String path, float x, float y, float width, float height) throws FileNotFoundException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("ERROR: Texture '" + path + "' does not exist!");
            return null;
        }
        Texture texture = TextureLoader.getTexture(getExtension(file).toUpperCase(), new FileInputStream(file));
        return new SingleTexture(texture, x, y, width, height);
    }

    public static boolean addTexture(String name, MultiTexture multiTexture) {
        name = name.toLowerCase();
        if (textureMap.containsKey(name)) {
            System.out.println("WARNING: Texture '" + name + "' is already registered!");
            return false;
        }
        textureMap.put(name, multiTexture);
        return true;
    }

    public static boolean removeTexture(String name) {
        name = name.toLowerCase();
        if (!textureMap.containsKey(name)) {
            System.out.println("WARNING: Texture '" + name + "' is not registered!");
            return false;
        }
        textureMap.remove(name);
        return true;
    }

    public static MultiTexture getTexture(String name) {
        name = name.toLowerCase();
        if (!textureMap.containsKey(name)) {
            System.out.println("WARNING: Texture '" + name + "' is not registered!");
            return null;
        }
        return textureMap.get(name);
    }
}
