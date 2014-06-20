package de.gemo.gameengine.manager;

import static org.lwjgl.opengl.GL11.GL_NEAREST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.gemo.gameengine.textures.MultiTexture;
import de.gemo.gameengine.textures.SingleTexture;

public class TextureManager {
    /**
     * the texturemap
     */
    private static HashMap<String, MultiTexture> textureMap = new HashMap<String, MultiTexture>();

    /**
     * Private method to get the filextension of a file. Used when loading a
     * texture.
     * 
     * @param file
     * @return the extension
     */
    public static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Load a texture from a given path. GL_NEAREST is used as the
     * interpolationfilter by default.
     * 
     * @param path
     *            - path to the file
     * @return A <b>SingleTexture</b>
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static SingleTexture loadSingleTexture(String path) throws FileNotFoundException, IOException {
        return loadSingleTexture(path, GL_NEAREST);
    }

    /**
     * Load a texture from a given path with the given interpolationfilter.
     * 
     * @param path
     *            - path to the file
     * @param filter
     *            - interpolationfilter
     * @return A <b>SingleTexture</b>
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static SingleTexture loadSingleTexture(String path, int filter) throws FileNotFoundException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("ERROR: Texture '" + path + "' does not exist!");
            return null;
        }
        Texture texture = TextureLoader.getTexture(getExtension(file).toUpperCase(), new FileInputStream(file), false, filter);
        return new SingleTexture(texture, 0, 0, texture.getImageWidth() - 1, texture.getImageHeight() - 1);
    }

    /**
     * Load a texture from a given path. This will return a SingleTexture with
     * calculated UV-Coordinates for correct rendering. GL_NEAREST is used as
     * the interpolationfilter by default.
     * 
     * @param path
     *            - path to the file
     * @param x
     *            - x-offset
     * @param y
     *            - y-offset
     * @param width
     *            - width of the wanted texture
     * @param height
     *            - height of the wanted texture
     * @return A <b>SingleTexture</b> with calculated UV-coordinates.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static SingleTexture loadSingleTexture(String path, float x, float y, float width, float height) throws FileNotFoundException, IOException {
        return loadSingleTexture(path, x, y, width, height, GL_NEAREST);
    }

    /**
     * Load a texture from a given path. This will return a SingleTexture with
     * calculated UV-Coordinates for correct rendering. GL_NEAREST is used as
     * the interpolationfilter by default.
     * 
     * @param path
     *            - path to the file
     * @param x
     *            - x-offset
     * @param y
     *            - y-offset
     * @param width
     *            - width of the wanted texture
     * @param height
     *            - height of the wanted texture * @param filter -
     *            interpolationfilter
     * @return A <b>SingleTexture</b> with calculated UV-coordinates.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static SingleTexture loadSingleTexture(String path, float x, float y, float width, float height, int filter) throws FileNotFoundException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("ERROR: Texture '" + path + "' does not exist!");
            return null;
        }
        Texture texture = TextureLoader.getTexture(getExtension(file).toUpperCase(), new FileInputStream(file), filter);
        return new SingleTexture(texture, x, y, width, height);
    }

    /**
     * Create a MultiTexture, based on a SingleTexture.
     * 
     * @param singleTexture
     *            - SingleTexture
     * @return A <b>MultiTexture</b>
     */
    public static MultiTexture SingleToMultiTexture(SingleTexture singleTexture) {
        return new MultiTexture(singleTexture.getWidth(), singleTexture.getHeight(), singleTexture);
    }

    /**
     * Add a MultiTexture to the texturemap, only if the name is unused.
     * 
     * @param name
     *            - the identifier of the texture
     * @param multiTexture
     *            - the texture
     * @return <b>true</b> if there was no texture with this name, otherwise
     *         <b>false</b>
     */
    public static boolean addTexture(String name, MultiTexture multiTexture) {
        name = name.toLowerCase();
        // if (textureMap.containsKey(name)) {
        // System.out.println("WARNING: Texture '" + name +
        // "' is already registered!");
        // return false;
        // }
        textureMap.put(name, multiTexture);
        return true;
    }

    /**
     * Remove a texture from the texturemap.
     * 
     * @param name
     *            - the identifier of the texture
     * @return <b>true</b> if the texture was present and is now removed,
     *         otherwise <b>false</b>
     */
    public static boolean removeTexture(String name) {
        name = name.toLowerCase();
        if (!textureMap.containsKey(name)) {
            System.out.println("WARNING: Texture '" + name + "' is not registered!");
            return false;
        }
        textureMap.remove(name);
        return true;
    }

    /**
     * Get a certain MultiTexture.
     * 
     * @param name
     *            - the identifier of the texture you want to get
     * @return A <b MultiTexture</b> if there is a texture with this name,
     *         otherwise <b>null</b>
     */
    public static MultiTexture getTexture(String name) {
        name = name.toLowerCase();
        if (!textureMap.containsKey(name)) {
            System.out.println("WARNING: Texture '" + name + "' is not registered!");
            return null;
        }
        return textureMap.get(name);
    }
}
